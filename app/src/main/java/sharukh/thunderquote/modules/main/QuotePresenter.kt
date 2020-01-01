package sharukh.thunderquote.modules.main

import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import sharukh.thunderquote.base.BaseView
import sharukh.thunderquote.data.Quote

class QuotePresenter(private val view: View) {

    private var disposable: Disposable? = null
    private val model = QuoteModel()

    fun dispose() {
        disposable?.dispose()
    }

    fun getQuote(id: Long? = null) {

        //Get quote from DB
        if (id != null)
            GlobalScope.launch {
                val quote = model.getQuoteDb(id).await()
                view.showQuote(quote)
                view.toggleLike(quote.isFavorite)
            }
        //Get Quote from Internet
        else
            disposable = model
                    .getQuote()
                    .doOnSubscribe { view.showProgress() }
                    .doOnTerminate { view.hideProgress() }
                    .subscribe(
                            {
                                view.showQuote(it)
                                view.toggleLike(it.isFavorite)
                            },
                            {
                                it.printStackTrace()
                                if (it is HttpException) {
                                    view.showError("Network Error: " + it.code() + " ${it.message()}")
                                }
                            },
                            {
                                println("Quote: OnComplete")

                            },
                            {
                                println("Quote: OnSubscribe")
                            }
                    )
    }

    fun toggleLike(id: Long) {
        model.toggleLike(id) { view.toggleLike(it) }
    }

    interface View : BaseView {
        fun showQuote(quote: Quote)
        fun toggleLike(liked: Boolean)
    }
}