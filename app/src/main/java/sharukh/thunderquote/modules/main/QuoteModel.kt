package sharukh.thunderquote.modules.main

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import sharukh.thunderquote.data.Quote
import sharukh.thunderquote.network.ForismaticAPI
import sharukh.thunderquote.network.ForismaticResponse
import sharukh.thunderquote.network.UnsplashAPI
import sharukh.thunderquote.network.UnsplashResponse

class QuoteModel {

    fun getQuote(): Observable<Quote> {
        return Observable.zip(
                UnsplashAPI().getRandomImage(),
                ForismaticAPI().getQuote(),
                BiFunction { unSplashResponse: Response<UnsplashResponse>, quoteResponse: Response<ForismaticResponse> ->
                    Quote(
                            quoteResponse.body()?.quoteText!!,
                            quoteResponse.body()?.quoteAuthor,
                            System.currentTimeMillis(),
                            unSplashResponse.body()?.urls?.regular)

                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}