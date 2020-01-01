package sharukh.thunderquote.modules.quoteList

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_quote.view.*
import sharukh.thunderquote.R
import sharukh.thunderquote.data.Quote
import sharukh.thunderquote.modules.main.QuoteActivity

class QuoteListAdapter : RecyclerView.Adapter<QuoteListAdapter.QuoteHolder>() {
    private val quoteList = ArrayList<Quote>()

    internal fun refreshQuotes(quotes: ArrayList<Quote>) {
        quoteList.clear()
        quoteList.addAll(quotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteHolder {
        return QuoteHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false))
    }

    override fun getItemCount(): Int {
        return quoteList.size
    }

    override fun onBindViewHolder(holder: QuoteHolder, position: Int) {
        holder.bind(quoteList[position])
    }

    inner class QuoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(quote: Quote) {
            with(itemView) {

                Picasso.get()
                        .load(quote.backgroundUrl)
                        .into(this.item_quote_image)

                this.item_quote_text.text = quote.text
                this.item_quote_author.text = quote.author

                setOnClickListener {
                    //Must give callback to QuoteListActivity then start activity there - this is a dirty way
                    context.startActivity(QuoteActivity.startIntent(context,quote.id))
                }
            }
        }
    }

}