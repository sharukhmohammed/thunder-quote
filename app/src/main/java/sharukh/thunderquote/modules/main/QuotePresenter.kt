package sharukh.thunderquote.modules.main

import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import sharukh.thunderquote.base.BaseView
import sharukh.thunderquote.data.Quote

class QuotePresenter(private val view: View) {

    private var disposable: Disposable? = null
    private var imageAPIDisposable: Disposable? = null
    private val model = QuoteModel()

    fun dispose() {
        disposable?.dispose()
    }

    fun getQuote() {

        disposable = model
                .getQuote()
                .doOnSubscribe { view.showProgress() }
                .doOnTerminate { view.hideProgress() }
                .subscribe(
                        {
                            view.loadImage(it.backgroundUrl.toString())
                            view.showQuote(it)
                        },
                        {
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

    interface View : BaseView {
        fun showQuote(quote: Quote)
        fun loadImage(url: String)
    }
}