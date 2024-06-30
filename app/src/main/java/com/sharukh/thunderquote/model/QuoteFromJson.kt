package com.sharukh.thunderquote.model

import kotlinx.serialization.Serializable

@Serializable
class QuoteFromJson(
    val quote: String,
    val author: String
)
