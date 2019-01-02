package sharukh.thunderquote.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.os.StrictMode
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sharukh.thunderquote.QuoteAPI

class QuoteRepository(application: Application) {
    private var quoteDao: QuoteDao
    private var quoteAPI:QuoteAPI
    private var allQuotes: LiveData<List<Quote>>
    private var randomQuote: Quote

    init {
        quoteDao = QuoteDatabase(application.applicationContext).quoteDao()
        quoteAPI = QuoteAPI()
        allQuotes = quoteDao.getAllQuotes()
        randomQuote = quoteDao.getRandomQuote()
    }

    fun insert(quote: Quote) {
        InsertQuoteAsyncTask(quoteDao).execute(quote)
    }


    fun update(quote: Quote) {
        UpdateQuoteAsyncTask(quoteDao).execute(quote)
    }

    fun delete(quote: Quote) {
        DeleteQuoteAsyncTask(quoteDao).execute(quote)
    }

    fun deleteAll() {
        DeleteAllQuoteAsyncTask(quoteDao).execute()
    }


    fun getAllQuotes(): LiveData<List<Quote>> {
        return allQuotes
    }

    fun getQuoteFromApi() {
        QuoteAPI()
                .getQuote()
                .enqueue(object : Callback<Quote> {
                    override fun onFailure(call: Call<Quote>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                        if (response.isSuccessful) {
                            InsertQuoteAsyncTask(quoteDao).execute(response.body())
                        } else {
                            Log.e("XX", "Failed")
                        }
                    }

                })
    }


    companion object {
        private class InsertQuoteAsyncTask(val quoteDao: QuoteDao) : AsyncTask<Quote, Unit, Unit>() {
            override fun doInBackground(vararg params: Quote) {
                quoteDao.insert(params[0])
            }
        }

        private class UpdateQuoteAsyncTask(val quoteDao: QuoteDao) : AsyncTask<Quote, Unit, Unit>() {
            override fun doInBackground(vararg params: Quote) {
                quoteDao.update(params[0])
            }
        }

        private class DeleteQuoteAsyncTask(val quoteDao: QuoteDao) : AsyncTask<Quote, Unit, Unit>() {
            override fun doInBackground(vararg params: Quote) {
                quoteDao.delete(params[0])
            }
        }

        private class DeleteAllQuoteAsyncTask(val quoteDao: QuoteDao) : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                quoteDao.deleteAllQuotes()
            }
        }
    }
}