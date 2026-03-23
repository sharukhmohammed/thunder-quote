package com.sharukh.thunderquote.ui.state

import com.sharukh.thunderquote.model.Quote
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteItemStateTest {

    private val sampleQuote = Quote(
        id = 1,
        quote = "To be or not to be.",
        author = "Shakespeare"
    )

    @Test
    fun isLoading_isTrue_whenQuoteIsNull() {
        val state = QuoteItemState(quote = null)
        assertTrue(state.isLoading)
    }

    @Test
    fun isLoading_isFalse_whenQuoteIsPresent() {
        val state = QuoteItemState(quote = sampleQuote)
        assertFalse(state.isLoading)
    }

    @Test
    fun defaultConstructor_hasNullQuote_andIsLoading() {
        val state = QuoteItemState()
        assertNull(state.quote)
        assertTrue(state.isLoading)
    }

    @Test
    fun copy_resettingQuoteToNull_setsIsLoadingTrue() {
        val loaded = QuoteItemState(quote = sampleQuote)
        val reset = loaded.copy(quote = null)
        assertTrue(reset.isLoading)
    }
}
