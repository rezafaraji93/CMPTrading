package dev.reza.cmptrading.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.reza.cmptrading.core.domain.DataError
import dev.reza.cmptrading.core.domain.Result
import dev.reza.cmptrading.core.util.formatCoinUnit
import dev.reza.cmptrading.core.util.formatFiat
import dev.reza.cmptrading.core.util.formatPercentage
import dev.reza.cmptrading.core.util.toUiText
import dev.reza.cmptrading.portfolio.domain.PortfolioCoinModel
import dev.reza.cmptrading.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = combine(
        _state,
        portfolioRepository.getPortfolioCoinsFlow(),
        portfolioRepository.totalBalanceFlow(),
        portfolioRepository.cashBalanceFLow()
    ) { currentState, portfolioCoinsResponse, totalBalanceResult, cashBalance ->
        when (portfolioCoinsResponse) {
            is Result.Error -> {
                handleErrorState(
                    currentState = currentState,
                    error = portfolioCoinsResponse.error
                )
            }

            is Result.Success -> {
                handleSuccessState(
                    currentState = currentState,
                    portfolioCoins = portfolioCoinsResponse.data,
                    totalBalanceResult = totalBalanceResult,
                    cashBalance = cashBalance
                )
            }
        }

    }.onStart {
        portfolioRepository.initializeBalance()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PortfolioState(isLoading = true)
    )


    private fun handleSuccessState(
        currentState: PortfolioState,
        portfolioCoins: List<PortfolioCoinModel>,
        totalBalanceResult: Result<Double, DataError>,
        cashBalance: Double
    ): PortfolioState {

        val portfolioValue = when (totalBalanceResult) {
            is Result.Error -> {
                formatFiat(0.0)
            }

            is Result.Success -> {
                formatFiat(totalBalanceResult.data)
            }
        }

        return currentState.copy(
            coins = portfolioCoins.map { it.toUiPortfolioCoinItem() },
            portfolioValue = portfolioValue,
            cashBalance = formatFiat(cashBalance),
            showBuyButton = portfolioCoins.isNotEmpty(),
            isLoading = false
        )
    }

    private fun handleErrorState(
        currentState: PortfolioState,
        error: DataError
    ): PortfolioState {
        return currentState.copy(
            isLoading = false,
            error = error.toUiText()
        )
    }

    private fun PortfolioCoinModel.toUiPortfolioCoinItem(): UiPortfolioCoinItem {
        return UiPortfolioCoinItem(
            id = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            amountInUnitText = formatCoinUnit(amount = ownedAmountInUnit, coin.symbol),
            amountInFiatText = formatFiat(amount = ownedAmountInUnit),
            performancePercentText = formatPercentage(performancePercent),
            isPositive = performancePercent >= 0
        )
    }

}