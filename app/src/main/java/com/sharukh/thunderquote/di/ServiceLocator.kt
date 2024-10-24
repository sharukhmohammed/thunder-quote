package com.sharukh.thunderquote.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.sharukh.thunderquote.app.App
import com.sharukh.thunderquote.db.AppDatabase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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


    fun preferences(): SharedPreferences {
        return context.getSharedPreferences("thunder-quote", Context.MODE_PRIVATE)
    }

    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }


    fun retrofit(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .client(okHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}