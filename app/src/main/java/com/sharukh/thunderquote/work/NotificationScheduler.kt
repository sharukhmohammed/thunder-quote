package com.sharukh.thunderquote.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sharukh.thunderquote.notification.Notification
import com.sharukh.thunderquote.repo.QuoteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class NotificationScheduler(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val repo = QuoteRepo()
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val quote = repo.randomQuote(true).firstOrNull()
            return@withContext if (quote != null) {
                Notification.post(
                    context,
                    Notification.Category.DailyQuotes,
                    quote.id,
                    quote.author,
                    quote.quote
                )
                Result.success()
            } else
                Result.failure()
        }
    }
}