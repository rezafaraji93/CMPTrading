package dev.reza.cmptrading.coins.data.remote.impl

import dev.reza.cmptrading.coins.data.remote.dto.CoinDetailsResponseDto
import dev.reza.cmptrading.coins.data.remote.dto.CoinPriceHistoryResponseDto
import dev.reza.cmptrading.coins.data.remote.dto.CoinsResponseDto
import dev.reza.cmptrading.coins.domain.api.CoinsRemoteDataSource
import dev.reza.cmptrading.core.domain.DataError
import io.ktor.client.HttpClient
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.network.safeCall
import io.ktor.client.request.get

private const val BASE_URL = "https://api.coinranking.com/v2"

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient
) : CoinsRemoteDataSource {

    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coins")
        }
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId/history")
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId")
        }
    }
}