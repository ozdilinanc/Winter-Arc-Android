package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillNode
import com.example.ui.theme.*
import com.example.ui.util.HealthSyncManager
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

@Composable
fun DailyTrackerView(
    focusSkill: SkillNode?,
    onCompleteSkill: ((SkillNode) -> Unit)? = null,
    onCycleFocus: (() -> Unit)? = null,
    onOpenThemePicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticEngine = rememberHapticEngine()
    val prefs = remember { context.getSharedPreferences("winter_arc_daily_tracker", Context.MODE_PRIVATE) }
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // ----------------------------------------------------
    // Dopamin Detoksu State
    // ----------------------------------------------------
    var dopamineStatus by remember(todayKey) {
        mutableStateOf(prefs.getString("dopamine_status_$todayKey", "none") ?: "none")
    }
    var streakCount by remember {
        mutableIntStateOf(prefs.getInt("dopamine_streak", 5))
    }

    fun setDopamine(maintained: Boolean) {
        hapticEngine.vibrateStepCompleted()
        if (maintained) {
            if (dopamineStatus != "maintained") {
                streakCount += 1
                prefs.edit().putInt("dopamine_streak", streakCount).apply()
            }
            dopamineStatus = "maintained"
            prefs.edit().putString("dopamine_status_$todayKey", "maintained").apply()
            Toast.makeText(context, "Dopamin detoksu korundu! Serin: $streakCount Gün 🔥", Toast.LENGTH_SHORT).show()
        } else {
            dopamineStatus = "broken"
            prefs.edit().putString("dopamine_status_$todayKey", "broken").apply()
            streakCount = 0
            prefs.edit().putInt("dopamine_streak", 0).apply()
            Toast.makeText(context, "Detoks bozuldu. Sorun değil, derin bir nefes al ve yeniden başla!", Toast.LENGTH_SHORT).show()
        }
    }

    // ----------------------------------------------------
    // Su Takibi State
    // ----------------------------------------------------
    val targetWaterMl = 3000
    var waterMl by remember(todayKey) {
        mutableIntStateOf(prefs.getInt("water_ml_$todayKey", 1500))
    }

    fun updateWater(delta: Int) {
        hapticEngine.vibrateSelection()
        val newAmount = (waterMl + delta).coerceIn(0, 4500)
        waterMl = newAmount
        prefs.edit().putInt("water_ml_$todayKey", newAmount).apply()
    }

    // ----------------------------------------------------
    // Uyku Takibi State (6+ Saat Uyku Kontrolü)
    // ----------------------------------------------------
    var slept6HoursPlus by remember(todayKey) {
        mutableStateOf(prefs.getBoolean("sleep_6h_plus_$todayKey", false))
    }
    var sleepQuality by remember(todayKey) {
        mutableStateOf(prefs.getString("sleep_quality_$todayKey", "refreshed") ?: "refreshed")
    }

    fun setSleep6h(status: Boolean) {
        if (status) hapticEngine.vibrateSkillCompleted() else hapticEngine.vibrateSelection()
        slept6HoursPlus = status
        prefs.edit().putBoolean("sleep_6h_plus_$todayKey", status).apply()
        if (status) {
            Toast.makeText(context, "6+ saat kaliteli uyku kaydedildi! ⚡", Toast.LENGTH_SHORT).show()
        }
    }

    fun setQuality(quality: String) {
        hapticEngine.vibrateSelection()
        sleepQuality = quality
        prefs.edit().putString("sleep_quality_$todayKey", quality).apply()
    }

    // ----------------------------------------------------
    // Hatırlatıcı Bildirimler State & Yönetimi
    // ----------------------------------------------------
    val reminderPrefs = remember { WinterArcNotificationHelper.getPrefs(context) }
    var dopamineReminderEnabled by remember {
        mutableStateOf(reminderPrefs.getBoolean(WinterArcNotificationHelper.KEY_DOPAMINE_ENABLED, true))
    }
    var waterReminderEnabled by remember {
        mutableStateOf(reminderPrefs.getBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, true))
    }

    fun toggleDopamineReminder(enabled: Boolean) {
        hapticEngine.vibrateSelection()
        dopamineReminderEnabled = enabled
        reminderPrefs.edit().putBoolean(WinterArcNotificationHelper.KEY_DOPAMINE_ENABLED, enabled).apply()
        WinterArcNotificationHelper.syncAllReminders(context)
        val msg = if (enabled) "Akşam detoks hatırlatıcısı (21:30) aktif edildi! 🔔" else "Akşam hatırlatıcısı kapatıldı"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun toggleWaterReminder(enabled: Boolean) {
        hapticEngine.vibrateSelection()
        waterReminderEnabled = enabled
        reminderPrefs.edit().putBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, enabled).apply()
        WinterArcNotificationHelper.syncAllReminders(context)
        val msg = if (enabled) "Gün içi su hatırlatıcısı (15:00) aktif edildi! 💧" else "Su hatırlatıcısı kapatıldı"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // ----------------------------------------------------
    // Spor & Kişisel Gelişim Rutinleri
    // ----------------------------------------------------
    val routineDefinitions = remember {
        listOf(
            // Bedensel & Hareket
            DailyHabitItem("hab_gym", "Ağırlık / Fitness Antrenmanı", "Spor", "🏋️‍♂️", 40),
            DailyHabitItem("hab_tennis", "Tenis Seansı / Maç Pratiği", "Spor", "🎾", 40),
            DailyHabitItem("hab_walk", "Günlük Yürüyüş (7.000 - 10.000 Adım)", "Hareket", "🚶‍♂️", 25),
            DailyHabitItem("hab_clean_food", "Temiz Beslenme & Şeker/Fast-Food Yok", "Beslenme", "🥗", 30),
            DailyHabitItem("hab_posture", "Masa Başı Postür & Omurga Esnetme", "Sağlık", "🧘", 15),

            // Kişisel Gelişim & Kültür
            DailyHabitItem("hab_reading", "Kitap Okuma (20-30 Sayfa)", "Kültür", "📚", 30),
            DailyHabitItem("hab_anime_manhwa", "Anime / Manhwa Bölümü Okuma & Takip", "Hobi", "⚔️", 20),
            DailyHabitItem("hab_cards", "Kart Numaraları & Parmak Pratiği (15 Dk)", "Hobi", "🃏", 25),
            DailyHabitItem("hab_english", "İngilizce / Kelime Uygulaması Pratiği", "Dil", "🇬🇧", 25)
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

    // ----------------------------------------------------
    // Sağlık Senkronizasyonu State (Health Connect & Huawei Health)
    // ----------------------------------------------------
    val coroutineScope = rememberCoroutineScope()
    var isSyncingHealth by remember { mutableStateOf(false) }
    var lastHealthSync by remember(todayKey) {
        mutableStateOf(HealthSyncManager.getLastSync(context, todayKey))
    }

    fun applyHealthSyncResult(result: HealthSyncManager.HealthSyncResult) {
        HealthSyncManager.saveLastSync(context, todayKey, result)
        lastHealthSync = result

        // 1. Adım ve Yürüyüş kontrolü (7000+ adım ise hab_walk otomatik tamamlanır)
        if (result.stepsCount >= HealthSyncManager.WALK_STEP_TARGET) {
            routineStatusMap = routineStatusMap.toMutableMap().also { it["hab_walk"] = true }
            prefs.edit().putBoolean("hab_walk_$todayKey", true).apply()
        }

        // 2. Uyku kontrolü (6+ saat ise sleep_6h_plus otomatik tamamlanır)
        if (result.isSleep6hPlus) {
            slept6HoursPlus = true
            prefs.edit().putBoolean("sleep_6h_plus_$todayKey", true).apply()
            sleepQuality = result.sleepQuality
            prefs.edit().putString("sleep_quality_$todayKey", result.sleepQuality).apply()
        }

        hapticEngine.vibrateSkillCompleted()
        val sleepHours = result.sleepMinutesTotal / 60
        val sleepMins = result.sleepMinutesTotal % 60
        val msg = if (result.isSuccess) {
            "Sağlık verileri eşitlendi! 🚶 ${result.stepsCount} Adım | 🛌 ${sleepHours}s ${sleepMins}dk"
        } else {
            result.message.ifEmpty { "Sağlık verileri eşitlendi." }
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
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
        } else {
            Toast.makeText(context, "Health Connect izinleri verilmedi.", Toast.LENGTH_SHORT).show()
        }
    }

    fun syncHealthData() {
        hapticEngine.vibrateSelection()
        coroutineScope.launch {
            isSyncingHealth = true
            try {
                val availability = HealthSyncManager.checkHealthConnectAvailability(context)
                if (availability == HealthSyncManager.HealthConnectAvailability.NOT_INSTALLED) {
                    Toast.makeText(context, "Health Connect cihazda yüklü değil. Yönlendiriliyorsunuz...", Toast.LENGTH_SHORT).show()
                    HealthSyncManager.launchHealthConnectOrStore(context)
                    return@launch
                }

                val hasPerms = HealthSyncManager.hasHealthPermissions(context)
                if (!hasPerms && availability == HealthSyncManager.HealthConnectAvailability.AVAILABLE) {
                    healthPermissionLauncher.launch(HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS)
                    return@launch
                }

                val result = HealthSyncManager.fetchTodayHealthData(context)
                applyHealthSyncResult(result)
            } catch (e: Exception) {
                Toast.makeText(context, "Senkronizasyon hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isSyncingHealth = false
            }
        }
    }

    // Toplam tamamlanma hesaplama
    val completedCount = routineStatusMap.count { it.value } +
            (if (dopamineStatus == "maintained") 1 else 0) +
            (if (waterMl >= 2500) 1 else 0) +
            (if (slept6HoursPlus) 1 else 0)

    val totalGoals = routineDefinitions.size + 3 // dopamine, water, sleep
    val overallDailyRatio = (completedCount.toFloat() / totalGoals.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. Header & Daily Summary Card
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WINTER ARC • GÜNLÜK TAKİP",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 1.1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Zihinsel berraklık, disiplin ve beden sağlığı.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Streak Counter Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PanelNavyHighlight,
                                border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = AccentAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$streakCount Gün Seri",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AccentAmber,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bugünkü İlerleme: $completedCount / $totalGoals Görev",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "%${(overallDailyRatio * 100).toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (overallDailyRatio >= 0.8f) StatusCompleted else AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val animatedRatio by animateFloatAsState(targetValue = overallDailyRatio, label = "dailyProgress")
                    LinearProgressIndicator(
                        progress = { animatedRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (overallDailyRatio >= 0.8f) StatusCompleted else AccentCyan,
                        trackColor = BorderSubtle.copy(alpha = 0.4f),
                    )
                }
            }
        }

        // ==========================================
        // 2. Section: Sağlık & Akıllı Saat Senkronizasyonu
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        if (lastHealthSync != null && (lastHealthSync!!.isWalkGoalMet || lastHealthSync!!.isSleep6hPlus))
                            StatusCompleted.copy(alpha = 0.5f)
                        else AccentCyan.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title & Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⌚", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "SAĞLIK SENKRONİZASYONU",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = lastHealthSync?.source ?: "Huawei Sağlık / Health Connect",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (lastHealthSync != null) StatusCompleted.copy(alpha = 0.15f) else PanelNavyHighlight
                        ) {
                            Text(
                                text = if (lastHealthSync != null) "EŞİTLENDİ ✅" else "BEKLENİYOR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (lastHealthSync != null) StatusCompleted else TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid: Adım & Uyku Kartları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Box: Adım (Yürüyüş)
                        val currentSteps = lastHealthSync?.stepsCount ?: 0L
                        val stepProgress = (currentSteps.toFloat() / 10000f).coerceIn(0f, 1f)
                        val stepMet = currentSteps >= HealthSyncManager.WALK_STEP_TARGET

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = PanelNavyHighlight,
                            border = BorderStroke(1.dp, if (stepMet) StatusCompleted.copy(alpha = 0.4f) else BorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🚶 Adım",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                    if (stepMet) {
                                        Text(
                                            text = "Tamam ✅",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = StatusCompleted,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentSteps > 0) "$currentSteps" else "--",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (stepMet) StatusCompleted else TextPrimary,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = if (stepMet) "Hedef aşıldı! (+25 XP)" else "/ 7.000 hedef",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (stepMet) StatusCompleted else TextDarkMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { stepProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (stepMet) StatusCompleted else AccentCyan,
                                    trackColor = BorderSubtle.copy(alpha = 0.4f)
                                )
                            }
                        }

                        // 2. Box: Uyku
                        val sleepMinutes = lastHealthSync?.sleepMinutesTotal ?: 0L
                        val sleepHours = sleepMinutes / 60
                        val sleepMinsRemaining = sleepMinutes % 60
                        val sleepMet = (lastHealthSync?.sleepHours ?: 0.0) >= HealthSyncManager.SLEEP_HOURS_TARGET

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = PanelNavyHighlight,
                            border = BorderStroke(1.dp, if (sleepMet) StatusCompleted.copy(alpha = 0.4f) else BorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🛌 Uyku",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                    if (sleepMet) {
                                        Text(
                                            text = "6+ Saat ⚡",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = StatusCompleted,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (sleepMinutes > 0) "${sleepHours}s ${sleepMinsRemaining}dk" else "--",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (sleepMet) StatusCompleted else TextPrimary,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = if (sleepMinutes > 0) {
                                        when (lastHealthSync?.sleepQuality) {
                                            "refreshed" -> "Dinlenmiş ⚡"
                                            "tired" -> "Yorgun 🥱"
                                            else -> "Normal 💤"
                                        }
                                    } else "Veri bekleniyor",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextDarkMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val sleepProgress = ((lastHealthSync?.sleepHours ?: 0.0) / 8.0).toFloat().coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { sleepProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (sleepMet) StatusCompleted else AccentPurple,
                                    trackColor = BorderSubtle.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: Sync & Open Companion App
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { syncHealthData() },
                            enabled = !isSyncingHealth,
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            if (isSyncingHealth) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Okunuyor...",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Şimdi Senkronize Et",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val launched = HealthSyncManager.launchInstalledHealthApp(context)
                                if (!launched) {
                                    HealthSyncManager.launchHealthConnectOrStore(context)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = PanelNavyHighlight),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Uygulamayı Aç ⌚",
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. Dopamin Detoksu Widget (Bozdum / Bozmadım)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        when (dopamineStatus) {
                            "maintained" -> StatusCompleted.copy(alpha = 0.6f)
                            "broken" -> Color(0xFFEF4444).copy(alpha = 0.5f)
                            else -> AccentCyan.copy(alpha = 0.4f)
                        },
                        RoundedCornerShape(14.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🧠", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DOPAMİN DETOKSU",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (dopamineStatus) {
                                "maintained" -> StatusCompleted.copy(alpha = 0.15f)
                                "broken" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                else -> PanelNavyHighlight
                            }
                        ) {
                            Text(
                                text = when (dopamineStatus) {
                                    "maintained" -> "KORUNDU ✅"
                                    "broken" -> "BOZULDU ❌"
                                    else -> "BEKLENİYOR"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = when (dopamineStatus) {
                                        "maintained" -> StatusCompleted
                                        "broken" -> Color(0xFFEF4444)
                                        else -> TextMuted
                                    },
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Shorts, Reels, TikTok ve sonsuz dikey kaydırma yok. Zihni boş uyaranlardan arındırıp odak derinliğini koru.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: Bozdum / Bozmadım
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { setDopamine(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dopamineStatus == "maintained") StatusCompleted else PanelNavyHighlight
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (dopamineStatus == "maintained") StatusCompleted else BorderSubtle
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (dopamineStatus == "maintained") Color.Black else TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bozmadım (+50 XP)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (dopamineStatus == "maintained") Color.Black else TextPrimary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = { setDopamine(false) },
                            modifier = Modifier.weight(0.7f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (dopamineStatus == "broken") Color(0xFF7F1D1D) else Color.Transparent
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (dopamineStatus == "broken") Color(0xFFEF4444) else BorderSubtle
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = if (dopamineStatus == "broken") Color.White else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bozdum",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (dopamineStatus == "broken") Color.White else TextMuted,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. Su Takibi Widget (Hidrasyon & Zihinsel Berraklık)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💧", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "SU & HİDRASYON",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "Zihinsel berraklık ve odak",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = "$waterMl / $targetWaterMl ml",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (waterMl >= targetWaterMl) StatusCompleted else BranchTools,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val waterProgress = (waterMl.toFloat() / targetWaterMl.toFloat()).coerceIn(0f, 1f)
                    val animatedWaterProgress by animateFloatAsState(targetValue = waterProgress, label = "waterProgress")

                    LinearProgressIndicator(
                        progress = { animatedWaterProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (waterMl >= targetWaterMl) StatusCompleted else BranchTools,
                        trackColor = BorderSubtle.copy(alpha = 0.4f),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: +250ml, +500ml, Sıfırla
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { updateWater(250) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BranchTools.copy(alpha = 0.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = PanelNavyHighlight)
                        ) {
                            Text("+250 ml", color = BranchTools, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = { updateWater(500) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BranchTools.copy(alpha = 0.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = PanelNavyHighlight)
                        ) {
                            Text("+500 ml", color = BranchTools, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = { updateWater(-waterMl) },
                            modifier = Modifier.weight(0.7f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Sıfırla", color = TextDarkMuted, fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. Uyku Düzeni & 6+ Saat Kontrolü (Huawei Sağlık)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        if (slept6HoursPlus) StatusCompleted.copy(alpha = 0.6f) else BorderSubtle,
                        RoundedCornerShape(14.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛌", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "UYKU KALİTESİ & SİRKADİYEN",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "Huawei Sağlık & Sirkadiyen Ritim",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (slept6HoursPlus) StatusCompleted.copy(alpha = 0.15f) else PanelNavyHighlight
                        ) {
                            Text(
                                text = if (slept6HoursPlus) "6+ SAAT ✅" else "YETERSİZ 💤",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (slept6HoursPlus) StatusCompleted else TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Zihinsel berraklık, hafıza konsolidasyonu ve testosteron dengesi için günde en az 6+ saat kaliteli uyku.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: 6+ Saat Uyudum / <6 Saat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { setSleep6h(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (slept6HoursPlus) StatusCompleted else PanelNavyHighlight
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (slept6HoursPlus) StatusCompleted else BorderSubtle
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (slept6HoursPlus) Color.Black else TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "6+ Saat Uyudum (+40 XP)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (slept6HoursPlus) Color.Black else TextPrimary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = { setSleep6h(false) },
                            modifier = Modifier.weight(0.7f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (!slept6HoursPlus) PanelNavyHighlight else Color.Transparent
                            ),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "<6 Saat",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quality tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val qualities = listOf(
                            Triple("refreshed", "Dinlenmiş ⚡", StatusCompleted),
                            Triple("normal", "Normal 💤", AccentCyan),
                            Triple("tired", "Yorgun 🥱", AccentAmber)
                        )

                        qualities.forEach { (qKey, label, accent) ->
                            val isSelected = sleepQuality == qKey
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) accent.copy(alpha = 0.2f) else PanelNavyHighlight,
                                border = BorderStroke(1.dp, if (isSelected) accent else BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { setQuality(qKey) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) accent else TextMuted,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (lastHealthSync != null && lastHealthSync!!.sleepMinutesTotal > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentPurple.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⌚ Senkronize Uyku: ${(lastHealthSync!!.sleepMinutesTotal / 60)}s ${lastHealthSync!!.sleepMinutesTotal % 60}dk (${lastHealthSync!!.source})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentPurple,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Open Health App button
                    OutlinedButton(
                        onClick = {
                            val launched = HealthSyncManager.launchInstalledHealthApp(context)
                            if (!launched) {
                                HealthSyncManager.launchHealthConnectOrStore(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = PanelNavyHighlight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Huawei Sağlık / Saat Uygulamasını Aç ⌚",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // ==========================================
        // 5. Section: Winter Arc Hatırlatıcıları & Bildirimler
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, AccentAmber.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔔", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "HATIRLATICI BİLDİRİMLER",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "Disiplin ve hidrasyon için yerel alarmlar",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (dopamineReminderEnabled || waterReminderEnabled) AccentAmber.copy(alpha = 0.15f) else PanelNavyHighlight
                        ) {
                            Text(
                                text = if (dopamineReminderEnabled || waterReminderEnabled) "ALARM AKTİF" else "KAPALI",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (dopamineReminderEnabled || waterReminderEnabled) AccentAmber else TextMuted,
                                    fontSize = 9.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle 1: Akşam Dopamin & Kapanış (21:30)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PanelNavyHighlight)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔥 Akşam Detoks & Kapanış (21:30)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = "Her akşam seriyi ve alışkanlıkları kaydetme hatırlatması",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 10.5.sp
                                )
                            )
                        }

                        Switch(
                            checked = dopamineReminderEnabled,
                            onCheckedChange = { toggleDopamineReminder(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StatusCompleted,
                                checkedTrackColor = StatusCompleted.copy(alpha = 0.35f),
                                uncheckedThumbColor = TextDarkMuted,
                                uncheckedTrackColor = BorderSubtle
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle 2: Su & Hidrasyon Hatırlatması (15:00)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PanelNavyHighlight)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "💧 Gün Ortası Hidrasyon Alarmı (15:00)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = "Zihinsel berraklık için su içme ve odak tazeleme alarmı",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 10.5.sp
                                )
                            )
                        }

                        Switch(
                            checked = waterReminderEnabled,
                            onCheckedChange = { toggleWaterReminder(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BranchTools,
                                checkedTrackColor = BranchTools.copy(alpha = 0.35f),
                                uncheckedThumbColor = TextDarkMuted,
                                uncheckedTrackColor = BorderSubtle
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Test Bildirimi Gönder Butonu
                    OutlinedButton(
                        onClick = {
                            hapticEngine.vibrateLevelUp()
                            WinterArcNotificationHelper.sendTestNotification(context)
                            Toast.makeText(context, "Test bildirimi gönderildi! 🔔", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AccentCyan.copy(alpha = 0.08f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Test Bildirimi Gönder 🔔",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // ==========================================
        // 6. Section: Bedensel Güç & Spor Protokolü
        // ==========================================
        item {
            Text(
                text = "BEDENSEL GÜÇ & HAREKET PROTOKOLÜ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        val physicalHabits = routineDefinitions.filter { it.category in listOf("Spor", "Hareket", "Beslenme", "Sağlık") }
        items(physicalHabits.size, key = { physicalHabits[it].id }) { index ->
            val habit = physicalHabits[index]
            val isDone = routineStatusMap[habit.id] ?: false
            val extraSubtitle = if (habit.id == "hab_walk" && (lastHealthSync?.stepsCount ?: 0L) > 0L) {
                "👟 Senkronize: ${lastHealthSync!!.stepsCount} Adım"
            } else null

            DailyHabitCard(
                habit = habit,
                isCompleted = isDone,
                extraSubtitle = extraSubtitle,
                onToggle = { toggleRoutine(habit.id) }
            )
        }

        // ==========================================
        // 6. Section: Zihinsel & Kültür Rutinleri
        // ==========================================
        item {
            Text(
                text = "KİŞİSEL GELİŞİM, KÜLTÜR & HOBİ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple,
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        val mentalHabits = routineDefinitions.filter { it.category in listOf("Kültür", "Hobi", "Dil") }
        items(mentalHabits.size, key = { mentalHabits[it].id }) { index ->
            val habit = mentalHabits[index]
            val isDone = routineStatusMap[habit.id] ?: false

            DailyHabitCard(
                habit = habit,
                isCompleted = isDone,
                onToggle = { toggleRoutine(habit.id) }
            )
        }

    }
}

@Composable
private fun DailyHabitCard(
    habit: DailyHabitItem,
    isCompleted: Boolean,
    extraSubtitle: String? = null,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isCompleted) StatusCompleted.copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) PanelNavyHighlight else PanelNavyElevated
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) StatusCompleted else Color.Transparent
                    )
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
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = habit.icon, fontSize = 18.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) TextMuted else TextPrimary,
                        fontSize = 13.sp
                    )
                )
                if (extraSubtitle != null) {
                    Text(
                        text = extraSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isCompleted) StatusCompleted else AccentCyan,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.5.sp
                        )
                    )
                }
                Text(
                    text = habit.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDarkMuted,
                        fontSize = 10.sp
                    )
                )
            }

            Text(
                text = "+${habit.xpValue} XP",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) StatusCompleted else AccentAmber,
                    fontSize = 11.sp
                )
            )
        }
    }
}
