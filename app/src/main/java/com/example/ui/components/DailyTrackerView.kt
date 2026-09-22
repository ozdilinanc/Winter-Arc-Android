package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
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
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Tarih formatı (Örn: "Pazartesi, 21 Eylül")
    val formattedDate = remember {
        val formatter = SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("tr"))
        formatter.format(Date()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("tr")) else it.toString() }
    }

    // ----------------------------------------------------
    // 1. Ekran Süresi & Dopamin Kalkanı State
    // ----------------------------------------------------
    var socialUsage by remember { mutableStateOf(ScreenTimeHelper.getTodaySocialUsage(context)) }
    var dopamineStatus by remember(todayKey) {
        mutableStateOf(prefs.getString("dopamine_status_$todayKey", "none") ?: "none")
    }
    var streakCount by remember {
        mutableIntStateOf(prefs.getInt("dopamine_streak", 5))
    }

    // Ekran süresini periyodik/ekran açılışında güncelle
    LaunchedEffect(Unit) {
        socialUsage = ScreenTimeHelper.getTodaySocialUsage(context)
        if (socialUsage.isPermissionGranted) {
            // Instagram süresi limite göre otomatik statü belirleme
            if (socialUsage.instagramMinutes <= INSTAGRAM_LIMIT_MINUTES) {
                if (dopamineStatus != "maintained") {
                    dopamineStatus = "maintained"
                    prefs.edit().putString("dopamine_status_$todayKey", "maintained").apply()
                }
            } else {
                if (dopamineStatus != "broken") {
                    dopamineStatus = "broken"
                    prefs.edit().putString("dopamine_status_$todayKey", "broken").apply()
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
            prefs.edit().putString("dopamine_status_$todayKey", "maintained").apply()
            Toast.makeText(context, "Dopamin detoksu korundu! 🔥 Serin: $streakCount Gün", Toast.LENGTH_SHORT).show()
        } else {
            dopamineStatus = "broken"
            prefs.edit().putString("dopamine_status_$todayKey", "broken").apply()
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
    var waterMl by remember(todayKey) {
        mutableIntStateOf(prefs.getInt("water_ml_$todayKey", 1500))
    }
    var showHydrationSheet by remember { mutableStateOf(false) }

    fun updateWater(delta: Int) {
        hapticEngine.vibrateSelection()
        val newAmount = (waterMl + delta).coerceIn(0, 5000)
        waterMl = newAmount
        prefs.edit().putInt("water_ml_$todayKey", newAmount).apply()
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
    var lastHealthSync by remember(todayKey) {
        mutableStateOf(HealthSyncManager.getLastSync(context, todayKey))
    }
    var slept6HoursPlus by remember(todayKey) {
        mutableStateOf(prefs.getBoolean("sleep_6h_plus_$todayKey", false))
    }

    // Manuel Adım & Uyku Düzeltme State (Huawei / Google Fit gecikmeli senkronizasyon için)
    var manualStepsOverride by remember(todayKey) {
        mutableLongStateOf(prefs.getLong("manual_steps_override_$todayKey", 0L))
    }
    var showStepEditDialog by remember { mutableStateOf(false) }
    var showSleepEditDialog by remember { mutableStateOf(false) }
    var showScreenTimeDialog by remember { mutableStateOf(false) }
    val effectiveSteps = maxOf(lastHealthSync?.stepsCount ?: 0L, manualStepsOverride)
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

    var routineStatusMap by remember(todayKey) {
        mutableStateOf(
            routineDefinitions.associate { it.id to prefs.getBoolean("${it.id}_$todayKey", false) }
        )
    }

    fun toggleRoutine(id: String) {
        val current = routineStatusMap[id] ?: false
        val updated = !current
        if (updated) hapticEngine.vibrateSkillCompleted() else hapticEngine.vibrateSelection()
        routineStatusMap = routineStatusMap.toMutableMap().also { it[id] = updated }
        prefs.edit().putBoolean("${id}_$todayKey", updated).apply()
    }

    fun applyHealthSyncResult(result: HealthSyncManager.HealthSyncResult) {
        HealthSyncManager.saveLastSync(context, todayKey, result)
        lastHealthSync = result

        val currentEffective = maxOf(result.stepsCount, manualStepsOverride)
        // 1. Adım sayısı 7000+ ise yürüyüş otomatik tamamlanır
        if (currentEffective >= HealthSyncManager.WALK_STEP_TARGET) {
            routineStatusMap = routineStatusMap.toMutableMap().also { it["hab_walk"] = true }
            prefs.edit().putBoolean("hab_walk_$todayKey", true).apply()
        }

        // 2. Uyku süresi 6+ saat ise otomatik tamamlanır
        if (result.isSleep6hPlus || (result.sleepMinutesTotal >= 360)) {
            slept6HoursPlus = true
            prefs.edit().putBoolean("sleep_6h_plus_$todayKey", true).apply()
        }
    }

    fun updateManualSteps(newSteps: Long) {
        manualStepsOverride = newSteps
        prefs.edit().putLong("manual_steps_override_$todayKey", newSteps).apply()
        if (newSteps >= HealthSyncManager.WALK_STEP_TARGET) {
            routineStatusMap = routineStatusMap.toMutableMap().also { it["hab_walk"] = true }
            prefs.edit().putBoolean("hab_walk_$todayKey", true).apply()
            hapticEngine.vibrateSkillCompleted()
        } else {
            hapticEngine.vibrateSelection()
        }
        Toast.makeText(context, "Adım sayısı $newSteps olarak güncellendi! ✅", Toast.LENGTH_SHORT).show()
    }

    fun updateManualSleep(hours: Double) {
        val totalMinutes = (hours * 60).toLong()
        val quality = HealthSyncManager.determineSleepQuality(hours)
        val is6hPlus = hours >= HealthSyncManager.SLEEP_HOURS_TARGET
        val updatedResult = HealthSyncManager.HealthSyncResult(
            stepsCount = effectiveSteps,
            sleepHours = hours,
            sleepMinutesTotal = totalMinutes,
            sleepQuality = quality,
            isSleep6hPlus = is6hPlus,
            isWalkGoalMet = effectiveSteps >= HealthSyncManager.WALK_STEP_TARGET,
            source = "Manuel Uyku 🌙",
            syncedAtMillis = System.currentTimeMillis(),
            isSuccess = true
        )
        HealthSyncManager.saveLastSync(context, todayKey, updatedResult)
        lastHealthSync = updatedResult
        slept6HoursPlus = is6hPlus
        prefs.edit().putBoolean("sleep_6h_plus_$todayKey", is6hPlus).apply()
        hapticEngine.vibrateSkillCompleted()
        Toast.makeText(context, "Uyku süresi ${hours}s olarak güncellendi! 🌙", Toast.LENGTH_SHORT).show()
    }

    val healthPermissionLauncher = rememberLauncherForActivityResult(
        contract = HealthSyncManager.createPermissionContract()
    ) { grantedPermissions ->
        if (grantedPermissions.containsAll(HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS)) {
            coroutineScope.launch {
                isSyncingHealth = true
                val result = HealthSyncManager.fetchTodayHealthData(context)
                applyHealthSyncResult(result)
                isSyncingHealth = false
            }
        }
    }

    fun syncHealthData() {
        coroutineScope.launch {
            isSyncingHealth = true
            try {
                val availability = HealthSyncManager.checkHealthConnectAvailability(context)
                if (availability == HealthSyncManager.HealthConnectAvailability.AVAILABLE) {
                    val hasPerms = HealthSyncManager.hasHealthPermissions(context)
                    if (!hasPerms) {
                        healthPermissionLauncher.launch(HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS)
                        return@launch
                    }
                }
                val result = HealthSyncManager.fetchTodayHealthData(context)
                applyHealthSyncResult(result)
            } catch (_: Exception) {
            } finally {
                isSyncingHealth = false
            }
        }
    }

    // Ekran açıldığında Huawei Sağlık / Health Connect otomatik eşitlensin!
    LaunchedEffect(Unit) {
        syncHealthData()
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
    val completedHabitsCount = routineStatusMap.count { it.value }
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
            val isDone = routineStatusMap[habit.id] ?: false

            ModernHabitItemRow(
                habit = habit,
                isCompleted = isDone,
                stepsCount = if (habit.id == "hab_walk") effectiveSteps else null,
                onToggle = { toggleRoutine(habit.id) },
                onLaunchClawssary = {
                    try {
                        val launchIntent = context.packageManager.getLaunchIntentForPackage(CLAWSSARY_PACKAGE)
                        if (launchIntent != null) {
                            hapticEngine.vibrateSkillCompleted()
                            context.startActivity(launchIntent)
                            if (!isDone) {
                                toggleRoutine(habit.id)
                            }
                        } else {
                            Toast.makeText(context, "Clawssary uygulaması yüklü görünmüyor.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Uygulama açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
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

    if (showStepEditDialog) {
        StepEditDialog(
            currentSteps = effectiveSteps,
            healthConnectSteps = lastHealthSync?.stepsCount ?: 0L,
            onDismiss = { showStepEditDialog = false },
            onSaveSteps = { newSteps ->
                updateManualSteps(newSteps)
                showStepEditDialog = false
            },
            onLaunchHuaweiHealth = {
                HealthSyncManager.launchInstalledHealthApp(context)
            },
            onRefreshHealthConnect = {
                syncHealthData()
            }
        )
    }

    if (showSleepEditDialog) {
        SleepEditDialog(
            currentMinutes = lastHealthSync?.sleepMinutesTotal ?: 0L,
            currentSource = lastHealthSync?.source ?: "Health Connect",
            onDismiss = { showSleepEditDialog = false },
            onSaveSleep = { hours ->
                updateManualSleep(hours)
                showSleepEditDialog = false
            },
            onLaunchHuaweiHealth = {
                HealthSyncManager.launchInstalledHealthApp(context)
            },
            onLaunchGoogleFit = {
                try {
                    val pm = context.packageManager
                    val intent = pm.getLaunchIntentForPackage("com.google.android.apps.fitness")
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Google Fit yüklü görünmüyor.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Uygulama açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onRefreshHealthConnect = {
                syncHealthData()
            }
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
                    border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Önceki",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Takvim",
                            tint = AccentCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Sonraki",
                            tint = TextDarkMuted,
                            modifier = Modifier.size(16.dp)
                        )
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

    val cardTitle = when {
        lastSync?.source?.contains("Google Fit") == true && lastSync.source.contains("Huawei") -> "HUAWEI & FIT UYKU"
        lastSync?.source?.contains("Google Fit") == true -> "GOOGLE FIT UYKU"
        lastSync?.source?.contains("Huawei") == true -> "HUAWEI SAĞLIK UYKU"
        lastSync?.source?.contains("Manuel") == true -> "MANUEL UYKU GİRİŞİ"
        else -> "OTOMATİK UYKU TAKİBİ"
    }

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
                        text = if (totalMins > 0) "${hours}s ${mins}dk" else "Kayıt Yok • Dokun",
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

                if (stepsCount != null && stepsCount > 0L) {
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

            // Sağ Butonlar: Yürüyüş için Huawei Eşitle, İngilizce için Clawssary, diğerleri için XP
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
                        .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tracker_water_drop),
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
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
                        checkedThumbColor = BranchTools,
                        checkedTrackColor = BranchTools.copy(alpha = 0.35f),
                        uncheckedThumbColor = TextDarkMuted,
                        uncheckedTrackColor = BorderSubtle
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 7: ADIM EŞİTLEME & DÜZELTME DİYALOĞU
// ====================================================================
@Composable
private fun StepEditDialog(
    currentSteps: Long,
    healthConnectSteps: Long,
    onDismiss: () -> Unit,
    onSaveSteps: (Long) -> Unit,
    onLaunchHuaweiHealth: () -> Unit,
    onRefreshHealthConnect: () -> Unit
) {
    var textInput by remember { mutableStateOf(if (currentSteps > 0) currentSteps.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = PanelNavyElevated,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⌚", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Adım Eşitleme",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
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
                // Bilgilendirme ve Health Connect Durumu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyHighlight),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Health Connect Kaydı:",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                            Text(
                                text = "$healthConnectSteps adım",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Huawei Sağlık, saatindeki adımları sisteme belirli aralıklarla aktarır. Saatinle eşitlemek için Huawei Sağlık'ı açıp aşağı kaydırarak senkronize edebilirsin.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hızlı Butonlar: Huawei Sağlık'ı Aç & Yeniden Oku
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onLaunchHuaweiHealth,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan.copy(alpha = 0.15f),
                            contentColor = AccentCyan
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Huawei Sağlık",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = onRefreshHealthConnect,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BranchTools.copy(alpha = 0.15f),
                            contentColor = BranchTools
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tekrar Oku",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Elle Adım Girişi
                Text(
                    text = "Veya saatindeki güncel adımı yaz:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                            textInput = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Örn: 7518", color = TextDarkMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentCyan
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hızlı Seçim Rozetleri (+500, +1.000, 7000 Hedef, Sıfırla)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "+500" to 500L,
                        "+1.000" to 1000L,
                        "7.000 Hedef" to 7000L
                    ).forEach { (label, delta) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelNavyHighlight,
                            border = BorderStroke(0.5.dp, BorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val cur = textInput.toLongOrNull() ?: currentSteps
                                    val newVal = if (delta == 7000L) 7000L else cur + delta
                                    textInput = newVal.toString()
                                }
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    fontSize = 10.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PanelNavyHighlight,
                        border = BorderStroke(0.5.dp, BorderSubtle),
                        modifier = Modifier
                            .weight(0.8f)
                            .clickable {
                                textInput = "0"
                            }
                    ) {
                        Text(
                            text = "Sıfırla",
                            modifier = Modifier.padding(vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = StatusFailed,
                                fontSize = 10.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = textInput.toLongOrNull() ?: 0L
                    onSaveSteps(parsed)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = CanvasDark
                )
            ) {
                Text(
                    text = "Kaydet",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Vazgeç",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                )
            }
        }
    )
}

// ====================================================================
// ALT BİLEŞEN 8: UYKU EŞİTLEME & DÜZELTME DİYALOĞU
// ====================================================================
@Composable
private fun SleepEditDialog(
    currentMinutes: Long,
    currentSource: String,
    onDismiss: () -> Unit,
    onSaveSleep: (Double) -> Unit,
    onLaunchHuaweiHealth: () -> Unit,
    onLaunchGoogleFit: () -> Unit,
    onRefreshHealthConnect: () -> Unit
) {
    val currentHours = if (currentMinutes > 0) String.format(Locale.US, "%.1f", currentMinutes / 60.0) else ""
    var textInput by remember { mutableStateOf(currentHours) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = PanelNavyElevated,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌙", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Uyku Takibi & Eşitleme",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
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
                // Bilgilendirme ve Health Connect Durumu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyHighlight),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mevcut Kayıt:",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                            Text(
                                text = if (currentMinutes > 0) "${currentMinutes / 60}s ${currentMinutes % 60}dk" else "Kayıt Yok",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentMinutes >= 360) StatusCompleted else AccentPurple
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kaynak: $currentSource\nHuawei ve Google Fit uyku seansını bitirdikten sonra Health Connect'e aktarır.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hızlı Uygulama Açma Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onLaunchHuaweiHealth,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentCyan.copy(alpha = 0.15f),
                            contentColor = AccentCyan
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Huawei ⌚",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = onLaunchGoogleFit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BranchEnglish.copy(alpha = 0.15f),
                            contentColor = BranchEnglish
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Google Fit 🏃",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = onRefreshHealthConnect,
                        modifier = Modifier.weight(0.9f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BranchTools.copy(alpha = 0.15f),
                            contentColor = BranchTools
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Yenile",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Elle Uyku Saati Girişi
                Text(
                    text = "Veya dün geceki uykunu saat olarak gir:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { newValue ->
                        if (newValue.length <= 4 && newValue.all { it.isDigit() || it == '.' }) {
                            textInput = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Örn: 7.5", color = TextDarkMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentPurple
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hızlı Seçim Rozetleri (6.0s, 7.0s, 7.5s Hedef, 8.0s, Sıfırla)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    listOf(
                        "6.0s" to 6.0,
                        "7.0s" to 7.0,
                        "7.5s" to 7.5,
                        "8.0s" to 8.0
                    ).forEach { (label, hours) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelNavyHighlight,
                            border = BorderStroke(0.5.dp, BorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    textInput = hours.toString()
                                }
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    fontSize = 10.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PanelNavyHighlight,
                        border = BorderStroke(0.5.dp, BorderSubtle),
                        modifier = Modifier
                            .weight(0.8f)
                            .clickable {
                                textInput = "0"
                            }
                    ) {
                        Text(
                            text = "Sıfırla",
                            modifier = Modifier.padding(vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = StatusFailed,
                                fontSize = 10.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = textInput.toDoubleOrNull() ?: 0.0
                    onSaveSleep(parsed)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = CanvasDark
                )
            ) {
                Text(
                    text = "Kaydet",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Vazgeç",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                )
            }
        }
    )
}

// ====================================================================
// ALT BİLEŞEN 8: EKRAN SÜRESİ VE DOPAMİN DETAY DİYALOĞU
// ====================================================================
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

