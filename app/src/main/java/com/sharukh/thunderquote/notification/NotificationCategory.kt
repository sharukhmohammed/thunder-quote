package com.sharukh.thunderquote.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.app.App


class Notification {

    companion object {


        fun initChannelsAndGroups(context: Context) {
            with(manager(context)){
                createNotificationChannelGroups(Group.entries.map { it.asGroup })
                createNotificationChannels(Category.entries.map { it.asChannel })
            }
        }


        private fun manager(context: Context): NotificationManager {
            return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

        fun openNotificationSettings(context: Context) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                // putExtra(Settings.EXTRA_CHANNEL_ID, QUOTES_CHANNEL_ID)
            }
            context.startActivity(intent)
        }

        fun post(
            context: Context,
            category: Notification.Category,
            id: Int,
            textTitle: String,
            textContent: String,
        ) {


            val builder =
                NotificationCompat.Builder(context, category.asChannel.id)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(
                        NotificationCompat
                            .BigTextStyle()
                            .bigText(textContent)
                            .setBigContentTitle(textTitle)
                    )

            manager(context).notify(id, builder.build())
        }


    }


    enum class Category(
        private val id: String,
        private val title: String,
        private val description: String,
        private val groupId: String,
        private val importance: Int,
    ) {


        DailyQuotes(
            "QUOTES_CHANNEL_ID",
            App.context.getString(R.string.notification_category_quotes_name),
            App.context.getString(R.string.notification_category_quotes_description),
            Group.QUOTES.id,
            NotificationManager.IMPORTANCE_LOW
        );

        val asChannel: NotificationChannel
            get() = NotificationChannel(
                id,
                title,
                importance
            ).apply {
                description = this@Category.description
                group = this@Category.groupId
            }


    }

    enum class Group(
        val id: String,
        private val title: String,
    ) {

        QUOTES(
            App.context.getString(R.string.notification_category_quotes_group),
            App.context.getString(R.string.notification_category_quotes_group),
        );

        val asGroup
            get() = NotificationChannelGroup(this.id, this.title)
    }
}