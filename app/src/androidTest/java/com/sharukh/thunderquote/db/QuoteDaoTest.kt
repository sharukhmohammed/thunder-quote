package com.sharukh.thunderquote.db

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sharukh.thunderquote.model.Quote
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuoteDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: QuoteDao

    private val quote1 = Quote(
        id = 1,
        quote = "The only way to do great work is to love what you do.",
        author = "Steve Jobs"
    )
    private val quote2 = Quote(
        id = 2,
        quote = "In the middle of every difficulty lies opportunity.",
        author = "Albert Einstein",
        isFavorite = true
    )
    private val quote3 = Quote(
        id = 3,
        quote = "It does not matter how slowly you go as long as you do not stop.",
        author = "Confucius"
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.quoteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    private suspend fun PagingSource<Int, Quote>.loadAll(): List<Quote> {
        val result = load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = Int.MAX_VALUE,
                placeholdersEnabled = false
            )
        )
        return (result as PagingSource.LoadResult.Page).data
    }

    @Test
    fun insertAll_andGetAll_returnsAllInsertedQuotes() = runTest {
        dao.insertAll(quote1, quote2, quote3)

        val all = dao.getAll().loadAll()

        assertEquals(3, all.size)
        assertTrue(all.containsAll(listOf(quote1, quote2, quote3)))
    }

    @Test
    fun update_changesIsFavoriteField() = runTest {
        dao.insertAll(quote1)

        dao.update(quote1.copy(isFavorite = true))

        val result = dao.getQuote(quote1.id).first()
        assertTrue(result?.isFavorite == true)
    }

    @Test
    fun getRandomId_returnsNull_forEmptyDatabase() = runTest {
        assertNull(dao.getRandomId())
    }

    @Test
    fun getRandomId_returnsValidId_forNonEmptyDatabase() = runTest {
        dao.insertAll(quote1, quote2, quote3)

        val id = dao.getRandomId()

        assertNotNull(id)
        assertTrue(id!! in listOf(1, 2, 3))
    }

    @Test
    fun getQuote_returnsCorrectQuote() = runTest {
        dao.insertAll(quote1)

        val result = dao.getQuote(quote1.id).first()

        assertEquals(quote1, result)
    }

    @Test
    fun getQuote_returnsNull_forNonExistentId() = runTest {
        val result = dao.getQuote(999).first()

        assertNull(result)
    }

    @Test
    fun getQuote_emitsUpdate_whenRowIsModified() = runTest {
        dao.insertAll(quote1)
        val before = dao.getQuote(quote1.id).first()
        assertFalse(before!!.isFavorite)

        dao.update(quote1.copy(isFavorite = true))

        val after = dao.getQuote(quote1.id).first()
        assertTrue(after!!.isFavorite)
    }

    @Test
    fun getFavorites_returnsOnlyFavoriteQuotes() = runTest {
        dao.insertAll(quote1, quote2, quote3)

        val favorites = dao.getFavorites().loadAll()

        assertEquals(1, favorites.size)
        assertEquals(quote2, favorites[0])
    }

    @Test
    fun getFavorites_returnsEmpty_whenNoFavoritesExist() = runTest {
        dao.insertAll(quote1, quote3)

        val favorites = dao.getFavorites().loadAll()

        assertTrue(favorites.isEmpty())
    }

    @Test
    fun insertAll_withConflict_replacesExistingRecord() = runTest {
        dao.insertAll(quote1)
        val updated = quote1.copy(quote = "Updated quote text", isFavorite = true)

        dao.insertAll(updated)

        val all = dao.getAll().loadAll()
        assertEquals(1, all.size)
        assertEquals(updated, all[0])
    }

    @Test
    fun getAll_withExactSearchQuery_returnsMatchingQuote() = runTest {
        dao.insertAll(quote1, quote2)

        val results = dao.getAll(quote1.quote).loadAll()

        assertEquals(1, results.size)
        assertEquals(quote1, results[0])
    }

    @Test
    fun getAll_withPartialSearchQuery_returnsEmpty() = runTest {
        // The SQL `WHERE quote LIKE :searchQuery` without % wildcards behaves as an exact
        // equality check. This test documents the current behavior: partial strings do not match.
        dao.insertAll(quote1)

        val results = dao.getAll("great work").loadAll()

        assertTrue(results.isEmpty())
    }
}
