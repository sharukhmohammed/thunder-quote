package com.sharukh.thunderquote.home.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.repo.QuoteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = QuoteRepo()

    private val _state = MutableStateFlow(
        State(
            quotes = repo.quoteByPaging()
        )
    )
    val state = _state.asStateFlow()


    init {
        determineQuotesState()
    }

    private fun determineQuotesState() {
        viewModelScope.launch {
            // repo.insertAllQuotes()
        }
    }

    fun refresh() {

    }

    fun share() {

    }

    fun favourite() {

    }


    @Immutable
    data class State(
        val quotes: Flow<PagingData<Quote>> = emptyFlow()
    )
}
