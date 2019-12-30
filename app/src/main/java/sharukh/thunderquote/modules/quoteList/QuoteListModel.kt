package sharukh.thunderquote.modules.quoteList

import android.app.Application
import androidx.lifecycle.LiveData
import sharukh.thunderquote.base.App
import sharukh.thunderquote.data.Quote
import sharukh.thunderquote.data.QuoteRepository

class QuoteListModel {

    private val quoteRepository = QuoteRepository(App.context as Application)

    fun getAllQuotes(): LiveData<List<Quote>> {
        return quoteRepository.getAllQuotes()
    }
}
