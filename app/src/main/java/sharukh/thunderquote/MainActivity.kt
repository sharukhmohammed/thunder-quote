package sharukh.thunderquote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sharukh.thunderquote.data.Quote
import sharukh.thunderquote.data.QuoteRepository

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        QuoteAPI()
                .getQuote()
                .enqueue(object : Callback<Quote> {
                    override fun onFailure(call: Call<Quote>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                        if (response.isSuccessful) {


                            quote_text.text = response.body()?.text
                            quote_author.text = response.body()?.author
                        } else {
                            Log.e("XX", "Failed")
                        }
                    }

                })

    }
}
