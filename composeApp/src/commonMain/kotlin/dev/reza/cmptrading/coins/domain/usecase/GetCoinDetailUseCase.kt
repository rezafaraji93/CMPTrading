package dev.reza.cmptrading.coins.domain.usecase

import dev.reza.cmptrading.coins.data.mapper.toCoinModel
import dev.reza.cmptrading.coins.domain.api.CoinsRemoteDataSource
import dev.reza.cmptrading.coins.domain.model.CoinModel
import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.domain.map

class GetCoinDetailUseCase(
    private val client: CoinsRemoteDataSource
) {

    suspend fun execute(coinId: String): Result<CoinModel, DataError.Remote> {
        return client.getCoinById(coinId).map { dto ->
            dto.data.coin.toCoinModel()
        }
    }
}