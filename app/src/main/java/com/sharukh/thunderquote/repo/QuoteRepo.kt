package com.sharukh.thunderquote.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.app.App
import com.sharukh.thunderquote.app.AppModule
import com.sharukh.thunderquote.di.ServiceLocator
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.model.QuoteFromJson
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

class QuoteRepo {
    private val json
        get() = AppModule.json

    private val db = ServiceLocator.appDatabase()

    @OptIn(ExperimentalSerializationApi::class)
    private fun readQuotes(): List<QuoteFromJson> {
        val quotesInputStream = App.context.resources.openRawResource(R.raw.quotes)
        return json.decodeFromStream<List<QuoteFromJson>>(quotesInputStream)
    }

    /*private fun getAllQuotes(): List<Quote> {
        return readQuotes()
            .mapIndexed { index, quote -> Quote(index, quote.quote, quote.author) }
    }

    suspend fun insertAllQuotes() {
        val qEs = getAllQuotes().map { Quote(it.id, it.quote, it.author, false) }
        db.quoteDao().insertAll(*qEs.toTypedArray())
    }*/

    fun quoteByPaging(): Flow<PagingData<Quote>> {
        return Pager(
            PagingConfig(10, 10),
        ) {
            db.quoteDao().getAll()
        }.flow
    }

}