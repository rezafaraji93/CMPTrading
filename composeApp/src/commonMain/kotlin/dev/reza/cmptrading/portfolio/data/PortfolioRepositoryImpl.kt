package dev.reza.cmptrading.portfolio.data

import androidx.sqlite.SQLiteException
import dev.reza.cmptrading.coins.domain.api.CoinsRemoteDataSource
import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.EmptyResult
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.domain.onError
import dev.reza.cmptrading.core.domain.onSuccess
import dev.reza.cmptrading.portfolio.data.local.BalanceDao
import dev.reza.cmptrading.portfolio.data.local.PortfolioDao
import dev.reza.cmptrading.portfolio.data.mapper.toPortfolioCoinEntity
import dev.reza.cmptrading.portfolio.data.mapper.toPortfolioCoinModel
import dev.reza.cmptrading.portfolio.domain.PortfolioCoinModel
import dev.reza.cmptrading.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: BalanceDao,
    private val coinsRemoteDataSource: CoinsRemoteDataSource
) : PortfolioRepository {

    override suspend fun initializeBalance() {
        val currentBalance = userBalanceDao.getCashBalance()
        if (currentBalance != null) {
            userBalanceDao.updateCashBalance(10_000.0)
        }
    }

    override fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return portfolioDao.getPortfolioCoins().flatMapLatest { portfolioCoinsEntities ->
            if (portfolioCoinsEntities.isEmpty()) {
                flow {
                    emit(Result.Success(emptyList()))
                }
            } else {
                flow {
                    coinsRemoteDataSource.getListOfCoins().onError { error ->
                        emit(Result.Error(error))
                    }.onSuccess { coinsDto ->
                        val portfolioCoins =
                            portfolioCoinsEntities.mapNotNull { portfolioCoinEntity ->
                                val coin =
                                    coinsDto.data.coins.find { it.uuid == portfolioCoinEntity.coinId }
                                coin?.let {
                                    portfolioCoinEntity.toPortfolioCoinModel(it.price)
                                }
                            }
                        emit(Result.Success(portfolioCoins))
                    }
                }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId).onError { error ->
            return Result.Error(error)
        }.onSuccess { coinDto ->
            val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
            return if (portfolioCoinEntity != null) {
                Result.Success(portfolioCoinEntity.toPortfolioCoinModel(coinDto.data.coin.price))
            } else {
                Result.Success(null)
            }
        }
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        try {
            portfolioDao.insert(portfolioCoin.toPortfolioCoinEntity())
            return Result.Success(Unit)
        } catch (e: SQLiteException) {
            return Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deleteFromPortfolio(coinId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return portfolioDao.getPortfolioCoins().flatMapLatest { portfolioCoinsEntities ->
            if (portfolioCoinsEntities.isEmpty()) {
                flow {
                    emit(Result.Success(0.0))
                }
            } else {
                flow {
                    val apiResult = coinsRemoteDataSource.getListOfCoins()
                    apiResult.onError { error ->
                            emit(Result.Error(error))
                        }.onSuccess { coinsDto ->
                            val totalValue = portfolioCoinsEntities.sumOf { ownedCoin ->
                                val coinPrice =
                                    coinsDto.data.coins.find { it.uuid == ownedCoin.coinId }?.price
                                        ?: 0.0
                                ownedCoin.amountOwned * coinPrice
                            }
                            emit(Result.Success(totalValue))
                        }
                }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
        return combine(
            cashBalanceFLow(), calculateTotalPortfolioValue()
        ) { cashBalance, portfolioResult ->
            when (portfolioResult) {
                is Result.Error -> {
                    Result.Error(portfolioResult.error)
                }

                is Result.Success -> {
                    Result.Success(cashBalance + portfolioResult.data)
                }
            }
        }
    }

    override fun cashBalanceFLow(): Flow<Double> {
        return flow {
            emit(userBalanceDao.getCashBalance() ?: 10000.0)
        }
    }

    override suspend fun updateCashBalance(newBalance: Double) {
        userBalanceDao.updateCashBalance(newBalance)
    }
}