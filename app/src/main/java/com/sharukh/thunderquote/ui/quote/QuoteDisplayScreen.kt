package com.sharukh.thunderquote.ui.quote

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.model.QuoteDummies
import com.sharukh.thunderquote.ui.state.QuoteItemState
import com.sharukh.thunderquote.ui.theme.Size
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme

@Composable
fun QuoteDisplayScreen(innerPadding: PaddingValues, state: QuoteItemState, onAction: QuoteActions) {
    val isLoading = state.isLoading
    val quote = state.quote
    Scaffold(modifier = Modifier.padding(innerPadding), floatingActionButton = {
        FloatingActionButton(
            onClick = onAction::onRefresh,
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(
                    Icons.Rounded.Refresh,
                    contentDescription = stringResource(id = R.string.refresh)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Refresh")
            }
        }
    }) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(Size.screenPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading || quote == null -> CircularProgressIndicator()
                else -> {
                    Card {
                        Column(modifier = Modifier.padding(Size.dp16)) {
                            Text(
                                text = quote.quote,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.padding(Size.dp8))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = quote.author,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.weight(1F))
                                IconButton(onClick = { onAction.onFavorite(quote) }) {
                                    Icon(
                                        imageVector = if (quote.isFavorite) Icons.Rounded.Favorite
                                        else Icons.Rounded.FavoriteBorder,
                                        contentDescription = stringResource(id = R.string.favorite)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun QuoteDisplayScreenPreview() {
    ThunderQuoteTheme {
        QuoteDisplayScreen(PaddingValues(16.dp),
            state = QuoteItemState(quote = QuoteDummies.display2),
            object : QuoteActions {})
    }
}

@Preview
@Composable
private fun QuoteDisplayScreenPreviewEmpty() {
    ThunderQuoteTheme {
        QuoteDisplayScreen(
            PaddingValues(16.dp),
            state = QuoteItemState(quote = null),
            object : QuoteActions {})
    }
}