package com.sharukh.thunderquote.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    var id: Int = -1,
    val quote: String,
    val author: String
)

object QuoteDummies {
    val empty: Quote?
        get() = null

    val display
        get() = Quote(
            1,
            "Love doesn't make the world go round, love is what makes the ride worthwhile",
            "Elizabeth Browning"
        )
}