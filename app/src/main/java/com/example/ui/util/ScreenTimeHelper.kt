package com.example.ui.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import java.util.Calendar

object ScreenTimeHelper {

    data class AppUsageDetail(
        val packageName: String,
        val appName: String,
        val iconEmoji: String,
        val minutes: Long
    )

    data class SocialMediaUsage(
        val instagramMinutes: Long = 0L,
        val totalSocialMinutes: Long = 0L,
        val appDetails: List<AppUsageDetail> = emptyList(),
        val isPermissionGranted: Boolean = false,
        val calculationMethod: String = "UsageEvents"
    )

    val TRACKED_APPS = listOf(
        AppUsageDetail("com.instagram.android", "Instagram", "📸", 0L),
        AppUsageDetail("com.zhiliaoapp.musically", "TikTok", "🎵", 0L),
        AppUsageDetail("com.ss.android.ugc.trill", "TikTok", "🎵", 0L),
        AppUsageDetail("com.twitter.android", "X (Twitter)", "🐦", 0L),
        AppUsageDetail("com.google.android.youtube", "YouTube", "▶️", 0L)
    )

    private val SOCIAL_PACKAGES = TRACKED_APPS.map { it.packageName }.toSet()

    fun hasUsagePermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun openUsageSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun openDigitalWellbeing(context: Context) {
        try {
            val intent = Intent().apply {
                action = "android.settings.DIGITAL_WELLBEING_SETTINGS"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openUsageSettings(context)
        }
    }

    fun getTodaySocialUsage(context: Context): SocialMediaUsage {
        if (!hasUsagePermission(context)) {
            return SocialMediaUsage(isPermissionGranted = false)
        }

        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return SocialMediaUsage(isPermissionGranted = true)

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.timeInMillis
            val now = System.currentTimeMillis()

            // 1. Birincil Yöntem: UsageEvents (Android Digital Wellbeing ile birebir aynı, saniyesine kadar net)
            val usageMillisMap = calculateUsageFromEvents(usageStatsManager, SOCIAL_PACKAGES, startOfDay, now)

            // 2. Yedek Yöntem: Eğer UsageEvents desteklenmiyorsa veya 0 döndüyse queryAndAggregateUsageStats
            val finalUsageMap = if (usageMillisMap.isNotEmpty()) {
                usageMillisMap
            } else {
                calculateUsageFromAggregatedStats(usageStatsManager, SOCIAL_PACKAGES, startOfDay, now)
            }

            val instaMillis = finalUsageMap["com.instagram.android"] ?: 0L
            val instaMins = instaMillis / (1000 * 60)

            val appDetailsList = mutableListOf<AppUsageDetail>()
            var totalSocialMillis = 0L

            for (app in TRACKED_APPS) {
                val millis = finalUsageMap[app.packageName] ?: 0L
                if (millis > 0L) {
                    totalSocialMillis += millis
                    val mins = millis / (1000 * 60)
                    appDetailsList.add(app.copy(minutes = mins))
                }
            }

            Log.d("ScreenTimeHelper", "=== EKRAN SÜRESİ HESAPLAMASI ===")
            Log.d("ScreenTimeHelper", "Instagram: $instaMins dk ($instaMillis ms)")
            for (detail in appDetailsList) {
                Log.d("ScreenTimeHelper", "${detail.appName} (${detail.packageName}): ${detail.minutes} dk")
            }

            SocialMediaUsage(
                instagramMinutes = instaMins,
                totalSocialMinutes = totalSocialMillis / (1000 * 60),
                appDetails = appDetailsList,
                isPermissionGranted = true,
                calculationMethod = if (usageMillisMap.isNotEmpty()) "UsageEvents (Birebir Hassas)" else "AggregatedStats"
            )
        } catch (e: Exception) {
            Log.e("ScreenTimeHelper", "Usage calculation error", e)
            SocialMediaUsage(isPermissionGranted = true)
        }
    }

    private fun calculateUsageFromEvents(
        usageStatsManager: UsageStatsManager,
        packageNames: Set<String>,
        startOfDay: Long,
        now: Long
    ): Map<String, Long> {
        val usageMap = mutableMapOf<String, Long>()
        val lastResumeMap = mutableMapOf<String, Long>()

        return try {
            val events = usageStatsManager.queryEvents(startOfDay, now) ?: return emptyMap()
            val event = UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                val pkg = event.packageName ?: continue
                if (pkg !in packageNames) continue

                val eventType = event.eventType
                val timeStamp = event.timeStamp

                // Sadece bugünün aralığındaki olayları dikkate al
                if (timeStamp < startOfDay || timeStamp > now) continue

                when (eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        lastResumeMap[pkg] = timeStamp
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.ACTIVITY_STOPPED -> {
                        val resumedAt = lastResumeMap.remove(pkg)
                        if (resumedAt != null && timeStamp > resumedAt) {
                            val duration = timeStamp - resumedAt
                            // 6 saatten uzun tekil session hatalarını filtrele
                            if (duration < 6 * 60 * 60 * 1000L) {
                                usageMap[pkg] = (usageMap[pkg] ?: 0L) + duration
                            }
                        }
                    }
                }
            }

            // Halen açık olan uygulama varsa şu ana kadar olan süresini ekle
            for ((pkg, resumedAt) in lastResumeMap) {
                if (now > resumedAt) {
                    val duration = now - resumedAt
                    if (duration < 6 * 60 * 60 * 1000L) {
                        usageMap[pkg] = (usageMap[pkg] ?: 0L) + duration
                    }
                }
            }

            usageMap
        } catch (e: Exception) {
            Log.w("ScreenTimeHelper", "queryEvents failed: ${e.message}")
            emptyMap()
        }
    }

    private fun calculateUsageFromAggregatedStats(
        usageStatsManager: UsageStatsManager,
        packageNames: Set<String>,
        startOfDay: Long,
        now: Long
    ): Map<String, Long> {
        val result = mutableMapOf<String, Long>()
        try {
            val aggregated = usageStatsManager.queryAndAggregateUsageStats(startOfDay, now)
            for (pkg in packageNames) {
                val stats = aggregated[pkg]
                if (stats != null && stats.totalTimeInForeground > 0L) {
                    result[pkg] = stats.totalTimeInForeground
                }
            }
        } catch (e: Exception) {
            Log.w("ScreenTimeHelper", "queryAndAggregateUsageStats failed: ${e.message}")
        }
        return result
    }
}
