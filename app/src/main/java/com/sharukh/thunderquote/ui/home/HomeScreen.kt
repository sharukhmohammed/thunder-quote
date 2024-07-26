@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.navigation.Screen
import com.sharukh.thunderquote.notification.Notification
import com.sharukh.thunderquote.ui.base.AppBottomAppBar
import com.sharukh.thunderquote.ui.base.AppTopAppBar
import com.sharukh.thunderquote.ui.quote.QuoteActions
import com.sharukh.thunderquote.ui.quote.QuoteDisplayScreen
import com.sharukh.thunderquote.ui.quote.QuoteListScreen
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme

@Composable
fun HomeActivityScreen(viewModel: HomeViewModel) {
    val listState by viewModel.listState.collectAsStateWithLifecycle()
    val favState by viewModel.favoritesState.collectAsStateWithLifecycle()
    val dailyState by viewModel.randomQuoteState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val actionHandler = object : QuoteActions {
        override fun onRefresh() {
            viewModel.refresh();
        }

        override fun onFavorite(quote: Quote) {
            viewModel.toggleFavorite(quote);
            Notification.post(context, quote, Notification.Category.DailyQuotes)
        }
        override fun onShare(quote: Quote) = Unit
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
                    QuoteDisplayScreen(innerPadding, dailyState, actionHandler)
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