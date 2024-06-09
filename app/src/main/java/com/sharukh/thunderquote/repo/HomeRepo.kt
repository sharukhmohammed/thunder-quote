package com.sharukh.thunderquote.repo

import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.app.App
import com.sharukh.thunderquote.app.AppModule
import com.sharukh.thunderquote.model.Quote
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import kotlin.text.Typography.quote

class HomeRepo {
    private val json
        get() = AppModule.json

    @OptIn(ExperimentalSerializationApi::class)
    private fun readQuotes(): List<Quote> {
        val quotesInputStream = App.context.resources.openRawResource(R.raw.quotes)
        return json.decodeFromStream<List<Quote>>(quotesInputStream)
    }

    fun getAllQuotes(): List<Quote> {
        return readQuotes()
            .onEachIndexed { index, quote -> quote.id = index  }
            .shuffled()
    }
}