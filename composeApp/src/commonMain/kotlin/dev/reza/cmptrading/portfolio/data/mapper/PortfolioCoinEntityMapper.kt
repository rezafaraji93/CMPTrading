package dev.reza.cmptrading.portfolio.data.mapper

import dev.reza.cmptrading.core.domain.coin.Coin
import dev.reza.cmptrading.portfolio.data.local.PortfolioCoinEntity
import dev.reza.cmptrading.portfolio.domain.PortfolioCoinModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double
): PortfolioCoinModel {
    return PortfolioCoinModel(
        coin = Coin(
            id = coinId,
            symbol = symbol,
            name = name,
            iconUrl = iconUrl
        ),
        performancePercent = ((currentPrice - averagePurchasePrice) / averagePurchasePrice) *100,
        averagePurchasePrice = averagePurchasePrice,
        ownedAmountInUnit = amountOwned,
        ownedAmountInFiat = amountOwned * currentPrice
    )
}

@OptIn(ExperimentalTime::class)
fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity {
    return PortfolioCoinEntity(
        coinId = coin.id,
        symbol = coin.symbol,
        name = coin.name,
        iconUrl = coin.iconUrl,
        averagePurchasePrice = averagePurchasePrice,
        amountOwned = ownedAmountInUnit,
        timestamp = Clock.System.now().toEpochMilliseconds()
    )
}