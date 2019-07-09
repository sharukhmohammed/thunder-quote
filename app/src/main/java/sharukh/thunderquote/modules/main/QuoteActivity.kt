package sharukh.thunderquote.modules.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_quote.*
import sharukh.thunderquote.R
import sharukh.thunderquote.base.BaseActivity
import sharukh.thunderquote.data.Quote

class QuoteActivity : BaseActivity(), QuotePresenter.View {

    private val presenter = QuotePresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        presenter.getQuote()

        quote_text.setOnClickListener { presenter.getQuote() }
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun showQuote(quote: Quote) {
        quote_text.text = quote.text
        quote_author.text = quote.author
    }

    override fun showProgress() {
        quote_progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        quote_progress.visibility = View.INVISIBLE
    }

    override fun showEmptyView() {
        //Do nothing
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun loadImage(url: String) {
        Picasso.get()
                .load(url)
                .into(quote_background)
    }
}
