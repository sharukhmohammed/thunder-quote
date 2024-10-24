package com.sharukh.thunderquote.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.repo.DogRepo
import com.sharukh.thunderquote.repo.QuoteRepo
import com.sharukh.thunderquote.ui.state.QuoteItemState
import com.sharukh.thunderquote.ui.state.QuoteListState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = QuoteRepo()
    private val dogRepo = DogRepo()

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
        // getDogBreeds()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDogBreeds() {
        viewModelScope.launch {
            dogRepo
                .getBreeds()
                .onStart { Log.i("HomeViewModel", "API Call Started") }
                .onCompletion { Log.i("HomeViewModel", "API Call Ended") }
                .catch { Log.e("HomeViewModel", "Error: ${it.message}") }
                .flatMapConcat { data ->
                    data.message.keys.map { key ->
                        dogRepo
                            .getSubBreeds(key)
                            .onStart { Log.i("HomeViewModel", "START $key") }
                            .onCompletion { Log.i("HomeViewModel", "END $key") }
                            .map { data -> key to data }
                    }.merge()
                }
                .collectLatest { subBreeds ->
                    Log.i(
                        "HomeViewModel",
                        "Sub-breeds for ${subBreeds.first}: ${subBreeds.second.message}"
                    )
                }
        }
    }


}
