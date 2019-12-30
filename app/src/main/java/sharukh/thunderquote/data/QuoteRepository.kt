package sharukh.thunderquote.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.Response
import sharukh.thunderquote.network.ForismaticAPI
import sharukh.thunderquote.network.ForismaticResponse
import sharukh.thunderquote.network.UnsplashAPI
import sharukh.thunderquote.network.UnsplashResponse

class QuoteRepository(app: Application) {
    private var quoteDao: Quote.Db

    private var allQuotes: LiveData<List<Quote>>

    init {
        val database = QuoteDatabase(app.applicationContext)
        quoteDao = database.dao()
        allQuotes = quoteDao.getAllQuotes()
    }

    fun insertQuote(quote: Quote, idCallback: ((Long) -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.IO) {
            val insertedAt = quoteDao.insert(quote)
            idCallback?.invoke(insertedAt)
            Log.d("QuoteDB", "Inserted Quote ($insertedAt)")
        }
    }

    fun getAllQuotes(): LiveData<List<Quote>> {
        return allQuotes
    }

    fun getRandomQuoteDb(): Deferred<Quote> {
        return GlobalScope.async(Dispatchers.IO) {
            quoteDao.getRandomQuote()
        }
    }

    fun getRandomQuoteServer(): Observable<Quote> {
        return Observable.zip(
                UnsplashAPI().getRandomImage(),
                ForismaticAPI().getQuote(),
                BiFunction { unSplashResponse: Response<UnsplashResponse>, quoteResponse: Response<ForismaticResponse> ->
                    Quote(
                            quoteResponse.body()?.quoteText!!,
                            quoteResponse.body()?.quoteAuthor,
                            System.currentTimeMillis(),
                            unSplashResponse.body()?.urls?.regular,
                            unSplashResponse.body()?.user?.name
                                    ?: unSplashResponse.body()?.user?.username.toString(),
                            "https://unsplash.com/${unSplashResponse.body()?.user?.username}"
                    )

                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .switchMap {
                    insertQuote(it) { id -> it.id = id }  //Dirty way of getting ID of inserted at room row.
                    Observable.just(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getQuote(id: Long): Deferred<Quote> {
        return GlobalScope.async(Dispatchers.IO) {
            quoteDao.getQuote(id)
        }
    }

    fun updateQuote(quote: Quote): Deferred<Unit> {
        return GlobalScope.async (Dispatchers.IO) {
            quoteDao.update(quote)
        }
    }
}