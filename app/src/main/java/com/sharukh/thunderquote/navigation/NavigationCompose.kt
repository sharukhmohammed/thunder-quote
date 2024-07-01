package com.sharukh.thunderquote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

object Screen {
    @Serializable
    object QuoteList

    @Serializable
    object QuoteDetail

    @Serializable
    object Settings

    @Composable
    fun AppNavHostHost(navController: NavHostController) {

    }

}
