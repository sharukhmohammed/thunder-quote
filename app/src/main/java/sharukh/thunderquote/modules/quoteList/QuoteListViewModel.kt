package sharukh.thunderquote.modules.quoteList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import sharukh.thunderquote.data.Quote

class QuoteListViewModel(app: Application) : AndroidViewModel(app) {
    private val model = QuoteListModel()

    fun getAllQuotes(): LiveData<List<Quote>> {
        return model.getAllQuotes()
    }

    fun getFavouriteQuotes(): LiveData<List<Quote>> {
        return model.getFavouriteQuotes()
    }
}