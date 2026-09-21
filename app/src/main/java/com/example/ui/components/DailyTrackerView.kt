package com.example.ui.components

import android.content.Context
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
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

private enum class HabitFilterTab(val title: String) {
    ALL("Tümü"),
    PHYSICAL("Bedensel"),
    MENTAL("Zihinsel")
}

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
        val formatter = SimpleDateFormat("EEEE, d MMMM", Locale("tr"))
        formatter.format(Date()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr")) else it.toString() }
    }

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
            Toast.makeText(context, "Detoks bozuldu. Derin nefes al ve yeniden başla!", Toast.LENGTH_SHORT).show()
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
    // Uyku Takibi State (6+ Saat Kontrolü)
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
    // Hatırlatıcı Bildirimler State
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
        val msg = if (enabled) "Akşam kapanış alarmı (21:30) aktif! 🔔" else "Akşam alarmı kapatıldı"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun toggleWaterReminder(enabled: Boolean) {
        hapticEngine.vibrateSelection()
        waterReminderEnabled = enabled
        reminderPrefs.edit().putBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, enabled).apply()
        WinterArcNotificationHelper.syncAllReminders(context)
        val msg = if (enabled) "Su içme alarmı (15:00) aktif! 💧" else "Su alarmı kapatıldı"
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
            DailyHabitItem("hab_anime_manhwa", "Anime / Manhwa Bölümü Okuma", "Hobi", "⚔️", 20),
            DailyHabitItem("hab_cards", "Kart Numaraları & Parmak Pratiği", "Hobi", "🃏", 25),
            DailyHabitItem("hab_english", "İngilizce / Kelime Uygulaması", "Dil", "🇬🇧", 25)
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
    // Sağlık Senkronizasyonu (Health Connect / Huawei Sağlık)
    // ----------------------------------------------------
    val coroutineScope = rememberCoroutineScope()
    var isSyncingHealth by remember { mutableStateOf(false) }
    var lastHealthSync by remember(todayKey) {
        mutableStateOf(HealthSyncManager.getLastSync(context, todayKey))
    }

    fun applyHealthSyncResult(result: HealthSyncManager.HealthSyncResult) {
        HealthSyncManager.saveLastSync(context, todayKey, result)
        lastHealthSync = result

        if (result.stepsCount >= HealthSyncManager.WALK_STEP_TARGET) {
            routineStatusMap = routineStatusMap.toMutableMap().also { it["hab_walk"] = true }
            prefs.edit().putBoolean("hab_walk_$todayKey", true).apply()
        }

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
            "Sağlık eşitlendi: 🚶 ${result.stepsCount} Adım | 🛌 ${sleepHours}s ${sleepMins}dk"
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
                    Toast.makeText(context, "Health Connect cihazda yüklü değil. Mağaza açılıyor...", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Eşitleme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isSyncingHealth = false
            }
        }
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
    val totalGoals = routineDefinitions.size + 3
    val overallDailyRatio = (completedTotalGoals.toFloat() / totalGoals.toFloat()).coerceIn(0f, 1f)
    val overallPercent = (overallDailyRatio * 100).toInt()

    // Habit Kategori Filtresi
    var selectedFilterTab by remember { mutableStateOf(HabitFilterTab.ALL) }

    val filteredHabits = remember(selectedFilterTab, routineStatusMap) {
        when (selectedFilterTab) {
            HabitFilterTab.ALL -> routineDefinitions
            HabitFilterTab.PHYSICAL -> routineDefinitions.filter { it.category in listOf("Spor", "Hareket", "Beslenme", "Sağlık") }
            HabitFilterTab.MENTAL -> routineDefinitions.filter { it.category in listOf("Kültür", "Hobi", "Dil") }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ====================================================================
        // 1. HERO SECTION: iOS Activity Rings & Günlük Özet Dashboard Kartı
        // ====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.03f)
                                )
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header Row: Tarih & Tema Butonu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedDate.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                letterSpacing = 1.2.sp,
                                fontSize = 10.5.sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Streak Kapsülü
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AccentAmber.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Orta Ana Blok: Büyük Başlık ve Activity Rings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Günün Özeti",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    fontSize = 24.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedTotalGoals / $totalGoals Görev Tamamlandı",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        // iOS Activity Rings (Üçlü Halka)
                        TripleActivityRings(
                            habitProgress = habitRatio,
                            dopamineProgress = dopamineRatio,
                            waterProgress = waterRatio,
                            overallPercent = overallPercent,
                            modifier = Modifier.size(96.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mini İlerleme İpuçları (Legend Pills)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PanelNavyHighlight.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendChip(
                            color = StatusCompleted,
                            label = "Rutin",
                            value = "$completedHabitsCount/${routineDefinitions.size}"
                        )
                        Box(modifier = Modifier.width(1.dp).height(14.dp).background(BorderSubtle))
                        LegendChip(
                            color = AccentAmber,
                            label = "Detoks",
                            value = if (dopamineStatus == "maintained") "Korundu" else "Bekliyor"
                        )
                        Box(modifier = Modifier.width(1.dp).height(14.dp).background(BorderSubtle))
                        LegendChip(
                            color = BranchTools,
                            label = "Su",
                            value = "${(waterMl / 1000f).let { String.format(Locale.US, "%.1fL", it) }}"
                        )
                    }
                }
            }
        }

        // ====================================================================
        // Odak Becerisi Bannerı (Eğer varsa)
        // ====================================================================
        if (focusSkill != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PanelNavyElevated,
                    border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎯", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GÜNÜN ODAK YETENEĞİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            Text(
                                text = focusSkill.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1
                            )
                        }

                        if (onCycleFocus != null) {
                            IconButton(
                                onClick = {
                                    hapticEngine.vibrateSelection()
                                    onCycleFocus()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Değiştir",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        if (onCompleteSkill != null) {
                            Button(
                                onClick = { onCompleteSkill(focusSkill) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "Tamamla",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 2. BENTO METRICS: 3 Farklı Şekil ve Estetikteki Metrik Kapsülü
        // ====================================================================

        // Metrik 1: Dopamin Kalkanı
        item {
            DopamineShieldCard(
                status = dopamineStatus,
                onMaintain = { setDopamine(true) },
                onBreak = { setDopamine(false) }
            )
        }

        // Metrik 2: Su & Hidrasyon Sıvı Kapsülü
        item {
            WaterLiquidCard(
                currentMl = waterMl,
                targetMl = targetWaterMl,
                onAddWater = { updateWater(it) },
                onResetWater = { updateWater(-waterMl) }
            )
        }

        // Metrik 3: Sirkadiyen & Uyku Kapsülü (Health Sync)
        item {
            SleepCircadianCard(
                slept6hPlus = slept6HoursPlus,
                sleepQuality = sleepQuality,
                lastSync = lastHealthSync,
                isSyncing = isSyncingHealth,
                onToggleSleep6h = { setSleep6h(!slept6HoursPlus) },
                onSelectQuality = { setQuality(it) },
                onSyncHealth = { syncHealthData() },
                onOpenHealthApp = {
                    val launched = HealthSyncManager.launchInstalledHealthApp(context)
                    if (!launched) {
                        HealthSyncManager.launchHealthConnectOrStore(context)
                    }
                }
            )
        }

        // ====================================================================
        // 3. GÜNLÜK RUTİNLER (Things 3 / Apple Reminders Tarzı Kontrol Listesi)
        // ====================================================================
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GÜNLÜK PROTOKOL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )

                    // Kategori Segment Seçici
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PanelNavyElevated)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        HabitFilterTab.values().forEach { tab ->
                            val isSelected = selectedFilterTab == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentCyan else Color.Transparent)
                                    .clickable {
                                        hapticEngine.vibrateSelection()
                                        selectedFilterTab = tab
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.Black else TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        items(filteredHabits.size, key = { filteredHabits[it].id }) { index ->
            val habit = filteredHabits[index]
            val isDone = routineStatusMap[habit.id] ?: false
            val extraStepNote = if (habit.id == "hab_walk" && (lastHealthSync?.stepsCount ?: 0L) > 0L) {
                "👟 ${lastHealthSync!!.stepsCount} Adım (Eşitlendi)"
            } else null

            ModernHabitItemRow(
                habit = habit,
                isCompleted = isDone,
                extraNote = extraStepNote,
                onToggle = { toggleRoutine(habit.id) }
            )
        }

        // ====================================================================
        // 4. SMART REMINDERS & ALARMLAR (Kompakt Alt Kapsül)
        // ====================================================================
        item {
            RemindersCompactCard(
                dopamineEnabled = dopamineReminderEnabled,
                waterEnabled = waterReminderEnabled,
                onToggleDopamine = { toggleDopamineReminder(it) },
                onToggleWater = { toggleWaterReminder(it) },
                onSendTestNotification = {
                    hapticEngine.vibrateLevelUp()
                    WinterArcNotificationHelper.sendTestNotification(context)
                    Toast.makeText(context, "Test bildirimi gönderildi! 🔔", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 1: TRIPLE ACTIVITY RINGS (Canvas iOS Çizimi)
// ====================================================================
@Composable
private fun TripleActivityRings(
    habitProgress: Float,
    dopamineProgress: Float,
    waterProgress: Float,
    overallPercent: Int,
    modifier: Modifier = Modifier
) {
    val animHabit by animateFloatAsState(targetValue = habitProgress, animationSpec = tween(700), label = "animHabit")
    val animDopamine by animateFloatAsState(targetValue = dopamineProgress, animationSpec = tween(700), label = "animDopamine")
    val animWater by animateFloatAsState(targetValue = waterProgress, animationSpec = tween(700), label = "animWater")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val strokeWidth = 7.dp.toPx()
            val spacing = 3.5.dp.toPx()
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (minOf(size.width, size.height) - strokeWidth) / 2f

            val r1 = maxRadius
            val r2 = maxRadius - strokeWidth - spacing
            val r3 = maxRadius - (strokeWidth + spacing) * 2

            val habitColor = Color(0xFF10B981)   // Emerald
            val dopamineColor = Color(0xFFF59E0B)// Amber
            val waterColor = Color(0xFF38BDF8)   // Sky Blue

            // Tracks (Arka plan silik halkalar)
            drawCircle(color = habitColor.copy(alpha = 0.16f), radius = r1, center = centerOffset, style = Stroke(width = strokeWidth))
            drawCircle(color = dopamineColor.copy(alpha = 0.16f), radius = r2, center = centerOffset, style = Stroke(width = strokeWidth))
            drawCircle(color = waterColor.copy(alpha = 0.16f), radius = r3, center = centerOffset, style = Stroke(width = strokeWidth))

            // Ön plan halkaları
            if (animHabit > 0f) {
                drawArc(
                    color = habitColor,
                    startAngle = -90f,
                    sweepAngle = (animHabit * 360f).coerceIn(0f, 360f),
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - r1, centerOffset.y - r1),
                    size = Size(r1 * 2, r1 * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            if (animDopamine > 0f) {
                drawArc(
                    color = dopamineColor,
                    startAngle = -90f,
                    sweepAngle = (animDopamine * 360f).coerceIn(0f, 360f),
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - r2, centerOffset.y - r2),
                    size = Size(r2 * 2, r2 * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            if (animWater > 0f) {
                drawArc(
                    color = waterColor,
                    startAngle = -90f,
                    sweepAngle = (animWater * 360f).coerceIn(0f, 360f),
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - r3, centerOffset.y - r3),
                    size = Size(r3 * 2, r3 * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Merkez Skor
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%$overallPercent",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            )
            Text(
                text = "SKOR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDarkMuted,
                    fontSize = 8.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
private fun LegendChip(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextDarkMuted,
                fontSize = 11.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 11.sp
            )
        )
    }
}

// ====================================================================
// ALT BİLEŞEN 2: DOPAMİN KALKANI KARTI (Sade & Motive Edici)
// ====================================================================
@Composable
private fun DopamineShieldCard(
    status: String,
    onMaintain: () -> Unit,
    onBreak: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when (status) {
            "maintained" -> StatusCompleted.copy(alpha = 0.6f)
            "broken" -> Color(0xFFEF4444).copy(alpha = 0.6f)
            else -> BorderSubtle
        },
        label = "dopamineBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
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
                        Text(text = "🧠", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DOPAMİN KALKANI",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.8.sp,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Sonsuz kaydırma & ucuz uyaransız zihin",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                // Durum Rozeti
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (status) {
                        "maintained" -> StatusCompleted.copy(alpha = 0.18f)
                        "broken" -> Color(0xFFEF4444).copy(alpha = 0.18f)
                        else -> PanelNavyHighlight
                    }
                ) {
                    Text(
                        text = when (status) {
                            "maintained" -> "KORUNDU 🔥"
                            "broken" -> "BOZULDU ❌"
                            else -> "DEVAM EDİYOR"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (status) {
                                "maintained" -> StatusCompleted
                                "broken" -> Color(0xFFEF4444)
                                else -> TextMuted
                            },
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dokunmatik İki Hap Buton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isMaintained = status == "maintained"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isMaintained) StatusCompleted else PanelNavyHighlight,
                    border = BorderStroke(1.dp, if (isMaintained) StatusCompleted else BorderSubtle),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(40.dp)
                        .clickable { onMaintain() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isMaintained) Color.Black else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Koru & Sürdür",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isMaintained) Color.Black else TextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                val isBroken = status == "broken"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isBroken) Color(0xFF7F1D1D) else Color.Transparent,
                    border = BorderStroke(1.dp, if (isBroken) Color(0xFFEF4444) else BorderSubtle),
                    modifier = Modifier
                        .weight(0.8f)
                        .height(40.dp)
                        .clickable { onBreak() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isBroken) Color.White else TextDarkMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bozdum",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isBroken) Color.White else TextDarkMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 3: SU & HİDRASYON KAPSÜLÜ (Sıvı Göstergesi)
// ====================================================================
@Composable
private fun WaterLiquidCard(
    currentMl: Int,
    targetMl: Int,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit
) {
    val progress = (currentMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "waterProgress")
    val isMet = currentMl >= targetMl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, if (isMet) StatusCompleted.copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(18.dp)),
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
                            .background(BranchTools.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💧", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HİDRASYON",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.8.sp,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = "Hücresel enerji ve odak berraklığı",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                // Hedef Metrik Sayacı
                Text(
                    text = "$currentMl / $targetMl ml",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isMet) StatusCompleted else BranchTools,
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sıvı Gösterge Çubuğu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BorderSubtle.copy(alpha = 0.35f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0284C7),
                                    if (isMet) StatusCompleted else BranchTools
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hızlı Ekleme Butonları (+250, +500, Sıfırla)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PanelNavyHighlight,
                    border = BorderStroke(1.dp, BranchTools.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable { onAddWater(250) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+250 ml",
                            color = BranchTools,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PanelNavyHighlight,
                    border = BorderStroke(1.dp, BranchTools.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable { onAddWater(500) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+500 ml",
                            color = BranchTools,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .width(42.dp)
                        .height(38.dp)
                        .clickable { onResetWater() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sıfırla",
                            tint = TextDarkMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 4: UYKU & SİRKADİYEN KAPSÜLÜ (Health Connect Entegrasyonu)
// ====================================================================
@Composable
private fun SleepCircadianCard(
    slept6hPlus: Boolean,
    sleepQuality: String,
    lastSync: HealthSyncManager.HealthSyncResult?,
    isSyncing: Boolean,
    onToggleSleep6h: () -> Unit,
    onSelectQuality: (String) -> Unit,
    onSyncHealth: () -> Unit,
    onOpenHealthApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (slept6hPlus) StatusCompleted.copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık & Senkronize Rozet
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
                            .background(AccentPurple.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌙", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "UYKU & SİRKADİYEN",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.8.sp,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (lastSync != null && lastSync.sleepMinutesTotal > 0)
                                "${lastSync.sleepMinutesTotal / 60}s ${lastSync.sleepMinutesTotal % 60}dk • ${lastSync.source}"
                            else "6+ saat hedefi & zihinsel toparlanma",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (slept6hPlus) StatusCompleted else TextMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (slept6hPlus) StatusCompleted.copy(alpha = 0.18f) else PanelNavyHighlight
                ) {
                    Text(
                        text = if (slept6hPlus) "6+ SAAT ✅" else "YETERSİZ 💤",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (slept6hPlus) StatusCompleted else TextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kalite Seçim Hapları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val qualities = listOf(
                    Triple("refreshed", "Dinlenmiş ⚡", StatusCompleted),
                    Triple("normal", "Normal 💤", AccentCyan),
                    Triple("tired", "Yorgun 🥱", AccentAmber)
                )

                qualities.forEach { (key, label, accent) ->
                    val isSelected = sleepQuality == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) accent.copy(alpha = 0.18f) else PanelNavyHighlight,
                        border = BorderStroke(1.dp, if (isSelected) accent else BorderSubtle),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clickable { onSelectQuality(key) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) accent else TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Alt Butonlar: 6+ Saat Uyudum Toggle & Sağlık Eşitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (slept6hPlus) StatusCompleted else PanelNavyHighlight,
                    border = BorderStroke(1.dp, if (slept6hPlus) StatusCompleted else BorderSubtle),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .clickable { onToggleSleep6h() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (slept6hPlus) Color.Black else TextPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (slept6hPlus) "6+ Saat Tamam" else "6+ Saat Uyudum",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (slept6hPlus) Color.Black else TextPrimary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PanelNavyHighlight,
                    border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clickable(enabled = !isSyncing) { onSyncHealth() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = AccentCyan,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Eşitleniyor",
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "⌚ Eşitle",
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .width(40.dp)
                        .height(38.dp)
                        .clickable { onOpenHealthApp() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Saat Uygulaması",
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN 5: MODERN HABIT ITEM ROW (Things 3 / Apple Reminders Stili)
// ====================================================================
@Composable
private fun ModernHabitItemRow(
    habit: DailyHabitItem,
    isCompleted: Boolean,
    extraNote: String? = null,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
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
            // Apple Tarzı Yuvarlak Checkbox
            Box(
                modifier = Modifier
                    .size(22.dp)
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
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Renkli Squircle İkon Kutusu
            val iconBg = when (habit.category) {
                "Spor" -> AccentAmber.copy(alpha = 0.15f)
                "Hareket" -> AccentCyan.copy(alpha = 0.15f)
                "Beslenme" -> StatusCompleted.copy(alpha = 0.15f)
                "Kültür" -> AccentPurple.copy(alpha = 0.15f)
                "Hobi" -> AccentViolet.copy(alpha = 0.15f)
                "Dil" -> BranchTools.copy(alpha = 0.15f)
                else -> PanelNavyHighlight
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = habit.icon, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Başlık & Alt Not
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isCompleted) FontWeight.Medium else FontWeight.SemiBold,
                        color = if (isCompleted) TextMuted else TextPrimary,
                        fontSize = 13.sp
                    )
                )

                if (extraNote != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = extraNote,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StatusCompleted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

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

// ====================================================================
// ALT BİLEŞEN 6: HATIRLATICILAR KOMPAKT KAPSÜLÜ
// ====================================================================
@Composable
private fun RemindersCompactCard(
    dopamineEnabled: Boolean,
    waterEnabled: Boolean,
    onToggleDopamine: (Boolean) -> Unit,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔔", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WINTER ARC ALARMLARI",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 0.8.sp,
                            fontSize = 12.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = { onSendTestNotification() },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Test Bildirimi",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle 1: Akşam Kapanış
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PanelNavyHighlight)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 Akşam Detoks & Kapanış (21:30)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                )
                Switch(
                    checked = dopamineEnabled,
                    onCheckedChange = { onToggleDopamine(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = StatusCompleted,
                        checkedTrackColor = StatusCompleted.copy(alpha = 0.35f),
                        uncheckedThumbColor = TextDarkMuted,
                        uncheckedTrackColor = BorderSubtle
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Toggle 2: Su Hatırlatıcı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PanelNavyHighlight)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💧 Gün Ortası Hidrasyonu (15:00)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                )
                Switch(
                    checked = waterEnabled,
                    onCheckedChange = { onToggleWater(it) },
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
