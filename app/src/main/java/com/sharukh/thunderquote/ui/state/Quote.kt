package com.sharukh.thunderquote.ui.state

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import com.sharukh.thunderquote.model.Quote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Immutable
data class QuoteListState(
    val quotes: Flow<PagingData<Quote>> = emptyFlow()
)

@Immutable
data class QuoteItemState(
    val quote: Quote? = null,
) {
    val isLoading = quote == null
}