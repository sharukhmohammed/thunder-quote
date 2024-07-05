package com.sharukh.thunderquote.ui.quote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.model.QuoteDummies
import com.sharukh.thunderquote.ui.state.QuoteListState
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme

@Composable
fun QuoteListScreen(
    innerPadding: PaddingValues,
    state: QuoteListState,
    onAction: QuoteActions,
) {
    val quotesPages = state.quotes.collectAsLazyPagingItems()
    val isLoading = quotesPages.loadState.refresh is LoadState.Loading

    if (isLoading) Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
    else LazyColumn(
        modifier = Modifier.padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
    ) {
        items(quotesPages.itemCount,
            key = quotesPages.itemKey { it.id },
            contentType = { "Quote" }) { index ->
            val quote = quotesPages[index]
            if (quote != null) QuoteListItem(
                quote = quote,
                modifier = Modifier.animateItem(),
                onAction = onAction
            )
        }
    }
}

@Composable
private fun QuoteListItem(modifier: Modifier, quote: Quote, onAction: QuoteActions) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = quote.quote, style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = quote.author, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.weight(1F))
                IconButton(onClick = {
                    onAction.onFavorite(quote)
                }) {
                    Icon(
                        imageVector = if (quote.isFavorite) Icons.Rounded.Favorite
                        else Icons.Rounded.FavoriteBorder, contentDescription = stringResource(
                            id = R.string.favorite
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun QuoteListItemPreview() {
    ThunderQuoteTheme {
        QuoteListItem(modifier = Modifier, quote = QuoteDummies.display, object : QuoteActions {})
    }
}