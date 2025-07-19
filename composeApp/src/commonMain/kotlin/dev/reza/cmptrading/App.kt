package dev.reza.cmptrading

import androidx.compose.runtime.Composable
import dev.reza.cmptrading.coins.presentation.CoinsListScreen
import dev.reza.cmptrading.theme.CMPTradingTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    CMPTradingTheme {
        CoinsListScreen {   }
    }

}