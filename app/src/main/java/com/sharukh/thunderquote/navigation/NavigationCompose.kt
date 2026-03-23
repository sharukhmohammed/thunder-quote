package com.sharukh.thunderquote.navigation

import kotlinx.serialization.Serializable

@Serializable
abstract class Screen {
    @Serializable
    data object QuoteList : Screen()

    @Serializable
    data object QuoteFavorites : Screen()

    @Serializable
    data object QuoteDetail : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object AiChat : Screen()
}
