package com.example.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.Calendar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillNode
import com.example.ui.theme.*
import com.example.ui.util.HealthSyncManager
import com.example.ui.util.ScreenTimeHelper
import com.example.ui.util.WinterArcNotificationHelper
import com.example.ui.util.rememberHapticEngine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyHabitItem(
    val id: String,
    val title: String,
    val category: String,
    val icon: String,
    val xpValue: Int,
    val isCompleted: Boolean = false
)

private const val INSTAGRAM_LIMIT_MINUTES = 45L
private const val CLAWSSARY_PACKAGE = "com.ozdilinanc.clawssary"

@Composable
fun DailyTrackerView(
    focusSkill: SkillNode? = null,
    onCompleteSkill: ((SkillNode) -> Unit)? = null,
    onCycleFocus: (() -> Unit)? = null,
    onOpenThemePicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticEngine = rememberHapticEngine()
    val prefs = remember { context.getSharedPreferences("winter_arc_daily_tracker", Context.MODE_PRIVATE) }

    fun getSystemTodayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var systemTodayKey by remember { mutableStateOf(getSystemTodayKey()) }
    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    val selectedDateKey = remember(selectedCalendar) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.time)
    }
    val isViewingToday = (selectedDateKey == systemTodayKey)

    // Tarih etiketi (Örn: "Bugün, 22 Eylül" veya "Dün, 21 Eylül" veya "Pazartesi, 20 Eylül")
    val formattedDate = remember(selectedDateKey, systemTodayKey) {
        val date = selectedCalendar.time
        val calNow = Calendar.getInstance()
        calNow.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calNow.time)

        val baseDayMonth = SimpleDateFormat("d MMMM", Locale.forLanguageTag("tr")).format(date)
        when (selectedDateKey) {
            systemTodayKey -> "Bugün, $baseDayMonth"
            yesterdayKey -> "Dün, $baseDayMonth"
            else -> {
                val fullFormatter = SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("tr"))
                fullFormatter.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("tr")) else it.toString()
                }
            }
        }
    }

    // Tarih gezinme fonksiyonları
    fun navigatePrevDay() {
        hapticEngine.vibrateSelection()
        val newCal = (selectedCalendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        selectedCalendar = newCal
    }

    fun navigateNextDay() {
        if (!isViewingToday) {
            hapticEngine.vibrateSelection()
            val newCal = (selectedCalendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            selectedCalendar = newCal
        }
    }

    fun resetToToday() {
        hapticEngine.vibrateSelection()
        selectedCalendar = Calendar.getInstance()
    }

    // Gece yarısı / gün değişimi veya uygulama arka plandan öne geldiğinde otomatik yenileme
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, context) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val freshToday = getSystemTodayKey()
                if (freshToday != systemTodayKey) {
                    val wasToday = (selectedDateKey == systemTodayKey)
                    systemTodayKey = freshToday
                    if (wasToday) {
                        selectedCalendar = Calendar.getInstance()
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
        }
        val dateReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val freshToday = getSystemTodayKey()
                if (freshToday != systemTodayKey) {
                    val wasToday = (selectedDateKey == systemTodayKey)
                    systemTodayKey = freshToday
                    if (wasToday) {
                        selectedCalendar = Calendar.getInstance()
                    }
                }
            }
        }
        context.registerReceiver(dateReceiver, filter)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            try {
                context.unregisterReceiver(dateReceiver)
            } catch (_: Exception) {}
        }
    }

    // ----------------------------------------------------
    // 1. Ekran Süresi & Dopamin Kalkanı State
    // ----------------------------------------------------
    var socialUsage by remember { mutableStateOf(ScreenTimeHelper.getTodaySocialUsage(context)) }
    var dopamineStatus by remember(selectedDateKey) {
        mutableStateOf(prefs.getString("dopamine_status_$selectedDateKey", "none") ?: "none")
    }
    var streakCount by remember {
        mutableIntStateOf(prefs.getInt("dopamine_streak", 5))
    }

    // Ekran süresini periyodik/ekran açılışında güncelle
    LaunchedEffect(systemTodayKey, isViewingToday) {
        if (isViewingToday) {
            socialUsage = ScreenTimeHelper.getTodaySocialUsage(context)
            if (socialUsage.isPermissionGranted) {
                // Instagram süresi limite göre otomatik statü belirleme
                if (socialUsage.instagramMinutes <= INSTAGRAM_LIMIT_MINUTES) {
                    if (dopamineStatus != "maintained") {
                        dopamineStatus = "maintained"
                        prefs.edit().putString("dopamine_status_$systemTodayKey", "maintained").apply()
                    }
                } else {
                    if (dopamineStatus != "broken") {
                        dopamineStatus = "broken"
                        prefs.edit().putString("dopamine_status_$systemTodayKey", "broken").apply()
                    }
                }
            }
        }
    }

    fun setDopamineManual(maintained: Boolean) {
        hapticEngine.vibrateStepCompleted()
        if (maintained) {
            if (dopamineStatus != "maintained") {
                streakCount += 1
                prefs.edit().putInt("dopamine_streak", streakCount).apply()
            }
            dopamineStatus = "maintained"
            prefs.edit().putString("dopamine_status_$selectedDateKey", "maintained").apply()
            Toast.makeText(context, "Dopamin detoksu korundu! 🔥 Serin: $streakCount Gün", Toast.LENGTH_SHORT).show()
        } else {
            dopamineStatus = "broken"
            prefs.edit().putString("dopamine_status_$selectedDateKey", "broken").apply()
            streakCount = 0
            prefs.edit().putInt("dopamine_streak", 0).apply()
            Toast.makeText(context, "Detoks bozuldu. Yeniden odaklan!", Toast.LENGTH_SHORT).show()
        }
    }

    // ----------------------------------------------------
    // 2. Su Takibi State
    // ----------------------------------------------------
    var targetWaterMl by remember {
        mutableIntStateOf(prefs.getInt("water_target_ml", 3000))
    }
    var waterMl by remember(selectedDateKey) {
        mutableIntStateOf(prefs.getInt("water_ml_$selectedDateKey", 0))
    }
    var showHydrationSheet by remember { mutableStateOf(false) }

    fun updateWater(delta: Int) {
        hapticEngine.vibrateSelection()
        val newAmount = (waterMl + delta).coerceIn(0, 5000)
        waterMl = newAmount
        prefs.edit().putInt("water_ml_$selectedDateKey", newAmount).apply()
    }

    fun updateTargetWater(newTarget: Int) {
        hapticEngine.vibrateLevelUp()
        targetWaterMl = newTarget
        prefs.edit().putInt("water_target_ml", newTarget).apply()
    }

    // ----------------------------------------------------
    // 3. Sağlık (Huawei Saat Uyku & Adım) Otomatik State
    // ----------------------------------------------------
    val coroutineScope = rememberCoroutineScope()
    var isSyncingHealth by remember { mutableStateOf(false) }
    val healthResultsByDate = remember { mutableStateMapOf<String, HealthSyncManager.HealthSyncResult>() }
    val healthMessagesByDate = remember { mutableStateMapOf<String, String>() }
    val cachedHealthSync = remember(selectedDateKey) {
        HealthSyncManager.getLastSync(context, selectedDateKey)
    }
    val lastHealthSync = healthResultsByDate[selectedDateKey] ?: cachedHealthSync
    val slept6HoursPlus = lastHealthSync?.isSleep6hPlus == true
    val healthSyncMessage = healthMessagesByDate[selectedDateKey] ?: lastHealthSync?.message
        ?: "Huawei Sağlık bağlantısını kontrol etmek için eşitleyin."
    var showStepEditDialog by remember { mutableStateOf(false) }
    var showSleepEditDialog by remember { mutableStateOf(false) }
    var showScreenTimeDialog by remember { mutableStateOf(false) }
    val effectiveSteps = lastHealthSync?.stepsCount ?: 0L
    // ----------------------------------------------------
    // 4. Sadeleştirilmiş 3 Temel Rutin (Yürüyüş, Kitap, İngilizce)
    // ----------------------------------------------------
    val routineDefinitions = remember {
        listOf(
            DailyHabitItem("hab_walk", "Günlük Yürüyüş (7.000+ Adım)", "Hareket", "walk", 30),
            DailyHabitItem("hab_reading", "Kitap Okuma (20-30 Sayfa)", "Kültür", "reading", 30),
            DailyHabitItem("hab_english", "İngilizce & Kelime Pratiği", "Dil", "english", 30)
        )
    }

    var routineStatusMap by remember(selectedDateKey) {
        mutableStateOf(
            routineDefinitions.filter { it.id != "hab_walk" }.associate {
                it.id to prefs.getBoolean("${it.id}_$selectedDateKey", false)
            }
        )
    }

    fun toggleRoutine(id: String) {
        if (id == "hab_walk") {
            showStepEditDialog = true
            return
        }
        val current = routineStatusMap[id] ?: false
        val updated = !current
        if (updated) hapticEngine.vibrateSkillCompleted() else hapticEngine.vibrateSelection()
        routineStatusMap = routineStatusMap.toMutableMap().also { it[id] = updated }
        prefs.edit().putBoolean("${id}_$selectedDateKey", updated).apply()
    }

    fun applyHealthSyncResult(result: HealthSyncManager.HealthSyncResult) {
        // Carry the queried date across midnight and date navigation.
        HealthSyncManager.saveLastSync(context, result.dateKey, result)
        val verified = if (result.isSuccess) result
            else HealthSyncManager.getLastSync(context, result.dateKey)
        healthMessagesByDate[result.dateKey] = result.message +
            if (!result.isSuccess && verified != null) " Son başarılı okuma gösteriliyor." else ""
        if (verified != null) {
            healthResultsByDate[result.dateKey] = verified
        }
    }

    val healthPermissionLauncher = rememberLauncherForActivityResult(
        contract = HealthSyncManager.createPermissionContract()
    ) { _ ->
        // Partial permission grants must still read available data and report missing permissions.
        coroutineScope.launch {
            if (isSyncingHealth) return@launch
            isSyncingHealth = true
            try {
                applyHealthSyncResult(HealthSyncManager.fetchTodayHealthData(context))
            } finally {
                isSyncingHealth = false
            }
        }
    }

    fun syncHealthData(requestPermissions: Boolean = true) {
        if (isSyncingHealth) return
        isSyncingHealth = true
        coroutineScope.launch {
            val syncDateKey = systemTodayKey
            try {
                if (requestPermissions &&
                    HealthSyncManager.checkHealthConnectAvailability(context) ==
                    HealthSyncManager.HealthConnectAvailability.AVAILABLE &&
                    !HealthSyncManager.hasHealthPermissions(context)) {
                    healthPermissionLauncher.launch(HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS)
                    return@launch
                }
                applyHealthSyncResult(HealthSyncManager.fetchTodayHealthData(context))
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                healthMessagesByDate[syncDateKey] =
                    "Sağlık bağlantısı açılamadı. Health Connect izinlerini kontrol edin."
            } finally {
                isSyncingHealth = false
            }
        }
    }

    LaunchedEffect(systemTodayKey) { syncHealthData(requestPermissions = false) }
    val refreshHealthOnResume by rememberUpdatedState(newValue = {
        syncHealthData(requestPermissions = false)
    })
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshHealthOnResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    // ----------------------------------------------------
    // 5. Su Alarmı State
    // ----------------------------------------------------
    val reminderPrefs = remember { WinterArcNotificationHelper.getPrefs(context) }
    var waterReminderEnabled by remember {
        mutableStateOf(reminderPrefs.getBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, true))
    }

    fun toggleWaterReminder(enabled: Boolean) {
        hapticEngine.vibrateSelection()
        waterReminderEnabled = enabled
        reminderPrefs.edit().putBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, enabled).apply()
        WinterArcNotificationHelper.syncAllReminders(context)
        val msg = if (enabled) "Su içme alarmı (15:00) devrede! 💧" else "Su alarmı kapatıldı"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // ----------------------------------------------------
    // İlerleme Oranları Hesaplamaları
    // ----------------------------------------------------
    val completedHabitsCount = routineStatusMap.count { it.value } +
        if (lastHealthSync?.isWalkGoalMet == true) 1 else 0
    val habitRatio = (completedHabitsCount.toFloat() / routineDefinitions.size.toFloat()).coerceIn(0f, 1f)
    val dopamineRatio = if (dopamineStatus == "maintained") 1f else 0f
    val waterRatio = (waterMl.toFloat() / targetWaterMl.toFloat()).coerceIn(0f, 1f)

    val completedTotalGoals = completedHabitsCount +
            (if (dopamineStatus == "maintained") 1 else 0) +
            (if (waterMl >= 2500) 1 else 0) +
            (if (slept6HoursPlus) 1 else 0)
    val totalGoals = routineDefinitions.size + 3 // 3 rutin + dopamin + su + uyku = 6 hedef
    val overallDailyRatio = (completedTotalGoals.toFloat() / totalGoals.toFloat()).coerceIn(0f, 1f)
    val overallPercent = (overallDailyRatio * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ====================================================================
        // 1. HERO SECTION: Nutrio-İlhamlı Büyük Çember & 3'lü Disiplin Halkaları
        // ====================================================================
        item {
            NutrioDailyHeroCard(
                formattedDate = formattedDate,
                isViewingToday = isViewingToday,
                onPrevDay = { navigatePrevDay() },
                onNextDay = { navigateNextDay() },
                onResetToToday = { resetToToday() },
                streakCount = streakCount,
                completedTotalGoals = completedTotalGoals,
                totalGoals = totalGoals,
                overallDailyRatio = overallDailyRatio,
                completedHabitsCount = completedHabitsCount,
                totalHabitsCount = routineDefinitions.size,
                habitRatio = habitRatio,
                waterMl = waterMl,
                targetWaterMl = targetWaterMl,
                waterRatio = waterRatio,
                socialUsage = socialUsage,
                dopamineStatus = dopamineStatus,
                slept6HoursPlus = slept6HoursPlus,
                onOpenThemePicker = onOpenThemePicker,
                onTapRoutineRing = {
                    hapticEngine.vibrateSelection()
                    Toast.makeText(context, "3 Temel Rutin: $completedHabitsCount/3 tamamlandı ✅", Toast.LENGTH_SHORT).show()
                },
                onTapWaterRing = {
                    hapticEngine.vibrateSelection()
                    showHydrationSheet = true
                },
                onTapDopamineRing = {
                    hapticEngine.vibrateSelection()
                    showScreenTimeDialog = true
                }
            )
        }

        // ====================================================================
        // 2. OTOMATİK METRİK 1: HUAWEI SAAT UYKU KAPSÜLÜ (100% Otomatik)
        // ====================================================================
        item {
            HuaweiSleepAutoCard(
                lastSync = lastHealthSync,
                isSyncing = isSyncingHealth,
                slept6hPlus = slept6HoursPlus,
                onRefresh = { syncHealthData() },
                onCardClick = { showSleepEditDialog = true }
            )
        }

        item {
            Text(
                text = healthSyncMessage,
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().clickable { showStepEditDialog = true }
            )
        }
        // ====================================================================
        // 3. METRİK 2: SU & HİDRASYON KAPSÜLÜ (Daily Drink Target)
        // ====================================================================
        item {
            DailyDrinkTargetCard(
                currentMl = waterMl,
                targetMl = targetWaterMl,
                onQuickAdd = {
                    updateWater(it)
                    Toast.makeText(context, "+$it ml su eklendi! (Toplam: ${(waterMl + it)} ml)", Toast.LENGTH_SHORT).show()
                },
                onCardClick = { showHydrationSheet = true }
            )
        }

        // ====================================================================
        // 4. METRİK 3: DOPAMİN & INSTAGRAM EKRAN SÜRESİ KALKANI
        // ====================================================================
        item {
            DopamineScreenTimeCard(
                socialUsage = socialUsage,
                dopamineStatus = dopamineStatus,
                onCardClick = { showScreenTimeDialog = true },
                onOpenSettings = {
                    ScreenTimeHelper.openUsageSettings(context)
                },
                onManualMaintain = { setDopamineManual(true) },
                onManualBreak = { setDopamineManual(false) }
            )
        }

        // ====================================================================
        // 5. GÜNLÜK 3 TEMEL RUTİN (Yürüyüş, Kitap, Clawssary İngilizce)
        // ====================================================================
        item {
            Text(
                text = "GÜNLÜK RUTİNLER (3 HEDEF)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(routineDefinitions.size, key = { routineDefinitions[it].id }) { index ->
            val habit = routineDefinitions[index]
            val isDone = if (habit.id == "hab_walk") lastHealthSync?.isWalkGoalMet == true
                else routineStatusMap[habit.id] ?: false

            ModernHabitItemRow(
                habit = habit,
                isCompleted = isDone,
                stepsCount = if (habit.id == "hab_walk" && lastHealthSync?.hasStepsData == true)
                    effectiveSteps else null,
                onToggle = { toggleRoutine(habit.id) },
                onLaunchClawssary = {
                    val launchIntent = try {
                        context.packageManager.getLaunchIntentForPackage(CLAWSSARY_PACKAGE)
                    } catch (e: Exception) {
                        null
                    }
                    if (launchIntent != null) {
                        try {
                            hapticEngine.vibrateSkillCompleted()
                            context.startActivity(launchIntent)
                            if (!isDone) {
                                toggleRoutine(habit.id)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Uygulama açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Uygulama yüklü değilse doğrudan mağazaya veya bilgilendirmeye yönlendir
                        hapticEngine.vibrateSelection()
                        try {
                            val marketIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$CLAWSSARY_PACKAGE"))
                            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(marketIntent)
                        } catch (_: Exception) {
                            val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$CLAWSSARY_PACKAGE"))
                            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(webIntent)
                        }
                    }
                },
                onLaunchHuaweiHealth = {
                    showStepEditDialog = true
                }
            )
        }

        // ====================================================================
        // 6. SU BİLDİRİMİ KAPSÜLÜ (15:00 Hidrasyon Alarmı)
        // ====================================================================
        item {
            WaterReminderCompactCard(
                waterEnabled = waterReminderEnabled,
                onToggleWater = { toggleWaterReminder(it) },
                onSendTestNotification = {
                    hapticEngine.vibrateLevelUp()
                    WinterArcNotificationHelper.sendTestNotification(context)
                    Toast.makeText(context, "Test su bildirimi gönderildi! 💧", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    if (showStepEditDialog || showSleepEditDialog) {
        HuaweiHealthSyncDialog(
            lastSync = lastHealthSync,
            message = healthSyncMessage,
            isSyncing = isSyncingHealth,
            isViewingToday = isViewingToday,
            onDismiss = {
                showStepEditDialog = false
                showSleepEditDialog = false
            },
            onLaunchHuaweiHealth = { HealthSyncManager.launchInstalledHealthApp(context) },
            onOpenHealthConnect = { HealthSyncManager.launchHealthConnectOrStore(context) },
            onRefresh = { syncHealthData() }
        )
    }
    if (showScreenTimeDialog) {
        ScreenTimeDetailDialog(
            socialUsage = socialUsage,
            dopamineStatus = dopamineStatus,
            onDismiss = { showScreenTimeDialog = false },
            onOpenDigitalWellbeing = {
                ScreenTimeHelper.openDigitalWellbeing(context)
            },
            onOpenSettings = {
                ScreenTimeHelper.openUsageSettings(context)
            },
            onManualMaintain = {
                setDopamineManual(true)
                showScreenTimeDialog = false
            },
            onManualBreak = {
                setDopamineManual(false)
                showScreenTimeDialog = false
            }
        )
    }

    if (showHydrationSheet) {
        HydrationDetailSheet(
            selectedDateKey = selectedDateKey,
            currentWaterMl = waterMl,
            targetWaterMl = targetWaterMl,
            onUpdateWater = { delta -> updateWater(delta) },
            onUpdateTarget = { newTarget -> updateTargetWater(newTarget) },
            onResetWater = { updateWater(-waterMl) },
            onDismiss = { showHydrationSheet = false }
        )
    }
}

// ====================================================================
// ALT BİLEŞEN 1: NUTRIO-STYLE HERO CARD & ÇEMBERLER (Modern Ring Architecture)
// ====================================================================
@Composable
private fun NutrioDailyHeroCard(
    formattedDate: String,
    isViewingToday: Boolean,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetToToday: () -> Unit,
    streakCount: Int,
    completedTotalGoals: Int,
    totalGoals: Int,
    overallDailyRatio: Float,
    completedHabitsCount: Int,
    totalHabitsCount: Int,
    habitRatio: Float,
    waterMl: Int,
    targetWaterMl: Int,
    waterRatio: Float,
    socialUsage: ScreenTimeHelper.SocialMediaUsage,
    dopamineStatus: String,
    slept6HoursPlus: Boolean,
    onOpenThemePicker: () -> Unit,
    onTapRoutineRing: () -> Unit,
    onTapWaterRing: () -> Unit,
    onTapDopamineRing: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.16f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                ),
                RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // ----------------------------------------------------
            // 1. ÜST BAR: Tarih Gezgini (< Bugün, 22 Eylül 📅 >) & Rozetler
            // ----------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PanelNavyHighlight.copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, if (!isViewingToday) AccentAmber.copy(alpha = 0.5f) else BorderSubtle.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPrevDay,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Önceki Gün",
                                tint = TextPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onResetToToday() }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isViewingToday) TextPrimary else AccentAmber,
                                    fontSize = 11.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = if (isViewingToday) "Bugün" else "Bugüne Dön",
                                tint = if (isViewingToday) AccentCyan else AccentAmber,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = onNextDay,
                            enabled = !isViewingToday,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Sonraki Gün",
                                tint = if (!isViewingToday) TextPrimary else TextDarkMuted.copy(alpha = 0.35f),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentAmber.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$streakCount Gün",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentAmber,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------
            // 2. ORTA BÖLÜM: BÜYÜK MERKEZİ ÇEMBER & İKİ YAN METRİK
            // (Nutrio 1190 kcal left, Eaten 1634, Burned 265 Hiyerarşisi)
            // ----------------------------------------------------
            val remainingGoals = (totalGoals - completedTotalGoals).coerceAtLeast(0)
            val isAllCompleted = completedTotalGoals >= totalGoals && totalGoals > 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol Metrik: Tamamlanan Hedefler
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Biten",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$completedTotalGoals",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = "/ $totalGoals hedef",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Merkez: Büyük Ana Çember
                val heroColor = if (isAllCompleted) StatusCompleted else AccentCyan
                val heroTrackColor = Color.White.copy(alpha = 0.08f)

                ModernCircularGauge(
                    progress = overallDailyRatio,
                    strokeWidth = 10.dp,
                    trackColor = heroTrackColor,
                    progressColor = heroColor,
                    modifier = Modifier.size(122.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isAllCompleted) {
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = StatusCompleted,
                                    fontSize = 24.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "tamamlandı",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = StatusCompleted,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = StatusCompleted,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "$remainingGoals",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    fontSize = 32.sp
                                )
                            )
                            Text(
                                text = "hedef kaldı",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Sağ Metrik: Günlük Disiplin XP'si
                val earnedXp = (completedHabitsCount * 30) +
                        (if (dopamineStatus == "maintained") 30 else 0) +
                        (if (waterMl >= 2500) 20 else 0) +
                        (if (slept6HoursPlus) 30 else 0)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kazanılan",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$earnedXp",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = AccentGold,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = "günlük xp",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // İnce Ayırıcı Çizgi
            HorizontalDivider(
                thickness = 0.8.dp,
                color = BorderSubtle.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ----------------------------------------------------
            // 3. ALT BÖLÜM: 3'LÜ TATLI ÇEMBERLER (Carbs / Protein / Fat Stili)
            // ----------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DİSİPLİN ODAKLARI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.1.sp,
                        fontSize = 10.5.sp
                    )
                )
                Text(
                    text = "Hızlı aksiyon için dokun",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDarkMuted,
                        fontSize = 9.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 1. Çember: Rutinler (Emerald Yeşili)
                TrioNutrioSubGauge(
                    progress = habitRatio,
                    primaryValue = "$completedHabitsCount",
                    secondaryValue = "/ $totalHabitsCount",
                    label = "Rutinler",
                    color = Color(0xFF10B981),
                    onClick = onTapRoutineRing,
                    modifier = Modifier.weight(1f)
                )

                // 2. Çember: Su / Hidrasyon (Gök Mavisi)
                val formattedLiters = String.format(Locale.US, "%.1f", waterMl / 1000f)
                val targetLiters = String.format(Locale.US, "%.1f", targetWaterMl / 1000f)
                TrioNutrioSubGauge(
                    progress = waterRatio,
                    primaryValue = formattedLiters,
                    secondaryValue = "/ ${targetLiters}L",
                    label = "Hidrasyon",
                    color = Color(0xFF38BDF8),
                    onClick = onTapWaterRing,
                    modifier = Modifier.weight(1f)
                )

                // 3. Çember: Dopamin / Ekran Süresi (Amber / Turuncu)
                val isInstaPermitted = socialUsage.isPermissionGranted
                val instaMinutes = socialUsage.instagramMinutes
                val isDopamineGood = if (isInstaPermitted) instaMinutes <= INSTAGRAM_LIMIT_MINUTES else dopamineStatus == "maintained"
                val dopamineColor = if (isDopamineGood) Color(0xFFF59E0B) else Color(0xFFEF4444)

                val dopamineProgress = if (isInstaPermitted) {
                    (instaMinutes.toFloat() / INSTAGRAM_LIMIT_MINUTES.toFloat()).coerceIn(0f, 1f)
                } else {
                    if (dopamineStatus == "maintained") 1f else 0.2f
                }

                val primaryText = if (isInstaPermitted) "$instaMinutes" else if (dopamineStatus == "maintained") "✓" else "—"
                val secondaryText = if (isInstaPermitted) "/ ${INSTAGRAM_LIMIT_MINUTES}dk" else if (dopamineStatus == "maintained") "Korundu" else "Kalkan"

                TrioNutrioSubGauge(
                    progress = dopamineProgress,
                    primaryValue = primaryText,
                    secondaryValue = secondaryText,
                    label = "Dopamin",
                    color = dopamineColor,
                    onClick = onTapDopamineRing,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TrioNutrioSubGauge(
    progress: Float,
    primaryValue: String,
    secondaryValue: String,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModernCircularGauge(
            progress = progress,
            strokeWidth = 6.dp,
            trackColor = color.copy(alpha = 0.15f),
            progressColor = color,
            modifier = Modifier.size(76.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = primaryValue,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = secondaryValue,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextDarkMuted,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun ModernCircularGauge(
    progress: Float,
    strokeWidth: androidx.compose.ui.unit.Dp = 8.dp,
    trackColor: Color = Color.White.copy(alpha = 0.08f),
    progressColor: Color = AccentCyan,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "gaugeProgress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
            val strokePx = strokeWidth.toPx()
            val diameter = minOf(size.width, size.height) - strokePx
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)

            // Arka plan tam halkası (Track)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx)
            )

            // İlerleme arkı (animasyonlu, yuvarlak uçlu)
            if (animatedProgress > 0f) {
                val sweep = animatedProgress * 360f
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

// ====================================================================
// ALT BİLEŞEN 2: HUAWEI SAAT UYKU KAPSÜLÜ (100% Otomatik, Butonsuz)
// ====================================================================
@Composable
private fun HuaweiSleepAutoCard(
    lastSync: HealthSyncManager.HealthSyncResult?,
    isSyncing: Boolean,
    slept6hPlus: Boolean,
    onRefresh: () -> Unit,
    onCardClick: () -> Unit
) {
    val totalMins = lastSync?.sleepMinutesTotal ?: 0L
    val hours = totalMins / 60
    val mins = totalMins % 60
    val isGoalMet = slept6hPlus || totalMins >= 360

    val cardTitle = "HUAWEI SAĞLIK UYKU"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isGoalMet) StatusCompleted.copy(alpha = 0.6f) else AccentPurple.copy(alpha = 0.35f),
                RoundedCornerShape(18.dp)
            )
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentPurple.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cardTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentPurple,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• Dokun",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextDarkMuted,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (lastSync?.hasSleepData == true) "${hours}s ${mins}dk" else "Kayıt Yok • Dokun",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isGoalMet) StatusCompleted else TextPrimary,
                            fontSize = 17.sp
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isGoalMet) "6+ saat hedefi aşıldı" else "Hedef: 6+ saat kaliteli uyku",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isGoalMet) StatusCompleted else TextMuted,
                                fontSize = 11.sp
                            )
                        )
                        if (isGoalMet) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusCompleted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Yenileme / Durum İkonu
            IconButton(
                onClick = onRefresh,
                enabled = !isSyncing,
                modifier = Modifier.size(36.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AccentCyan,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Yenile",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}



// ====================================================================
// ALT BİLEŞEN 4: DOPAMİN & INSTAGRAM EKRAN SÜRESİ KALKANI
// ====================================================================
@Composable
private fun DopamineScreenTimeCard(
    socialUsage: ScreenTimeHelper.SocialMediaUsage,
    dopamineStatus: String,
    onCardClick: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onManualMaintain: () -> Unit,
    onManualBreak: () -> Unit
) {
    val isPermitted = socialUsage.isPermissionGranted
    val instaMins = socialUsage.instagramMinutes
    val isUnderLimit = instaMins <= INSTAGRAM_LIMIT_MINUTES

    val borderColor by animateColorAsState(
        targetValue = if (isPermitted) {
            if (isUnderLimit) StatusCompleted.copy(alpha = 0.6f) else Color(0xFFEF4444).copy(alpha = 0.6f)
        } else {
            if (dopamineStatus == "maintained") StatusCompleted.copy(alpha = 0.6f) else BorderSubtle
        },
        label = "dopamineBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onCardClick() }
            .border(1.dp, borderColor, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DOPAMİN KALKANI",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Detay",
                                tint = TextDarkMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = if (isPermitted) "Instagram: $instaMins / $INSTAGRAM_LIMIT_MINUTES dk limit (tıkla)" else "Shorts & dikey kaydırma yok",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isPermitted && !isUnderLimit) Color(0xFFEF4444) else TextMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                // Rozet
                val isMaintained = if (isPermitted) isUnderLimit else dopamineStatus == "maintained"
                val badgeColor = if (isMaintained) StatusCompleted else if (isPermitted) Color(0xFFEF4444) else TextMuted
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isMaintained) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                        } else if (isPermitted && !isUnderLimit) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = if (isMaintained) "KORUNDU" else if (isPermitted) "LİMİT AŞILDI" else "BEKLİYOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isPermitted) {
                // İzin isteme çubuğu & manuel kontrol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = PanelNavyHighlight),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ekran Süresi İzni Ver", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (dopamineStatus == "maintained") StatusCompleted else PanelNavyHighlight,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.height(36.dp).clickable { onManualMaintain() }
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Koru ✓",
                                color = if (dopamineStatus == "maintained") Color.Black else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            } else {
                // İzin verilmiş: Otomatik Ekran Süresi Barı
                val usageRatio = (instaMins.toFloat() / INSTAGRAM_LIMIT_MINUTES.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { usageRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isUnderLimit) StatusCompleted else Color(0xFFEF4444),
                    trackColor = BorderSubtle.copy(alpha = 0.4f)
                )
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 5: MODERN HABIT ITEM ROW (Things 3 / Apple Reminders)
// ====================================================================
@Composable
private fun ModernHabitItemRow(
    habit: DailyHabitItem,
    isCompleted: Boolean,
    stepsCount: Long? = null,
    onToggle: () -> Unit,
    onLaunchClawssary: () -> Unit,
    onLaunchHuaweiHealth: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isCompleted) PanelNavyHighlight.copy(alpha = 0.6f) else PanelNavyElevated,
        border = BorderStroke(1.dp, if (isCompleted) StatusCompleted.copy(alpha = 0.35f) else BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) StatusCompleted else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isCompleted) StatusCompleted else TextDarkMuted,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // İkon Tablası
            val iconBg = when (habit.category) {
                "Hareket" -> AccentCyan.copy(alpha = 0.15f)
                "Kültür" -> AccentPurple.copy(alpha = 0.15f)
                "Dil" -> BranchTools.copy(alpha = 0.15f)
                else -> PanelNavyHighlight
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                when (habit.id) {
                    "hab_walk" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tracker_walk),
                            contentDescription = habit.title,
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    "hab_reading" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tracker_book),
                            contentDescription = habit.title,
                            tint = AccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    "hab_english" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tracker_language),
                            contentDescription = habit.title,
                            tint = BranchTools,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = habit.title,
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Başlık & Not
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isCompleted) FontWeight.Medium else FontWeight.SemiBold,
                        color = if (isCompleted) TextMuted else TextPrimary,
                        fontSize = 13.sp
                    )
                )

                if (stepsCount != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = if (stepsCount >= 7000) StatusCompleted else AccentCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Huawei: $stepsCount / 7.000 Adım",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (stepsCount >= 7000) StatusCompleted else AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                        if (stepsCount >= 7000) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusCompleted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Sağ Butonlar: Yürüyüş için Huawei Eşitle, İngilizce için Clawssary / Kartlar, diğerleri için XP
            if (habit.id == "hab_walk" && onLaunchHuaweiHealth != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentCyan.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { onLaunchHuaweiHealth() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⌚ Eşitle",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            } else if (habit.id == "hab_english") {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BranchTools.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, BranchTools.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { onLaunchClawssary() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Clawssary 🚀",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BranchTools,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            } else {
                // XP Rozeti
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) StatusCompleted.copy(alpha = 0.15f) else AccentGold.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "+${habit.xpValue} XP",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) StatusCompleted else AccentGold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 6: SU BİLDİRİMİ KAPSÜLÜ (15:00 Hidrasyon Alarmı)
// ====================================================================
@Composable
private fun WaterReminderCompactCard(
    waterEnabled: Boolean,
    onToggleWater: (Boolean) -> Unit,
    onSendTestNotification: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tracker_water_drop),
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GÜN ORTASI SU ALARMI (15:00)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 0.6.sp,
                            fontSize = 11.5.sp
                        )
                    )
                    Text(
                        text = "Odak tazelemek için zengin hidrasyon bildirimi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSendTestNotification,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Test",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Switch(
                    checked = waterEnabled,
                    onCheckedChange = onToggleWater,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentCyan,
                        checkedTrackColor = AccentCyan.copy(alpha = 0.35f),
                        uncheckedThumbColor = TextDarkMuted,
                        uncheckedTrackColor = BorderSubtle
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }
    }
}

