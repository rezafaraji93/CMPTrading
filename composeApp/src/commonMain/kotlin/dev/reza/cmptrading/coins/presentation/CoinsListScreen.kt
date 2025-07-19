package dev.reza.cmptrading.coins.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.reza.cmptrading.coins.presentation.component.PerformanceChart
import dev.reza.cmptrading.theme.LocalCMPTradingColorsPalette
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit
) {

    val coinsListViewModel = koinViewModel<CoinsListViewModel>()
    val state by coinsListViewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onCoinClicked = onCoinClicked,
        onDismissChart = { coinsListViewModel.dismissChart() },
        onCoinLongPressed = { coinsListViewModel.onCoinLongPress(it) }
    )

}

@Composable
fun CoinsListContent(
    state: CoinsState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {

        if (state.uiChartState != null) {
            CoinChartDialog(uiChartState = state.uiChartState, onDismiss = onDismissChart)
        }

        CoinsList(
            coins = state.coins,
            onCoinLongPressed = onCoinLongPressed,
            onCoinClicked = onCoinClicked
        )

    }

}

@Composable
fun CoinsList(
    coins: List<UiCoinListItem>,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "\uD83D\uDD25 Top Coins:",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(coins) { coin ->
                CoinListItem(
                    coin = coin,
                    onCoinLongPressed = onCoinLongPressed,
                    onCoinClicked = onCoinClicked
                )
            }
        }

    }

}

@Composable
private fun CoinListItem(
    modifier: Modifier = Modifier,
    coin: UiCoinListItem,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().combinedClickable(enabled = true, onLongClick = {
                onCoinLongPressed(coin.id)
            }, onClick = {
                onCoinClicked(coin.id)
            }).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = Modifier.padding(4.dp).clip(CircleShape).size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = coin.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Text(
                text = coin.symbol,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.End
        ) {
            Text(
                text = coin.formattedPrice,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Text(
                text = coin.formattedChange,
                color = if (coin.isPositive) LocalCMPTradingColorsPalette.current.profitGreen else LocalCMPTradingColorsPalette.current.lossRed,
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
        }

    }
}


@Composable
fun CoinChartDialog(
    uiChartState: UiChartState,
    onDismiss: () -> Unit,
) {
    AlertDialog(modifier = Modifier.fillMaxWidth(), onDismissRequest = onDismiss, title = {
        Text(
            text = "24h Price chart for ${uiChartState.coinName}",
        )
    }, text = {
        if (uiChartState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else {
            PerformanceChart(
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                nodes = uiChartState.sparkLine,
                profitColor = LocalCMPTradingColorsPalette.current.profitGreen,
                lossColor = LocalCMPTradingColorsPalette.current.lossRed,
            )
        }
    }, confirmButton = {}, dismissButton = {
        Button(
            onClick = onDismiss
        ) {
            Text(
                text = "Close",
            )
        }
    })

}