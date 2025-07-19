package dev.reza.cmptrading

import androidx.compose.ui.window.ComposeUIViewController
import dev.reza.cmptrading.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}