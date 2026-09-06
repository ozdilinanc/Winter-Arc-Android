package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import com.example.ui.util.rememberHapticEngine
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
        // 2. Dopamin Detoksu Widget (Bozdum / Bozmadım)
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

                    // Open Huawei Health App button
                    OutlinedButton(
                        onClick = {
                            try {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.huawei.health")
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                } else {
                                    Toast.makeText(context, "Huawei Sağlık uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Uygulama açılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            text = "Huawei Sağlık Uygulamasını Aç ⌚",
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
        // 5. Section: Bedensel Güç & Spor Protokolü
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

            DailyHabitCard(
                habit = habit,
                isCompleted = isDone,
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
