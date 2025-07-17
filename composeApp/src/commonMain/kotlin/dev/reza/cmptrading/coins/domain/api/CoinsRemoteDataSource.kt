package dev.reza.cmptrading.coins.domain.api

import dev.reza.cmptrading.coins.data.remote.dto.CoinDetailsResponseDto
import dev.reza.cmptrading.coins.data.remote.dto.CoinPriceHistoryResponseDto
import dev.reza.cmptrading.coins.data.remote.dto.CoinsResponseDto
import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.Result

interface CoinsRemoteDataSource {

    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>

    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>

    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}