package dev.reza.cmptrading.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.reza.cmptrading.coins.domain.usecase.GetCoinsListUseCase
import dev.reza.cmptrading.core.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase
): ViewModel()  {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart { getAllCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState()
        )

    private suspend fun getAllCoins() {
        when(val coinsResponse  = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                _state.update {
                    CoinsState(
                        coins = coinsResponse.data.map { coinItem ->
                            UiCoinListItem(
                                id = coinItem.coin.id,
                                symbol = coinItem.coin.symbol,
                                name = coinItem.coin.name,
                                iconUrl = coinItem.coin.iconUrl,
                                formattedPrice = coinItem.price.toString(),
                                formattedChange = coinItem.change.toString(),
                                isPositive = coinItem.change >= 0
                            )
                        }
                    )
                }
            }
            is Result.Error -> {
                _state.update {
                    CoinsState(
                        error = null // TODO
                    )
                }

            }
        }
    }

}