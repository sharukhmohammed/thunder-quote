package com.sharukh.thunderquote.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sharukh.thunderquote.model.Quote

@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg quotes: Quote)

    @Update
    suspend fun update(quote: Quote)

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    fun getFavorites(): PagingSource<Int, Quote>

    @Query("SELECT * FROM quotes")
    fun getAll(): PagingSource<Int, Quote>

    @Query("SELECT * FROM quotes  WHERE quote LIKE :searchQuery")
    fun getAll(searchQuery: String): PagingSource<Int, Quote>
}