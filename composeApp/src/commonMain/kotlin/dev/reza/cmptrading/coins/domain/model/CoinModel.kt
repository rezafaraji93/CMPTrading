package dev.reza.cmptrading.coins.domain.model

import dev.reza.cmptrading.core.domain.coin.Coin

data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double
)