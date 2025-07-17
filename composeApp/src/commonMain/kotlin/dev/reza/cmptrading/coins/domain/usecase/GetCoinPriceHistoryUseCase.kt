package dev.reza.cmptrading.coins.domain.usecase

import dev.reza.cmptrading.coins.data.mapper.toPriceModel
import dev.reza.cmptrading.coins.domain.api.CoinsRemoteDataSource
import dev.reza.cmptrading.coins.domain.model.PriceModel
import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.domain.map

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource
) {

    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getPriceHistory(coinId).map { dto ->
            dto.data.history.map { it.toPriceModel() }
        }
    }
}