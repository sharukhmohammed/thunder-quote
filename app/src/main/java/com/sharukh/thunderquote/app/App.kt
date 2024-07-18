package com.sharukh.thunderquote.app

import android.app.Application
import com.sharukh.thunderquote.notification.Notification
import com.sharukh.thunderquote.work.Works

class App : Application() {
    companion object {
        lateinit var context: App private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        Notification.initChannelsAndGroups(context)
        Works.init(context)
    }
}