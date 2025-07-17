package dev.reza.cmptrading.coins.domain.usecase

import dev.reza.cmptrading.coins.data.mapper.toCoinModel
import dev.reza.cmptrading.coins.domain.api.CoinsRemoteDataSource
import dev.reza.cmptrading.coins.domain.model.CoinModel
import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.domain.map

class GetCoinsListUseCase(
    private val client: CoinsRemoteDataSource
) {

    suspend fun execute(): Result<List<CoinModel>, DataError.Remote> {
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map { it.toCoinModel() }
        }
    }

}