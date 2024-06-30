package com.sharukh.thunderquote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey
    val id: Int,
    val quote: String,
    val author: String,
    val isFavorite: Boolean = false,
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

    val display2
        get() = Quote(
            2,
            "Character develops itself in the stream of life.",
            "Johann Wolfgang von Goethe"
        )
}