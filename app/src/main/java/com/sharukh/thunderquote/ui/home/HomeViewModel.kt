package com.sharukh.thunderquote.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.repo.QuoteRepo
import com.sharukh.thunderquote.ui.state.QuoteItemState
import com.sharukh.thunderquote.ui.state.QuoteListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = QuoteRepo()

    private val _listState = MutableStateFlow(
        QuoteListState(repo.quoteByPaging("", false))
    )
    val listState = _listState.asStateFlow()

    private val _favoritesState = MutableStateFlow(
        QuoteListState(repo.quoteByPaging("", true))
    )
    val favoritesState = _favoritesState.asStateFlow()

    private val _randomQuoteState = MutableStateFlow(QuoteItemState())
    val randomQuoteState = _randomQuoteState.asStateFlow()


    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        repo.randomQuote(true).stateIn(viewModelScope).collect { newQuote ->
                Log.d("HomeViewModel", "Callback: ${newQuote?.quote}")
                _randomQuoteState.update { state -> state.copy(quote = newQuote) }
            }
    }

    fun toggleFavorite(quote: Quote) = viewModelScope.launch {
        repo.setFavorite(quote, quote.isFavorite.not())
    }


}
