package sharukh.thunderquote.modules.main

import android.app.Application
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sharukh.thunderquote.base.App
import sharukh.thunderquote.data.Quote
import sharukh.thunderquote.data.QuoteRepository

class QuoteModel {

    private val quoteRepository = QuoteRepository(App.context as Application)

    fun getQuote(): Observable<Quote> {
        return quoteRepository.getRandomQuoteServer()
    }

    fun toggleLike(id: Long, statusCallback: (Boolean) -> Unit) {
        GlobalScope.launch {
            val quote = quoteRepository.getQuote(id).await()
            quote.isFavorite = !quote.isFavorite
            statusCallback(quote.isFavorite)
            quoteRepository.updateQuote(quote).await()
        }
    }

    fun getQuoteDb(id: Long): Deferred<Quote> {
        return quoteRepository.getQuote(id)
    }

}