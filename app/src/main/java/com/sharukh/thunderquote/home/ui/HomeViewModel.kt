package com.sharukh.thunderquote.home.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.repo.HomeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = HomeRepo()

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()


    init {
        getAllQuotes()
    }

    private fun getAllQuotes() = viewModelScope.launch() {
        val quotes = repo.getAllQuotes()
        _state.update {
            it.copy(
                quote = quotes.randomOrNull(),
                quotes = quotes.take(10)
            )
        }
    }

    fun refresh() {
        _state.update {
            it.copy(
                quote = it.quotes.randomOrNull(),
                quotes = it.quotes.shuffled()
            )
        }
    }

    fun share() {

    }

    fun favourite() {

    }


    @Immutable
    data class State(
        val quote: Quote? = null,
        val quotes: List<Quote> = emptyList()
    ) {
        val isLoading
            get() = quote == null || quotes.isEmpty()
    }
}
