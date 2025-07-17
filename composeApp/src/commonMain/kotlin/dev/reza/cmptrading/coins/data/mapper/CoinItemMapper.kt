package dev.reza.cmptrading.coins.data.mapper

import dev.reza.cmptrading.coins.data.remote.dto.CoinItemDto
import dev.reza.cmptrading.coins.data.remote.dto.CoinPriceDto
import dev.reza.cmptrading.coins.domain.model.CoinModel
import dev.reza.cmptrading.coins.domain.model.PriceModel
import dev.reza.cmptrading.core.domain.coin.Coin

fun CoinItemDto.toCoinModel() = CoinModel(
    coin = Coin(id = uuid, symbol = symbol, name = name, iconUrl = iconUrl),
    price = price,
    change = change
)

fun CoinPriceDto.toPriceModel() = PriceModel(
    price = price ?: 0.0,
    timestamp = timestamp
)