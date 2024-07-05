package com.sharukh.thunderquote.ui.quote

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.model.QuoteDummies
import com.sharukh.thunderquote.ui.state.QuoteItemState
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme

@Composable
fun QuoteDisplayScreen(state: QuoteItemState, onAction: (QuoteDisplayScreenAction) -> Unit) {
    val isLoading = state.isLoading
    val quote = state.quote
    ThunderQuoteTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAction(QuoteDisplayScreenAction.Refresh) }) {
                    Text(text = "Refresh")
                }
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading || quote == null -> CircularProgressIndicator()
                    else -> {
                        Card(
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = quote.quote,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = quote.author,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.weight(1F))
                                    IconButton(onClick = {}) {
                                        Icon(
                                            imageVector = if (quote.isFavorite) Icons.Rounded.Favorite
                                            else Icons.Rounded.FavoriteBorder,
                                            contentDescription = stringResource(
                                                id = R.string.favorite
                                            )
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

}

sealed class QuoteDisplayScreenAction {
    data object Refresh : QuoteDisplayScreenAction()
    data class Favorite(val quote: Quote) : QuoteDisplayScreenAction()
}

@Preview
@Composable
private fun QuoteDisplayScreenPreview() {
    ThunderQuoteTheme {
        QuoteDisplayScreen(
            state = QuoteItemState(quote = QuoteDummies.display2)
        ) {}
    }
}

@Preview
@Composable
private fun QuoteDisplayScreenPreviewEmpty() {
    ThunderQuoteTheme {
        QuoteDisplayScreen(
            state = QuoteItemState(quote = null)
        ) {}
    }
}