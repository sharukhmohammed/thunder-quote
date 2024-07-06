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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream

class QuoteRepo {

    private val json
        get() = AppModule.json

    private val db = ServiceLocator.appDatabase()
    private val dao = db.quoteDao()

    private var randomId: Int? = null

    @OptIn(ExperimentalSerializationApi::class)
    private fun readQuotes(): List<QuoteFromJson> {
        val quotesInputStream = App.context.resources.openRawResource(R.raw.quotes)
        return json.decodeFromStream<List<QuoteFromJson>>(quotesInputStream)
    }

    fun quoteByPaging(
        filterString: String,
        filterFavorite: Boolean
    ): Flow<PagingData<Quote>> {
        return Pager(
            PagingConfig(20, 15),
        ) {
            if (filterFavorite)
                dao.getFavorites()
            else
                dao.getAll()
        }.flow
    }

    suspend fun setFavorite(quote: Quote, favorite: Boolean) {
        dao.update(quote.copy(isFavorite = favorite))
    }

    private suspend fun randomId(): Int? {
        return dao.getRandomId()
    }

    fun getQuote(id: Int): Flow<Quote?> = dao.getQuote(id)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun randomQuote(refresh: Boolean = false): Flow<Quote?> = flow {
        if (randomId == null || refresh) {
            val id = dao.getRandomId()
            checkNotNull(id) { "DB Empty" }
            randomId = id
            emit(id)
        }
    }.flatMapConcat { id ->
        dao.getQuote(id)
    }

}