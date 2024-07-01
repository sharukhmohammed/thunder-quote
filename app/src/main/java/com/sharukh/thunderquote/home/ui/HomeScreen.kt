@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.home.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.model.QuoteDummies
import com.sharukh.thunderquote.navigation.Screen
import com.sharukh.thunderquote.navigation.Screen.QuoteDetail
import com.sharukh.thunderquote.navigation.Screen.QuoteList
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreenActivity(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.QuoteList) {
        composable<QuoteList> { backStackEntry ->
            val actions = object : HomeScreenActions {
                override fun onClickRefresh() {
                    viewModel.refresh()
                }

                override fun onClickMore() {
                    TODO("Not yet implemented")
                }

                override fun onClickFavorite() {
                    TODO("Not yet implemented")
                }

                override fun onClickShare() {
                    TODO("Not yet implemented")
                }
            }
            HomeScreen(state, actions)
        }
        composable<QuoteDetail> { backStackEntry ->
            val profile: QuoteDetail = backStackEntry.toRoute()
//            ProfileScreen(profile.id)
        }
    }

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
                quote = quote, modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun QuoteListItem(modifier: Modifier, quote: Quote) {
    Card(
        modifier = modifier.fillMaxWidth()
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
                Text(text = quote.author, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.weight(1F))
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = if (quote.isFavorite)
                            Icons.Rounded.Favorite
                        else
                            Icons.Rounded.FavoriteBorder,
                        contentDescription = stringResource(
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
        QuoteListItem(modifier = Modifier, quote = QuoteDummies.display)
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    HomeScreen(
        HomeViewModel.State(
            quotes = flowOf(
                PagingData.from(
                    data = listOf(
                        QuoteDummies.display, QuoteDummies.display2
                    ), sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(false),
                        prepend = LoadState.NotLoading(false),
                    )
                )
            ),
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    HomeScreen(
        HomeViewModel.State(
            quotes = emptyFlow()
        )
    )
}