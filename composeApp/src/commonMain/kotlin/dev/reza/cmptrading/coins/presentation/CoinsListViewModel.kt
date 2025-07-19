package dev.reza.cmptrading.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.reza.cmptrading.coins.domain.usecase.GetCoinPriceHistoryUseCase
import dev.reza.cmptrading.coins.domain.usecase.GetCoinsListUseCase
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.util.formatFiat
import dev.reza.cmptrading.core.util.formatPercentage
import dev.reza.cmptrading.core.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state.onStart { getAllCoins() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CoinsState()
    )

    private suspend fun getAllCoins() {
        when (val coinsResponse = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                _state.update {
                    CoinsState(
                        coins = coinsResponse.data.map { coinItem ->
                            UiCoinListItem(
                                id = coinItem.coin.id,
                                symbol = coinItem.coin.symbol,
                                name = coinItem.coin.name,
                                iconUrl = coinItem.coin.iconUrl,
                                formattedPrice = formatFiat(coinItem.price),
                                formattedChange = formatPercentage(coinItem.change),
                                isPositive = coinItem.change >= 0
                            )
                        })
                }
            }

            is Result.Error -> {
                _state.update {
                    it.copy(
                        coins = emptyList(), error = coinsResponse.error.toUiText()
                    )
                }

            }
        }
    }

    fun onCoinLongPress(coinId: String) {
        _state.update {
            it.copy(
                uiChartState = UiChartState(
                    isLoading = true, sparkLine = emptyList()
                )
            )
        }

        viewModelScope.launch {
            when (val priceHistoryResponse = getCoinPriceHistoryUseCase.execute(coinId)) {
                is Result.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            uiChartState = UiChartState(
                                isLoading = false,
                                sparkLine = priceHistoryResponse.data.sortedBy { it.timestamp }
                                    .map { it.price },
                                coinName = currentState.coins.find { it.id == coinId }?.name.orEmpty()
                            )
                        )
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            uiChartState = UiChartState(
                                isLoading = false, sparkLine = emptyList(), coinName = ""
                            ), error = priceHistoryResponse.error.toUiText()
                        )
                    }
                }
            }
        }
    }

    fun dismissChart() {
        _state.update {
            it.copy(
                uiChartState = null
            )
        }
    }

}