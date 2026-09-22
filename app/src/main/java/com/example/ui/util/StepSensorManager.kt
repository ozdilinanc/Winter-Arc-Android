package com.example.ui.util

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Cihazın donanım sensörlerinden (TYPE_STEP_COUNTER & TYPE_STEP_DETECTOR)
 * Health Connect veya üçüncü parti uygulama gerekmeksizin doğrudan adım sayısını takip eden yönetici.
 *
 * TYPE_STEP_COUNTER donanımsal olarak cihazın son önyüklemesinden (boot) bu yana atılan toplam adımları tutar.
 * Bu sınıf her gün için bir başlangıç taban değeri (base) saklayarak günün gerçek adımını hesaplar:
 * `Günün Adımı = Anlık Sayaç - Gün Başlangıç Tabanı + Yeniden Başlatma Öncesi Adımlar`
 */
object StepSensorManager {

    private const val TAG = "StepSensorManager"
    private const val PREFS_NAME = "winter_arc_step_sensor"
    private const val KEY_BOOT_BASE_PREFIX = "step_base_"
    private const val KEY_SAVED_TODAY_PREFIX = "step_today_"
    private const val KEY_LAST_RAW_COUNTER = "step_last_raw_counter"
    private const val KEY_LAST_SYNC_DATE = "step_last_sync_date"

    private fun getTodayKey(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Cihazda donanım adım sayar sensörünün bulunup bulunmadığını kontrol eder.
     */
    fun hasStepSensor(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: return false
        return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null ||
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null
    }

    /**
     * Aktivite tanıma (Adım sayma) izninin verilip verilmediğini kontrol eder.
     */
    fun hasActivityRecognitionPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Kaydedilmiş bugünün adım sayısını döndürür.
     */
    fun getTodaySteps(context: Context, dateKey: String = getTodayKey()): Long {
        val prefs = getPrefs(context)
        return prefs.getLong("$KEY_SAVED_TODAY_PREFIX$dateKey", 0L)
    }

    /**
     * Donanım adım sayar sensöründen anlık okuma yaparak bugünün adımını günceller.
     * Coroutine içinde güvenle çağrılabilir.
     */
    suspend fun readLiveHardwareSteps(context: Context): Long {
        if (!hasActivityRecognitionPermission(context)) {
            Log.w(TAG, "ACTIVITY_RECOGNITION izni verilmediği için donanım adım sayar okunamadı.")
            return getTodaySteps(context)
        }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val stepCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (sensorManager == null || stepCounter == null) {
            Log.w(TAG, "TYPE_STEP_COUNTER sensörü bulunamadı.")
            return getTodaySteps(context)
        }

        // Sensörden ilk değeri alana kadar maksimum 1.5 saniye bekle
        val rawCounter = withTimeoutOrNull(1500L) {
            suspendCancellableCoroutine<Float?> { cont ->
                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER && event.values.isNotEmpty()) {
                            val value = event.values[0]
                            try {
                                sensorManager.unregisterListener(this)
                            } catch (_: Exception) {}
                            if (cont.isActive) cont.resume(value)
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }

                val registered = sensorManager.registerListener(
                    listener,
                    stepCounter,
                    SensorManager.SENSOR_DELAY_FASTEST
                )

                if (!registered) {
                    if (cont.isActive) cont.resume(null)
                }

                cont.invokeOnCancellation {
                    try {
                        sensorManager.unregisterListener(listener)
                    } catch (_: Exception) {}
                }
            }
        }

        return if (rawCounter != null && rawCounter > 0f) {
            calculateAndSaveTodaySteps(context, rawCounter.toLong())
        } else {
            getTodaySteps(context)
        }
    }

    /**
     * Donanım adım sayacının ham değerini işler:
     * - Yeni güne geçildiğinde bugünün taban değerini kaydeder.
     * - Cihaz yeniden başlatılmışsa (rawCounter < lastRawCounter) tabanı günceller.
     */
    fun calculateAndSaveTodaySteps(context: Context, rawCounter: Long): Long {
        val prefs = getPrefs(context)
        val todayKey = getTodayKey()
        val lastRawCounter = prefs.getLong(KEY_LAST_RAW_COUNTER, 0L)
        val lastSyncDate = prefs.getString(KEY_LAST_SYNC_DATE, "") ?: ""

        val isNewDay = lastSyncDate != todayKey
        val isRebooted = rawCounter < lastRawCounter

        var baseSteps = prefs.getLong("$KEY_BOOT_BASE_PREFIX$todayKey", -1L)

        if (baseSteps == -1L || isNewDay) {
            // Bugün için ilk okuma: Mevcut sayacı taban al
            baseSteps = rawCounter
            prefs.edit()
                .putLong("$KEY_BOOT_BASE_PREFIX$todayKey", baseSteps)
                .putString(KEY_LAST_SYNC_DATE, todayKey)
                .apply()
        } else if (isRebooted) {
            // Cihaz gün içinde yeniden başlatıldıysa sayac sıfırlanmıştır.
            // Tabanı 0'a çekerek yeni boot döngüsünü ekle
            val stepsBeforeReboot = prefs.getLong("$KEY_SAVED_TODAY_PREFIX$todayKey", 0L)
            baseSteps = 0L - stepsBeforeReboot
            prefs.edit()
                .putLong("$KEY_BOOT_BASE_PREFIX$todayKey", baseSteps)
                .apply()
        }

        val calculatedTodaySteps = (rawCounter - baseSteps).coerceAtLeast(0L)

        prefs.edit()
            .putLong(KEY_LAST_RAW_COUNTER, rawCounter)
            .putLong("$KEY_SAVED_TODAY_PREFIX$todayKey", calculatedTodaySteps)
            .apply()

        Log.d(TAG, "Adım Hesabı: Ham Sayaç: $rawCounter, Taban: $baseSteps -> Bugünün Adımı: $calculatedTodaySteps")
        return calculatedTodaySteps
    }

    /**
     * Manuel olarak kullanıcı adım girdiğinde donanım tabanını senkronize eder.
     */
    fun recordManualSteps(context: Context, dateKey: String, manualSteps: Long) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putLong("$KEY_SAVED_TODAY_PREFIX$dateKey", manualSteps)
            .apply()
    }
}
