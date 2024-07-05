@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.navigation.Screen
import com.sharukh.thunderquote.ui.base.AppBottomAppBar
import com.sharukh.thunderquote.ui.base.AppTopAppBar
import com.sharukh.thunderquote.ui.quote.QuoteActions
import com.sharukh.thunderquote.ui.quote.QuoteDisplayScreen
import com.sharukh.thunderquote.ui.quote.QuoteDisplayScreenAction
import com.sharukh.thunderquote.ui.quote.QuoteListScreen
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme

@Composable
fun HomeScreenParent(viewModel: HomeViewModel) {
    val listState by viewModel.listState.collectAsStateWithLifecycle()
    val favState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val dailyState by viewModel.randomQuoteState.collectAsStateWithLifecycle()

    val actionHandler = object : QuoteActions {
        override fun onFavorite(quote: Quote) = viewModel.toggleFavorite(quote)
        override fun onShare(quote: Quote) = viewModel.getRandom()
    }

    val navController = rememberNavController()
    ThunderQuoteTheme {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            topBar = {
                AppTopAppBar(stringResource(id = R.string.app_name), scrollBehavior)
            },
            bottomBar = {
                AppBottomAppBar(false) {
                    navController.navigate(it)
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        // action?.onClickRefresh()
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
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.QuoteDetail
            ) {
                composable<Screen.QuoteList> {
                    QuoteListScreen(innerPadding, listState, actionHandler)
                }
                composable<Screen.QuoteFavorites> {
                    QuoteListScreen(innerPadding, favState, actionHandler)
                }
                composable<Screen.QuoteDetail> {
                    QuoteDisplayScreen(dailyState) { action ->
                        when (action) {
                            is QuoteDisplayScreenAction.Refresh -> viewModel.getRandom()
                            is QuoteDisplayScreenAction.Favorite -> viewModel.toggleFavorite(action.quote)
                        }
                    }
                }
                composable<Screen.Settings> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Settings")
                    }
                }
            }
        }
    }

}

//@Composable
//private fun HomeScreen(
//    navController: NavHostController,
//    quoteListState: QuoteListState,
//    favoritesState: QuoteListState,
//    quoteDailyState: QuoteItemState,
//    goToScreen: (Screen) -> Unit
//) {
//
//}
//
//@Preview
//@Composable
//private fun HomeScreenPreview() {
//    HomeScreen(
//        navController = rememberNavController(),
//        quoteListState = QuoteListState(),
//        favoritesState = QuoteListState(),
//        quoteDailyState = QuoteItemState(),
//    ) {
//
//    }
//}