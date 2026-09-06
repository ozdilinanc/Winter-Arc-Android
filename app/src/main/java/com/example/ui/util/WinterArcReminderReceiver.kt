package com.example.ui.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WinterArcReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "🌲 Winter Arc"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Günlük hedeflerini kontrol etmeyi unutma!"
        val id = intent.getIntExtra(EXTRA_ID, WinterArcNotificationHelper.ID_DOPAMINE)
        val hour = intent.getIntExtra(EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(EXTRA_MINUTE, -1)

        // Bildirimi göster
        WinterArcNotificationHelper.showNotification(
            context = context,
            notificationId = id,
            title = title,
            message = message,
            targetTab = "DAILY_TRACKER"
        )

        // Bir sonraki gün için tekrar kur
        if (hour >= 0 && minute >= 0) {
            WinterArcNotificationHelper.scheduleDailyReminder(
                context = context,
                requestCode = id,
                hour = hour,
                minute = minute,
                title = title,
                message = message
            )
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_reminder_title"
        const val EXTRA_MESSAGE = "extra_reminder_message"
        const val EXTRA_ID = "extra_reminder_id"
        const val EXTRA_HOUR = "extra_reminder_hour"
        const val EXTRA_MINUTE = "extra_reminder_minute"
    }
}
