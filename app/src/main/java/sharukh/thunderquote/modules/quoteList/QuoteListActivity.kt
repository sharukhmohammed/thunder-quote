package sharukh.thunderquote.modules.quoteList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sharukh.thunderquote.R

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
