package dev.reza.cmptrading.portfolio.domain

import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.EmptyResult
import dev.reza.cmptrading.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    suspend fun initializeBalance()
    fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>>
    suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote>
    suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local>
    suspend fun removeCoinFromPortfolio(coinId: String)

    fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>>
    fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>>
    fun cashBalanceFLow(): Flow<Double>
    suspend fun updateCashBalance(newBalance: Double)

}