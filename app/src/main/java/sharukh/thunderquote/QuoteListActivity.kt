package sharukh.thunderquote

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class QuoteListActivity : AppCompatActivity() {

    //lateinit var quoteViewModel:QuoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote_list)

        initVars()
    }

    private fun initVars() {
       // quoteViewModel = ViewModelProviders.of(this).get(QuoteViewModel::class.java)
    }
}
