package com.sharukh.thunderquote.ui.quote

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.ui.state.QuoteItemState
import com.sharukh.thunderquote.ui.theme.ThunderQuoteTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuoteDisplayScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testQuote = Quote(
        id = 1,
        quote = "Do what you can, with what you have, where you are.",
        author = "Theodore Roosevelt",
        isFavorite = false
    )

    @Test
    fun loadingState_doesNotShowQuoteContent() {
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteDisplayScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = QuoteItemState(quote = null),
                    onAction = object : QuoteActions {}
                )
            }
        }

        composeTestRule.onNodeWithText(testQuote.quote).assertDoesNotExist()
        composeTestRule.onNodeWithText(testQuote.author).assertDoesNotExist()
    }

    @Test
    fun withQuote_displaysQuoteText() {
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteDisplayScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = QuoteItemState(quote = testQuote),
                    onAction = object : QuoteActions {}
                )
            }
        }

        composeTestRule.onNodeWithText(testQuote.quote).assertIsDisplayed()
    }

    @Test
    fun withQuote_displaysAuthorName() {
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteDisplayScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = QuoteItemState(quote = testQuote),
                    onAction = object : QuoteActions {}
                )
            }
        }

        composeTestRule.onNodeWithText(testQuote.author).assertIsDisplayed()
    }

    @Test
    fun favoriteButton_invokesOnFavoriteCallback_withCorrectQuote() {
        var favoriteClicked: Quote? = null
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteDisplayScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = QuoteItemState(quote = testQuote),
                    onAction = object : QuoteActions {
                        override fun onFavorite(quote: Quote) {
                            favoriteClicked = quote
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()

        assertEquals(testQuote, favoriteClicked)
    }

    @Test
    fun refreshFab_invokesOnRefreshCallback() {
        var refreshCalled = false
        composeTestRule.setContent {
            ThunderQuoteTheme {
                QuoteDisplayScreen(
                    innerPadding = PaddingValues(0.dp),
                    state = QuoteItemState(quote = testQuote),
                    onAction = object : QuoteActions {
                        override fun onRefresh() {
                            refreshCalled = true
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Refresh").performClick()

        assertTrue(refreshCalled)
    }
}
