package dev.reza.cmptrading.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CMPTradingColorsPalette(
    val profitGreen: Color = Color.Unspecified,
    val lossRed: Color = Color.Unspecified,
)

val ProfitGreenColor = Color(color = 0xFF32de84)
val LossRedColor = Color(color = 0xFFD2122E)

val DarkProfitGreenColor = Color(color = 0xFF32de84)
val DarkLossRedColor = Color(color = 0xFFD2122E)

val LightCoinRoutineColorsPalette = CMPTradingColorsPalette(
    profitGreen = ProfitGreenColor,
    lossRed = LossRedColor,
)

val DarkCoinRoutineColorsPalette = CMPTradingColorsPalette(
    profitGreen = DarkProfitGreenColor,
    lossRed = DarkLossRedColor,
)

val LocalCMPTradingColorsPalette = compositionLocalOf { CMPTradingColorsPalette() }