@Composable
private fun HuaweiHealthSyncDialog(
    lastSync: HealthSyncManager.HealthSyncResult?,
    message: String,
    isSyncing: Boolean,
    isViewingToday: Boolean,
    onDismiss: () -> Unit,
    onLaunchHuaweiHealth: () -> Unit,
    onOpenHealthConnect: () -> Unit,
    onRefresh: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = { Text("Huawei Sağlık eşitleme", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (lastSync?.hasStepsData == true) "Adım: ${lastSync.stepsCount}"
                        else "Adım: Huawei kaydı yok",
                    color = TextPrimary
                )
                Text(
                    text = if (lastSync?.hasSleepData == true)
                        "Uyku: ${lastSync.sleepMinutesTotal / 60}s ${lastSync.sleepMinutesTotal % 60}dk"
                        else "Uyku: Huawei kaydı yok",
                    color = TextPrimary
                )
                if (lastSync != null) {
                    Text(
                        "Son okuma: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            .format(Date(lastSync.syncedAtMillis)),
                        color = TextMuted
                    )
                }
                Text(message, color = TextMuted)
                Text(
                    "Huawei Sağlık'ta saati eşitleyin, ardından Health Connect için adım ve uyku " +
                        "yazma izinlerini açın. Winter Arc'a da adım ve uyku okuma izinlerini verin.",
                    color = TextMuted
                )
                TextButton(onClick = onLaunchHuaweiHealth) { Text("Huawei Sağlık'ı aç") }
                TextButton(onClick = onOpenHealthConnect) { Text("Health Connect izinleri") }
                if (!isViewingToday) Text("Yenileme bugünün verilerini okur.", color = TextMuted)
            }
        },
        confirmButton = {
            TextButton(onClick = onRefresh, enabled = !isSyncing && isViewingToday) {
                Text(if (isSyncing) "Eşitleniyor…" else "İzin ver ve yenile")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Kapat") } }
    )
}
@Composable
private fun ScreenTimeDetailDialog(
    socialUsage: ScreenTimeHelper.SocialMediaUsage,
    dopamineStatus: String,
    onDismiss: () -> Unit,
    onOpenDigitalWellbeing: () -> Unit,
    onOpenSettings: () -> Unit,
    onManualMaintain: () -> Unit,
    onManualBreak: () -> Unit
) {
    val isPermitted = socialUsage.isPermissionGranted
    val instaMins = socialUsage.instagramMinutes
    val isUnderLimit = instaMins <= INSTAGRAM_LIMIT_MINUTES

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧠", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Dopamin & Ekran Süresi",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = if (isPermitted) socialUsage.calculationMethod else "İzin Bekleniyor",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = TextDarkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Öne Çıkan Instagram Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyHighlight),
                    border = BorderStroke(1.dp, if (isPermitted && !isUnderLimit) Color(0xFFEF4444).copy(alpha = 0.5f) else BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📸", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Instagram Süresi",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = "Günlük Limit: $INSTAGRAM_LIMIT_MINUTES dk",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPermitted) {
                                    if (isUnderLimit) StatusCompleted.copy(alpha = 0.18f) else Color(0xFFEF4444).copy(alpha = 0.18f)
                                } else {
                                    PanelNavyElevated
                                }
                            ) {
                                Text(
                                    text = if (isPermitted) {
                                        if (isUnderLimit) "KORUNDU 🔥" else "AŞILDI ⚠️"
                                    } else {
                                        "İZİN YOK"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPermitted) {
                                            if (isUnderLimit) StatusCompleted else Color(0xFFEF4444)
                                        } else TextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = if (isPermitted) "$instaMins dakika" else "İzin Gerekiyor",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPermitted && !isUnderLimit) Color(0xFFEF4444) else TextPrimary,
                                    fontSize = 22.sp
                                )
                            )
                            if (isPermitted) {
                                Text(
                                    text = "%${((instaMins.toFloat() / INSTAGRAM_LIMIT_MINUTES.toFloat()) * 100).toInt()}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnderLimit) StatusCompleted else Color(0xFFEF4444)
                                    )
                                )
                            }
                        }

                        if (isPermitted) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val ratio = (instaMins.toFloat() / INSTAGRAM_LIMIT_MINUTES.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isUnderLimit) StatusCompleted else Color(0xFFEF4444),
                                trackColor = BorderSubtle.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Tespit Edilen Diğer Sosyal Medyalar (TikTok, YouTube, Twitter vb.)
                if (socialUsage.appDetails.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PanelNavyHighlight.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DİĞER SOSYAL MEDYA KULLANIMLARI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            for (app in socialUsage.appDetails) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = app.iconEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = app.appName,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextPrimary,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "${app.minutes} dk",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 3. Teknik Açıklama Kartı (Neye göre ölçüyor?)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyHighlight.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚙️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nasıl Hesaplanıyor?",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Android sistem servisinin (UsageEvents) kaydettiği ACTIVITY_RESUMED ve ACTIVITY_PAUSED olayları toplanır. Uygulamanın ekranda aktif olarak açık kaldığı net süredir; arka plan veya kilitli ekran sayılmaz.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkMuted,
                                fontSize = 10.5.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Doğrulama ve İzin Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenDigitalWellbeing,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan.copy(alpha = 0.15f),
                            contentColor = AccentCyan
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dijital Denge ↗",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (!isPermitted) {
                        Button(
                            onClick = onOpenSettings,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentAmber.copy(alpha = 0.15f),
                                contentColor = AccentAmber
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "İzin Ver ⚙️",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                if (dopamineStatus == "maintained") onManualBreak() else onManualMaintain()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dopamineStatus == "maintained") StatusFailed.copy(alpha = 0.15f) else StatusCompleted.copy(alpha = 0.15f),
                                contentColor = if (dopamineStatus == "maintained") StatusFailed else StatusCompleted
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (dopamineStatus == "maintained") "Boz ✗" else "Koru ✓",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PanelNavyHighlight,
                    contentColor = TextPrimary
                )
            ) {
                Text("Kapat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    )
}

