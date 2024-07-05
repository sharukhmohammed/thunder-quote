package com.sharukh.thunderquote.ui.quote

import com.sharukh.thunderquote.model.Quote

interface QuoteActions {
    fun onFavorite(quote: Quote) {

    }

    fun onShare(quote: Quote) {

    }

    fun onRefresh() {

    }
}