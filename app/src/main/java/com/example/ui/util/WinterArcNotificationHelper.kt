package com.example.ui.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import java.util.Calendar

object WinterArcNotificationHelper {

    const val CHANNEL_ID = "winter_arc_reminders"
    const val CHANNEL_NAME = "Winter Arc Hatırlatıcıları"
    const val CHANNEL_DESC = "Dopamin detoksu, su ve günlük rutin hatırlatıcıları"

    const val PREFS_NAME = "winter_arc_reminders_prefs"
    const val KEY_DOPAMINE_ENABLED = "key_dopamine_reminder_enabled"
    const val KEY_DOPAMINE_HOUR = "key_dopamine_hour"
    const val KEY_DOPAMINE_MINUTE = "key_dopamine_minute"

    const val KEY_WATER_ENABLED = "key_water_reminder_enabled"
    const val KEY_WATER_HOUR = "key_water_hour"
    const val KEY_WATER_MINUTE = "key_water_minute"

    const val ID_DOPAMINE = 1001
    const val ID_WATER = 1002
    const val ID_TEST = 9999

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        targetTab: String = "DAILY_TRACKER"
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TARGET_TAB", targetTab)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val smallIcon = context.applicationInfo.icon.takeIf { it != 0 } ?: android.R.drawable.ic_dialog_info

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted on Android 13+
        }
    }

    fun sendTestNotification(context: Context) {
        showNotification(
            context = context,
            notificationId = ID_TEST,
            title = "🌲 Winter Arc • Test Bildirimi",
            message = "Bildirimler ve dokunsal geri bildirim motoru kusursuz çalışıyor! 🔥 Zihnini odaklı tut ve seriyi bozma."
        )
    }

    fun scheduleDailyReminder(
        context: Context,
        requestCode: Int,
        hour: Int,
        minute: Int,
        title: String,
        message: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, WinterArcReminderReceiver::class.java).apply {
            putExtra(WinterArcReminderReceiver.EXTRA_TITLE, title)
            putExtra(WinterArcReminderReceiver.EXTRA_MESSAGE, message)
            putExtra(WinterArcReminderReceiver.EXTRA_ID, requestCode)
            putExtra(WinterArcReminderReceiver.EXTRA_HOUR, hour)
            putExtra(WinterArcReminderReceiver.EXTRA_MINUTE, minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WinterArcReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun syncAllReminders(context: Context) {
        val prefs = getPrefs(context)

        // 1. Dopamin & Akşam Rutini
        val dopamineEnabled = prefs.getBoolean(KEY_DOPAMINE_ENABLED, true)
        val dopHour = prefs.getInt(KEY_DOPAMINE_HOUR, 21)
        val dopMin = prefs.getInt(KEY_DOPAMINE_MINUTE, 30)

        if (dopamineEnabled) {
            scheduleDailyReminder(
                context = context,
                requestCode = ID_DOPAMINE,
                hour = dopHour,
                minute = dopMin,
                title = "🔥 Winter Arc • Günlük Kapanış & Detoks",
                message = "Bugün dopamin detoksunu korudun mu? Zihinsel berraklığını ve rutinlerini kaydetmeyi unutma!"
            )
        } else {
            cancelReminder(context, ID_DOPAMINE)
        }

        // 2. Su & Hidrasyon
        val waterEnabled = prefs.getBoolean(KEY_WATER_ENABLED, true)
        val waterHour = prefs.getInt(KEY_WATER_HOUR, 15)
        val waterMin = prefs.getInt(KEY_WATER_MINUTE, 0)

        if (waterEnabled) {
            scheduleDailyReminder(
                context = context,
                requestCode = ID_WATER,
                hour = waterHour,
                minute = waterMin,
                title = "💧 Hidrasyon & Zihinsel Odak",
                message = "Günün ortasındasın. Zihnini ve metabolizmanı tazelemek için büyük bir bardak su iç!"
            )
        } else {
            cancelReminder(context, ID_WATER)
        }
    }
}
