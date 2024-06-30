package com.sharukh.thunderquote.di

import androidx.room.Room
import com.sharukh.thunderquote.app.App
import com.sharukh.thunderquote.db.AppDatabase

object ServiceLocator {

    private val context = App.context

    fun appDatabase(): AppDatabase {
        val dBName = "thunder-quote.db"
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            dBName
        ).createFromAsset(dBName)
            .fallbackToDestructiveMigration()
            .build()
    }
}