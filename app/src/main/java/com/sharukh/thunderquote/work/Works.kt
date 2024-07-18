package com.sharukh.thunderquote.work

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object Works {
    fun init(context: Context) {
        val request = PeriodicWorkRequestBuilder<NotificationScheduler>(
            1,
            TimeUnit.MINUTES
        )
        WorkManager.getInstance(context).enqueue(request.build())
    }
}