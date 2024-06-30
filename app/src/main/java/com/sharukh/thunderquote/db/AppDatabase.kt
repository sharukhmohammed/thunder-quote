package com.sharukh.thunderquote.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sharukh.thunderquote.model.Quote

@Database(
    entities = [Quote::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}