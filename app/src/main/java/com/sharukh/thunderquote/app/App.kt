package com.sharukh.thunderquote.app

import android.app.Application

class App : Application() {
    companion object {
        lateinit var context: App private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}