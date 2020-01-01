package sharukh.thunderquote.modules.quoteList

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_quote_list.*
import sharukh.thunderquote.R
import sharukh.thunderquote.base.BaseActivity
import sharukh.thunderquote.data.Quote


class QuoteListActivity : BaseActivity() {


    companion object {
        private const val TAG = "QuoteListActivity"
    }
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

        //Inverted Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor("#1A000000")
        }

        quote_list_recycler.adapter = adapter

        quote_list_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {performTabSelection(tab)}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) { performTabSelection(tab) }
        })

        quote_list_tab_layout.getTabAt(0)?.select() //Select 'All'
    }


    private fun paintViews(quotes: List<Quote>) {
        if (!quotes.isNullOrEmpty())
            adapter.refreshQuotes(ArrayList(quotes))
        else
            Toast.makeText(this, "No Quotes", Toast.LENGTH_SHORT).show()
    }

    private fun performTabSelection(tab: TabLayout.Tab?) {
        when (tab!!.position) {
            0 -> {
                //All
                quoteListViewModel
                        .getAllQuotes()
                        .observe(this@QuoteListActivity as LifecycleOwner, Observer {
                            paintViews(it)
                        })
            }
            1 -> {
                //Favourites
                quoteListViewModel
                        .getFavouriteQuotes()
                        .observe(this@QuoteListActivity as LifecycleOwner, Observer {
                            paintViews(it)
                        })
            }
            else -> Log.wtf(TAG, "Shouldn't be happening")
        }
    }
}
