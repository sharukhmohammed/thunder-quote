package com.sharukh.thunderquote.navigation

import kotlinx.serialization.Serializable

abstract class Screen {
    @Serializable
    data class QuoteList(val onlyFav: Boolean) : Screen()

    @Serializable
    data object QuoteDetail : Screen()

    @Serializable
    data object Settings : Screen()

}
