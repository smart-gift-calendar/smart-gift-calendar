package com.smartgiftcalendar.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smartgiftcalendar.app.R
import com.smartgiftcalendar.app.data.FakeRepository

object ReminderNotifier {
    private const val CHANNEL_ID = "birthday_reminders"

    fun showDueReminders(context: Context) {
        createChannel(context)
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val manager = NotificationManagerCompat.from(context)
        FakeRepository.remindersForToday().forEachIndexed { index, (entry, days) ->
            // Localized reminder text for 3 days before or birthday day.
            val message = if (days == 0L) {
                context.getString(R.string.reminder_today, entry.name)
            } else {
                context.getString(R.string.reminder_three_days, entry.name)
            }
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            manager.notify(1000 + index, notification)
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
