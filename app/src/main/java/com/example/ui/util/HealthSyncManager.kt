package com.example.ui.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Otomatik Sağlık Verisi Senkronizasyon Yöneticisi
 * Huawei Sağlık, Samsung Health, Google Fit ve donanım sensörlerinden
 * günlük adım ve uyku verilerini Health Connect üzerinden çeker.
 */
object HealthSyncManager {

    const val WALK_STEP_TARGET = 7000L
    const val SLEEP_HOURS_TARGET = 6.0

    private const val PREFS_NAME = "winter_arc_health_sync"
    private const val KEY_PREFIX_STEPS = "sync_steps_"
    private const val KEY_PREFIX_SLEEP_HOURS = "sync_sleep_hours_"
    private const val KEY_PREFIX_SLEEP_MINUTES = "sync_sleep_minutes_"
    private const val KEY_PREFIX_SLEEP_QUALITY = "sync_sleep_quality_"
    private const val KEY_PREFIX_SOURCE = "sync_source_"
    private const val KEY_PREFIX_TIME = "sync_time_"

    val REQUIRED_HEALTH_PERMISSIONS: Set<String> by lazy {
        setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class)
        )
    }

    enum class HealthConnectAvailability {
        AVAILABLE,
        NOT_INSTALLED,
        NOT_SUPPORTED
    }

    data class HealthSyncResult(
        val stepsCount: Long = 0L,
        val sleepHours: Double = 0.0,
        val sleepMinutesTotal: Long = 0L,
        val sleepQuality: String = "refreshed", // refreshed, normal, tired
        val isSleep6hPlus: Boolean = false,
        val isWalkGoalMet: Boolean = false,
        val source: String = "Health Connect",
        val syncedAtMillis: Long = System.currentTimeMillis(),
        val isSuccess: Boolean = true,
        val message: String = ""
    )

    /**
     * Cihazdaki Health Connect durumunu kontrol eder.
     */
    fun checkHealthConnectAvailability(context: Context): HealthConnectAvailability {
        val status = HealthConnectClient.getSdkStatus(context)
        return when (status) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    /**
     * Health Connect izinlerinin verilip verilmediğini kontrol eder.
     */
    suspend fun hasHealthPermissions(context: Context): Boolean {
        if (checkHealthConnectAvailability(context) != HealthConnectAvailability.AVAILABLE) {
            return false
        }
        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(REQUIRED_HEALTH_PERMISSIONS)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Health Connect izin isteme kontratı üretir.
     */
    fun createPermissionContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    /**
     * Bugünün adım ve dünkü gecenin uyku verilerini Health Connect üzerinden çeker.
     * Bulunamazsa cihaz donanım sensöründen adımları almayı dener.
     */
    suspend fun fetchTodayHealthData(context: Context): HealthSyncResult = withContext(Dispatchers.IO) {
        val availability = checkHealthConnectAvailability(context)

        if (availability != HealthConnectAvailability.AVAILABLE) {
            return@withContext fetchFromHardwareOrFallback(
                context,
                "Health Connect desteklenmiyor (${availability.name})"
            )
        }

        try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()

            val hasStepsPerm = granted.contains(HealthPermission.getReadPermission(StepsRecord::class))
            val hasSleepPerm = granted.contains(HealthPermission.getReadPermission(SleepSessionRecord::class))

            if (!hasStepsPerm && !hasSleepPerm) {
                return@withContext fetchFromHardwareOrFallback(
                    context,
                    "Health Connect izinleri verilmedi. Lütfen izin verin."
                )
            }

            val now = Instant.now()
            val zoneId = ZoneId.systemDefault()
            val todayDate = LocalDate.now(zoneId)

            // 1. ADIM VERİLERİ (Bugün 00:00 - Şu An + Tampon)
            var totalSteps = 0L
            var stepsReadSuccess = false
            val detectedStepSources = mutableSetOf<String>()
            if (hasStepsPerm) {
                try {
                    val startOfDay = todayDate.atStartOfDay(zoneId).toInstant()
                    val endOfWindow = now.plus(Duration.ofHours(2))

                    // 1. Resmi Aggregate API'si (Health Connect deduplication & öncelik)
                    var aggCount = 0L
                    try {
                        val aggregateRequest = AggregateRequest(
                            metrics = setOf(StepsRecord.COUNT_TOTAL),
                            timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfWindow)
                        )
                        val aggregateResponse = client.aggregate(aggregateRequest)
                        aggCount = aggregateResponse[StepsRecord.COUNT_TOTAL] ?: 0L
                    } catch (e: Exception) {
                        Log.w("HealthSyncManager", "Aggregate failed: ${e.message}")
                    }

                    // 2. Ham Kayıtlar ve Kaynak Dağılımı (Huawei vs Google Fit vb.)
                    val stepsRequest = ReadRecordsRequest(
                        recordType = StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfWindow)
                    )
                    val stepsResponse = client.readRecords(stepsRequest)

                    val stepsByPackage = mutableMapOf<String, Long>()
                    for (record in stepsResponse.records) {
                        val pkg = record.metadata.dataOrigin.packageName
                        detectedStepSources.add(pkg)
                        val cur = stepsByPackage.getOrDefault(pkg, 0L)
                        stepsByPackage[pkg] = cur + record.count
                    }

                    Log.d("HealthSyncManager", "=== HEALTH CONNECT ADIM KAYITLARI ===")
                    Log.d("HealthSyncManager", "Aggregate COUNT_TOTAL: $aggCount adım")
                    Log.d("HealthSyncManager", "Toplam kayıt sayısı: ${stepsResponse.records.size}")
                    for ((pkg, count) in stepsByPackage) {
                        Log.d("HealthSyncManager", "Kaynak [$pkg] -> $count adım")
                    }

                    // Çift saymayı (double counting) önleme:
                    // Kullanıcı hem Huawei hem Google Fit bağladığında her iki uygulama da aynı yürüyüşü yazabilir.
                    // En yüksek adımı sunan tekil kaynak (örn. Huawei saatin gerçek 7518 adımı)
                    val maxSingleSourceSteps = stepsByPackage.values.maxOrNull() ?: 0L

                    // aggCount ile tekil en yüksek kaynak arasındaki güvenilir adım:
                    totalSteps = maxOf(aggCount, maxSingleSourceSteps)
                    stepsReadSuccess = totalSteps > 0L || stepsResponse.records.isNotEmpty()

                    Log.d("HealthSyncManager", "Nihai Seçilen Adım: $totalSteps (agg: $aggCount, maxSingle: $maxSingleSourceSteps)")
                } catch (e: Exception) {
                    Log.e("HealthSyncManager", "Steps read failed", e)
                    totalSteps = 0L
                }
            }

            // 2. UYKU VERİLERİ (Genişletilmiş 72 Saatlik Pencere)
            var totalSleepMinutes = 0L
            var sleepReadSuccess = false
            val detectedSleepSources = mutableSetOf<String>()
            if (hasSleepPerm) {
                try {
                    val sleepQueryStart = now.minus(Duration.ofHours(72))
                    val sleepQueryEnd = now.plus(Duration.ofHours(2))

                    val sleepRequest = ReadRecordsRequest(
                        recordType = SleepSessionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(sleepQueryStart, sleepQueryEnd)
                    )
                    val sleepResponse = client.readRecords(sleepRequest)

                    Log.d("HealthSyncManager", "=== HEALTH CONNECT UYKU KAYITLARI ===")
                    Log.d("HealthSyncManager", "Bulunan uyku oturumu sayısı: ${sleepResponse.records.size}")

                    for (rec in sleepResponse.records) {
                        val duration = Duration.between(rec.startTime, rec.endTime).toMinutes()
                        val origin = rec.metadata.dataOrigin.packageName
                        detectedSleepSources.add(origin)
                        Log.d("HealthSyncManager", "Uyku Seansı: $duration dk | Başlangıç: ${rec.startTime} | Bitiş: ${rec.endTime} | Kaynak: $origin | Not: ${rec.title ?: "Yok"}")
                    }

                    if (sleepResponse.records.isNotEmpty()) {
                        // Öncelik 1: Son 24 saat içinde tamamlanmış uyku seansları (Dün gecenin uykusu)
                        val last24h = now.minus(Duration.ofHours(24))
                        val recentSessions = sleepResponse.records.filter { it.endTime.isAfter(last24h) }

                        val candidateSessions = if (recentSessions.isNotEmpty()) recentSessions else {
                            // Son 24 saatte yoksa, en son kaydedilmiş oturumu al
                            val latestRecord = sleepResponse.records.maxByOrNull { it.endTime }
                            if (latestRecord != null) listOf(latestRecord) else emptyList()
                        }

                        // Aynı geceye ait birden fazla uyku segmenti varsa topla (örn. gece uyanıp tekrar uyuma)
                        totalSleepMinutes = candidateSessions.sumOf { record ->
                            Duration.between(record.startTime, record.endTime).toMinutes()
                        }
                        sleepReadSuccess = totalSleepMinutes > 0L
                        Log.d("HealthSyncManager", "Seçilen Uyku Süresi: $totalSleepMinutes dk (${totalSleepMinutes / 60.0} saat)")
                    }
                } catch (e: Exception) {
                    Log.e("HealthSyncManager", "Sleep read failed", e)
                    totalSleepMinutes = 0L
                }
            }

            // Eğer Health Connect adımları 0 ise ve cihazda donanım sensörü varsa dene
            var isHardwareSensorUsed = false
            if (totalSteps == 0L) {
                val sensorSteps = readHardwareSensorSteps(context)
                if (sensorSteps > 0L) {
                    totalSteps = sensorSteps
                    isHardwareSensorUsed = true
                }
            }

            val sleepHours = totalSleepMinutes / 60.0
            val quality = determineSleepQuality(sleepHours)
            val isSleepMet = isSleepTargetAchieved(sleepHours)
            val isWalkMet = isWalkTargetAchieved(totalSteps)

            // Kaynak adını tespit et (Huawei, Google Fit vb.)
            val allSources = detectedStepSources + detectedSleepSources
            val sourceName = when {
                allSources.any { it.contains("huawei") } && allSources.any { it.contains("fitness") } ->
                    "Huawei & Google Fit ⌚🏃"
                allSources.any { it.contains("huawei") } -> "Huawei Sağlık ⌚"
                allSources.any { it.contains("fitness") } -> "Google Fit 🏃"
                allSources.any { it.contains("shealth") } -> "Samsung Health ⌚"
                isHardwareSensorUsed && allSources.isNotEmpty() -> "Health Connect + Adım Sensörü 👟"
                isHardwareSensorUsed -> "Cihaz Adım Sensörü 👟"
                allSources.isNotEmpty() -> "Health Connect (${allSources.first().substringAfterLast('.')})"
                else -> getInstalledHealthAppName(context) ?: "Health Connect"
            }

            HealthSyncResult(
                stepsCount = totalSteps,
                sleepHours = sleepHours,
                sleepMinutesTotal = totalSleepMinutes,
                sleepQuality = quality,
                isSleep6hPlus = isSleepMet,
                isWalkGoalMet = isWalkMet,
                source = sourceName,
                syncedAtMillis = System.currentTimeMillis(),
                isSuccess = stepsReadSuccess || sleepReadSuccess,
                message = if (stepsReadSuccess || sleepReadSuccess) "Veriler başarıyla senkronize edildi!" else "Sağlık verisi bulunamadı."
            )
        } catch (e: Exception) {
            fetchFromHardwareOrFallback(context, "Senkronizasyon hatası: ${e.localizedMessage}")
        }
    }

    /**
     * Uyku süresine göre uyku kalitesini belirler.
     * >= 7.5 saat: refreshed (Dinlenmiş)
     * 6.0 <= saat < 7.5: normal (Normal)
     * < 6.0 saat: tired (Yorgun)
     */
    fun determineSleepQuality(hours: Double): String {
        return when {
            hours >= 7.5 -> "refreshed"
            hours >= 6.0 -> "normal"
            else -> "tired"
        }
    }

    fun isWalkTargetAchieved(steps: Long): Boolean = steps >= WALK_STEP_TARGET

    fun isSleepTargetAchieved(hours: Double): Boolean = hours >= SLEEP_HOURS_TARGET

    /**
     * Cihazın donanım adım sayar sensöründen anlık veri okumayı dener (fallback).
     */
    private suspend fun readHardwareSensorSteps(context: Context): Long {
        val liveSteps = StepSensorManager.readLiveHardwareSteps(context)
        return if (liveSteps > 0L) liveSteps else StepSensorManager.getTodaySteps(context)
    }

    private suspend fun fetchFromHardwareOrFallback(context: Context, errorReason: String): HealthSyncResult {
        val sensorSteps = readHardwareSensorSteps(context)
        val isWalkMet = isWalkTargetAchieved(sensorSteps)

        // Eğer bugün için daha önceden kaydedilmiş uyku verisi varsa koru
        val todayKey = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault()).format(java.util.Date())
        val previousSync = getLastSync(context, todayKey)
        val sleepHours = previousSync?.sleepHours ?: 0.0
        val sleepMinutes = previousSync?.sleepMinutesTotal ?: 0L
        val sleepQuality = previousSync?.sleepQuality ?: "refreshed"

        val hasSteps = sensorSteps > 0L
        val hasSleep = sleepMinutes > 0L

        val source = when {
            hasSteps && previousSync != null && previousSync.source.contains("Health") -> "${previousSync.source} + Sensör"
            hasSteps -> "Cihaz Adım Sensörü 👟"
            previousSync != null -> previousSync.source
            else -> "Manuel / Bekleniyor"
        }

        return HealthSyncResult(
            stepsCount = if (hasSteps) sensorSteps else (previousSync?.stepsCount ?: 0L),
            sleepHours = sleepHours,
            sleepMinutesTotal = sleepMinutes,
            sleepQuality = sleepQuality,
            isSleep6hPlus = isSleepTargetAchieved(sleepHours),
            isWalkGoalMet = isWalkMet || (previousSync?.isWalkGoalMet == true),
            source = source,
            syncedAtMillis = System.currentTimeMillis(),
            isSuccess = hasSteps || hasSleep,
            message = if (hasSteps) "Donanım adım sayarından $sensorSteps adım okundu." else errorReason
        )
    }

    /**
     * Cihazda yüklü olan sağlık uygulamasını tespit eder.
     */
    fun getInstalledHealthAppName(context: Context): String? {
        val pm = context.packageManager
        val apps = listOf(
            "com.huawei.health" to "Huawei Sağlık ⌚",
            "com.samsung.android.shealth" to "Samsung Health ⌚",
            "com.google.android.apps.fitness" to "Google Fit 🏃",
            "com.google.android.apps.healthdata" to "Google Health Connect 🔗"
        )
        for ((pkg, name) in apps) {
            try {
                pm.getPackageInfo(pkg, 0)
                return name
            } catch (_: PackageManager.NameNotFoundException) {
            }
        }
        return null
    }

    /**
     * Cihazda yüklü sağlık uygulamasını başlatır (Huawei Health, Samsung Health vb.).
     */
    fun launchInstalledHealthApp(context: Context): Boolean {
        val pm = context.packageManager
        val candidatePackages = listOf(
            "com.huawei.health",
            "com.samsung.android.shealth",
            "com.google.android.apps.fitness",
            "com.google.android.apps.healthdata"
        )
        for (pkg in candidatePackages) {
            try {
                val intent = pm.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return true
                }
            } catch (_: Exception) {
            }
        }
        return false
    }

    /**
     * Health Connect ayarlarını veya Google Play Store indirme sayfasını açar.
     */
    fun launchHealthConnectOrStore(context: Context) {
        try {
            // Android 14+ yerleşik ayar sayfası
            val intent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                // Play Store
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.apps.healthdata")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(playStoreIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Health Connect açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Senkronizasyon sonuçlarını SharedPreferences içine kaydeder.
     */
    fun saveLastSync(context: Context, todayKey: String, result: HealthSyncResult) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putLong("$KEY_PREFIX_STEPS$todayKey", result.stepsCount)
            .putString("$KEY_PREFIX_SLEEP_HOURS$todayKey", result.sleepHours.toString())
            .putLong("$KEY_PREFIX_SLEEP_MINUTES$todayKey", result.sleepMinutesTotal)
            .putString("$KEY_PREFIX_SLEEP_QUALITY$todayKey", result.sleepQuality)
            .putString("$KEY_PREFIX_SOURCE$todayKey", result.source)
            .putLong("$KEY_PREFIX_TIME$todayKey", result.syncedAtMillis)
            .apply()
    }

    /**
     * Bugün için önceden kaydedilmiş senkronizasyon sonucunu okur.
     */
    fun getLastSync(context: Context, todayKey: String): HealthSyncResult? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val time = prefs.getLong("$KEY_PREFIX_TIME$todayKey", 0L)
        if (time == 0L) return null

        val steps = prefs.getLong("$KEY_PREFIX_STEPS$todayKey", 0L)
        val sleepHoursStr = prefs.getString("$KEY_PREFIX_SLEEP_HOURS$todayKey", "0.0") ?: "0.0"
        val sleepHours = sleepHoursStr.toDoubleOrNull() ?: 0.0
        val sleepMinutes = prefs.getLong("$KEY_PREFIX_SLEEP_MINUTES$todayKey", 0L)
        val quality = prefs.getString("$KEY_PREFIX_SLEEP_QUALITY$todayKey", "refreshed") ?: "refreshed"
        val source = prefs.getString("$KEY_PREFIX_SOURCE$todayKey", "Health Connect") ?: "Health Connect"

        return HealthSyncResult(
            stepsCount = steps,
            sleepHours = sleepHours,
            sleepMinutesTotal = sleepMinutes,
            sleepQuality = quality,
            isSleep6hPlus = isSleepTargetAchieved(sleepHours),
            isWalkGoalMet = isWalkTargetAchieved(steps),
            source = source,
            syncedAtMillis = time,
            isSuccess = true
        )
    }
}
