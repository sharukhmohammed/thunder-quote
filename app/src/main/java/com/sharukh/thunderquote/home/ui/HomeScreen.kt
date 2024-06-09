@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.home.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.model.QuoteDummies
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme

@Composable
fun HomeScreenActivity(viewModel: HomeViewModel, actions: HomeScreenActions? = null) {
    val state by viewModel.state.collectAsState()
    Log.i("HomeScreen", "Quotes: ${state.quotes.size}")

    HomeScreen(state, actions)
}

@Composable
private fun HomeScreen(state: HomeViewModel.State, action: HomeScreenActions? = null) {
    ThunderQuoteTheme {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(id = R.string.app_name))
                }, actions = {
                    IconButton(onClick = { action?.onClickMore() }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                }, scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomAppBar(actions = {
                    IconButton(onClick = {
                        action?.onClickFavorite()
                    }) {
                        Icon(
                            Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.favorite)
                        )
                    }
                    IconButton(onClick = {
                        action?.onClickShare()
                    }) {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = stringResource(id = R.string.share)
                        )
                    }
                }, floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            action?.onClickRefresh()
                        },
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
                })
            },
        ) { innerPadding ->
            HomeScreenContent(innerPadding, state)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(innerPadding: PaddingValues, state: HomeViewModel.State) {
    if (state.isLoading) Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
    else LazyColumn(
        modifier = Modifier.padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
    ) {
        items(state.quotes, key = { it.id }) { quote ->
            QuoteListItem(quote = quote, modifier = Modifier.animateItemPlacement())
        }
    }
}

@Composable
private fun QuoteListItem(modifier: Modifier, quote: Quote) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = quote.quote, style = MaterialTheme.typography.bodyLarge)
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = quote.author, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    HomeScreen(
        HomeViewModel.State(
            quote = QuoteDummies.display,
            quotes = listOf(QuoteDummies.display, QuoteDummies.display)
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    HomeScreen(
        HomeViewModel.State(
            quote = QuoteDummies.empty, quotes = emptyList()
        )
    )
}