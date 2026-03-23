package com.sharukh.thunderquote.ui.quote

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.ui.state.QuoteListState
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuoteListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val quote1 = Quote(
        id = 1,
        quote = "The only way to do great work is to love what you do.",
        author = "Steve Jobs"
    )
    private val quote2 = Quote(
        id = 2,
        quote = "In every difficulty lies opportunity.",
        author = "Albert Einstein",
        isFavorite = true
    )

    private fun stateWithQuotes(vararg quotes: Quote): QuoteListState =
        QuoteListState(quotes = flowOf(PagingData.from(quotes.toList())))

    @Test
    fun listScreen_displaysQuoteText() {
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteListScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = stateWithQuotes(quote1),
                    onAction = object : QuoteActions {}
                )
            }
        }

        composeTestRule.onNodeWithText(quote1.quote).assertIsDisplayed()
    }

    @Test
    fun listScreen_displaysAuthorName() {
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteListScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = stateWithQuotes(quote1),
                    onAction = object : QuoteActions {}
                )
            }
        }

        composeTestRule.onNodeWithText(quote1.author).assertIsDisplayed()
    }

    @Test
    fun listScreen_displaysMultipleQuotes() {
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteListScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = stateWithQuotes(quote1, quote2),
                    onAction = object : QuoteActions {}
                )
            }
        }

        composeTestRule.onNodeWithText(quote1.quote).assertIsDisplayed()
        composeTestRule.onNodeWithText(quote2.quote).assertIsDisplayed()
    }

    @Test
    fun favoriteButton_invokesOnFavoriteCallback_withCorrectQuote() {
        var favoriteClicked: Quote? = null
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteListScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = stateWithQuotes(quote1),
                    onAction = object : QuoteActions {
                        override fun onFavorite(quote: Quote) {
                            favoriteClicked = quote
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()

        assertEquals(quote1, favoriteClicked)
    }
}
