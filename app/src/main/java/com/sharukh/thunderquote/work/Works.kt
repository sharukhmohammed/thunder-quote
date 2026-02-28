package com.sharukh.thunderquote.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object Works {
    private const val DAILY_NOTIFICATION = "DailyNotification"

    fun init(context: Context) {
        val request = PeriodicWorkRequestBuilder<NotificationScheduler>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_NOTIFICATION,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_NOTIFICATION)
    }

    fun isScheduled(context: Context): Boolean {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(DAILY_NOTIFICATION)
            .get()
            .any { !it.state.isFinished }
    }
}
