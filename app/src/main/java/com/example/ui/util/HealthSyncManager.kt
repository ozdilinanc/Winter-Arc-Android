package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Reads only records written by Huawei Health. Health Connect is the local transport. */
object HealthSyncManager {
    const val WALK_STEP_TARGET = 7000L
    const val SLEEP_HOURS_TARGET = 6.0
    const val HUAWEI_HEALTH_PACKAGE = "com.huawei.health"
    const val HUAWEI_SOURCE = "Huawei Sağlık"
    private const val TAG = "HealthSyncManager"
    // Never reuse the old mixed-source, sensor or manual cache as Huawei data.
    private const val PREFS_NAME = "winter_arc_huawei_sync_v2"
    private val huaweiOrigin = setOf(DataOrigin(HUAWEI_HEALTH_PACKAGE))

    val REQUIRED_HEALTH_PERMISSIONS: Set<String> by lazy {
        setOf(HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class))
    }
    enum class HealthConnectAvailability { AVAILABLE, NOT_INSTALLED, NOT_SUPPORTED }

    data class HealthSyncResult(
        val stepsCount: Long = 0L,
        val sleepHours: Double = 0.0,
        val sleepMinutesTotal: Long = 0L,
        val sleepQuality: String = "tired",
        val isSleep6hPlus: Boolean = false,
        val isWalkGoalMet: Boolean = false,
        val source: String = HUAWEI_SOURCE,
        val syncedAtMillis: Long = System.currentTimeMillis(),
        val isSuccess: Boolean = false,
        val message: String = "",
        val hasStepsData: Boolean = false,
        val hasSleepData: Boolean = false,
        val dateKey: String = LocalDate.now().toString()
    )

    fun checkHealthConnectAvailability(context: Context): HealthConnectAvailability =
        when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }

    suspend fun hasHealthPermissions(context: Context): Boolean {
        if (checkHealthConnectAvailability(context) != HealthConnectAvailability.AVAILABLE) return false
        return try {
            HealthConnectClient.getOrCreate(context).permissionController.getGrantedPermissions()
                .containsAll(REQUIRED_HEALTH_PERMISSIONS)
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) {
            Log.w(TAG, "Could not read health permissions", e)
            false
        }
    }

    fun createPermissionContract(): ActivityResultContract<Set<String>, Set<String>> =
        PermissionController.createRequestPermissionResultContract()

    suspend fun fetchTodayHealthData(context: Context): HealthSyncResult = withContext(Dispatchers.IO) {
        val now = Instant.now()
        val zone = ZoneId.systemDefault()
        val today = now.atZone(zone).toLocalDate()
        fun failure(message: String) = HealthSyncResult(message = message, dateKey = today.toString())
        when (checkHealthConnectAvailability(context)) {
            HealthConnectAvailability.NOT_INSTALLED -> return@withContext failure(
                "Health Connect kurulmalı veya güncellenmeli. Huawei Sağlık paylaşımını da etkinleştirin.")
            HealthConnectAvailability.NOT_SUPPORTED -> return@withContext failure(
                "Bu cihazda Health Connect desteklenmiyor. Huawei Sağlık veri paylaşımı desteğini kontrol edin.")
            HealthConnectAvailability.AVAILABLE -> Unit
        }
        try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            val messages = mutableListOf<String>()
            var steps: Long? = null
            var sleepMinutes: Long? = null
            if (HealthPermission.getReadPermission(StepsRecord::class) in granted) {
                try {
                    val start = today.atStartOfDay(zone).toInstant()
                    if (now.isAfter(start)) {
                        // No raw sums, other sources, hardware fallback or future-time buffer.
                        steps = client.aggregate(AggregateRequest(
                            metrics = setOf(StepsRecord.COUNT_TOTAL),
                            timeRangeFilter = TimeRangeFilter.between(start, now),
                            dataOriginFilter = huaweiOrigin
                        ))[StepsRecord.COUNT_TOTAL]
                    }
                    if (steps == null) messages += "Bugün için Huawei adım kaydı bulunamadı."
                } catch (e: CancellationException) { throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Huawei steps read failed", e)
                    messages += "Huawei adımları okunamadı; izinleri kontrol edip tekrar deneyin."
                }
            } else messages += "Adım okuma izni verilmedi."

            if (HealthPermission.getReadPermission(SleepSessionRecord::class) in granted) {
                try {
                    val records = mutableListOf<SleepSessionRecord>()
                    var token: String? = null
                    do {
                        val response = client.readRecords(ReadRecordsRequest(
                            recordType = SleepSessionRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(
                                today.minusDays(1).atStartOfDay(zone).toInstant(), now),
                            dataOriginFilter = huaweiOrigin,
                            pageToken = token
                        ))
                        records += response.records
                        token = response.pageToken
                    } while (!token.isNullOrEmpty())
                    sleepMinutes = calculateHuaweiSleepMinutes(records, today, zone, now)
                    if (sleepMinutes == null) messages += "Bugün biten Huawei uyku kaydı bulunamadı."
                } catch (e: CancellationException) { throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Huawei sleep read failed", e)
                    messages += "Huawei uykusu okunamadı; izinleri kontrol edip tekrar deneyin."
                }
            } else messages += "Uyku okuma izni verilmedi."
            if (steps == null || sleepMinutes == null) messages +=
                "Huawei Sağlık > Ben > Gizlilik yönetimi > Veri paylaşımı ve yetkilendirme bölümünde " +
                "Health Connect paylaşımını kontrol edin ve saatinizi eşitleyin. " +
                "Bu seçenek yoksa Huawei Sağlık sürümünü ve bölge desteğini kontrol edin. " +
                "Yalnızca Huawei Sağlık kayıtları okunur."
            val minutes = sleepMinutes ?: 0L
            val hours = minutes / 60.0
            HealthSyncResult(
                stepsCount = steps ?: 0L, sleepHours = hours, sleepMinutesTotal = minutes,
                sleepQuality = determineSleepQuality(hours),
                isSleep6hPlus = sleepMinutes != null && isSleepTargetAchieved(hours),
                isWalkGoalMet = steps != null && isWalkTargetAchieved(steps),
                syncedAtMillis = now.toEpochMilli(), isSuccess = steps != null || sleepMinutes != null,
                message = messages.joinToString(" ").ifEmpty { "Huawei adım ve uyku verileri güncellendi." },
                hasStepsData = steps != null, hasSleepData = sleepMinutes != null,
                dateKey = today.toString()
            )
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) {
            Log.e(TAG, "Huawei health sync failed", e)
            failure("Sağlık bağlantısı kurulamadı. Health Connect izinlerini kontrol edip tekrar deneyin.")
        }
    }

    /** Assign sleep to its local wake-up date; union intervals to prevent double counting. */
    internal fun calculateHuaweiSleepMinutes(
        records: List<SleepSessionRecord>, date: LocalDate, zone: ZoneId, now: Instant
    ): Long? {
        val sessions = records.filter {
            it.metadata.dataOrigin.packageName == HUAWEI_HEALTH_PACKAGE &&
                !it.endTime.isAfter(now) && it.endTime.atZone(zone).toLocalDate() == date
        }
        if (sessions.isEmpty()) return null
        val sleepingStages = setOf(SleepSessionRecord.STAGE_TYPE_UNKNOWN,
            SleepSessionRecord.STAGE_TYPE_SLEEPING, SleepSessionRecord.STAGE_TYPE_LIGHT,
            SleepSessionRecord.STAGE_TYPE_DEEP, SleepSessionRecord.STAGE_TYPE_REM)
        val sleepingIntervals = sessions.flatMap { session ->
            if (session.stages.isEmpty()) listOf(session.startTime to session.endTime)
            else session.stages.filter { it.stage in sleepingStages }.map {
                maxOf(session.startTime, it.startTime) to minOf(session.endTime, it.endTime)
            }
        }
        val awakeIntervals = sessions.flatMap { session ->
            session.stages.filter { it.stage !in sleepingStages }.map {
                maxOf(session.startTime, it.startTime) to minOf(session.endTime, it.endTime)
            }
        }
        val sleeping = mergeIntervals(sleepingIntervals)
        val awake = mergeIntervals(awakeIntervals)
        // Detailed awake stages also take precedence over an overlapping, unstaged summary.
        var total = Duration.ZERO
        for ((start, end) in sleeping) {
            total = total.plus(Duration.between(start, end))
            for ((awakeStart, awakeEnd) in awake) {
                val overlapStart = maxOf(start, awakeStart)
                val overlapEnd = minOf(end, awakeEnd)
                if (overlapEnd.isAfter(overlapStart)) {
                    total = total.minus(Duration.between(overlapStart, overlapEnd))
                }
            }
        }
        return total.toMinutes()
    }

    private fun mergeIntervals(intervals: List<Pair<Instant, Instant>>): List<Pair<Instant, Instant>> {
        val merged = mutableListOf<Pair<Instant, Instant>>()
        for ((start, end) in intervals.filter { it.second.isAfter(it.first) }.sortedBy { it.first }) {
            val previous = merged.lastOrNull()
            if (previous == null || start.isAfter(previous.second)) merged += start to end
            else merged[merged.lastIndex] = previous.first to maxOf(previous.second, end)
        }
        return merged
    }

    fun determineSleepQuality(hours: Double): String = when {
        hours >= 7.5 -> "refreshed"
        hours >= 6.0 -> "normal"
        else -> "tired"
    }
    fun isWalkTargetAchieved(steps: Long): Boolean = steps >= WALK_STEP_TARGET
    fun isSleepTargetAchieved(hours: Double): Boolean = hours >= SLEEP_HOURS_TARGET

    fun launchInstalledHealthApp(context: Context): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(HUAWEI_HEALTH_PACKAGE)
        if (intent == null) {
            Toast.makeText(context, "Huawei Sağlık yüklü değil.", Toast.LENGTH_LONG).show()
            return false
        }
        return try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Huawei Sağlık açılamadı.", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun launchHealthConnectOrStore(context: Context) {
        try {
            context.startActivity(Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: Exception) {
                Toast.makeText(context, "Health Connect açılamadı.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveLastSync(context: Context, todayKey: String, result: HealthSyncResult) {
        if (!result.isSuccess || (!result.hasStepsData && !result.hasSleepData) ||
            result.source != HUAWEI_SOURCE || todayKey != result.dateKey) return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putLong("steps_$todayKey", result.stepsCount)
            .putLong("sleep_minutes_$todayKey", result.sleepMinutesTotal)
            .putBoolean("has_steps_$todayKey", result.hasStepsData)
            .putBoolean("has_sleep_$todayKey", result.hasSleepData)
            .putString("message_$todayKey", result.message)
            .putLong("time_$todayKey", result.syncedAtMillis).apply()
    }

    fun getLastSync(context: Context, todayKey: String): HealthSyncResult? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val time = prefs.getLong("time_$todayKey", 0L)
        if (time == 0L) return null
        val steps = prefs.getLong("steps_$todayKey", 0L)
        val minutes = prefs.getLong("sleep_minutes_$todayKey", 0L)
        val hours = minutes / 60.0
        val hasSteps = prefs.getBoolean("has_steps_$todayKey", false)
        val hasSleep = prefs.getBoolean("has_sleep_$todayKey", false)
        return HealthSyncResult(
            stepsCount = steps, sleepHours = hours, sleepMinutesTotal = minutes,
            sleepQuality = determineSleepQuality(hours),
            isSleep6hPlus = hasSleep && isSleepTargetAchieved(hours),
            isWalkGoalMet = hasSteps && isWalkTargetAchieved(steps),
            syncedAtMillis = time, isSuccess = hasSteps || hasSleep,
            message = prefs.getString("message_$todayKey", "") ?: "",
            hasStepsData = hasSteps, hasSleepData = hasSleep, dateKey = todayKey)
    }
}
