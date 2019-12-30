package sharukh.thunderquote.modules.quoteList

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_quote_list.*
import sharukh.thunderquote.R
import sharukh.thunderquote.base.BaseActivity
import sharukh.thunderquote.data.Quote

class QuoteListActivity : BaseActivity() {


    private val adapter = QuoteListAdapter()
    private lateinit var quoteListViewModel: QuoteListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote_list)

        initVars()
        initViews()

    }

    private fun initVars() {
        quoteListViewModel = ViewModelProviders.of(this).get(QuoteListViewModel::class.java)

    }

    private fun initViews() {

        quoteListViewModel
                .getAllQuotes()
                .observe(this as LifecycleOwner, Observer {
                    paintViews(it)
                })


        quote_list_recycler.adapter = adapter
    }


    private fun paintViews(quotes: List<Quote>) {
        if (!quotes.isNullOrEmpty())
            adapter.refreshQuotes(ArrayList(quotes))
        else
            Toast.makeText(this, "No Quotes", Toast.LENGTH_SHORT).show()
    }
}
