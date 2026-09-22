package com.example.ui.components

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.util.rememberHapticEngine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class BeverageItem(
    val id: String,
    val name: String,
    val servingName: String,
    val defaultMl: Int,
    val iconResId: Int,
    val themeColor: Color
)

data class DayHydrationStat(
    val dayLabel: String,
    val dayShort: String,
    val dateKey: String,
    val consumedMl: Int,
    val targetMl: Int,
    val isToday: Boolean
) {
    val percentage: Float
        get() = if (targetMl > 0) (consumedMl.toFloat() / targetMl.toFloat() * 100f).coerceIn(0f, 100f) else 0f
}

/**
 * Modern Hidrasyon & İçecek Takip Detay Sayfası (ModalBottomSheet):
 * 1. 1. Görsel Stili: 3D/İzometrik Sütunlu "Hydration Stats" Haftalık Çubuk Grafiği
 * 2. Günlük Su Hedefi (Target) & Hızlı Yudum/Bardak/Şişe Ekleme
 * 3. 3. Görsel Stili: Düz dairesel (Flat Circle, 45° gölge) ikonlu Diğer İçecekler Listesi
 *
 * TÜM TEMALARLA (Forest Pine, Warm Espresso, Nordic Frost, Dracula, Light Paper, Cyber Neon vb.)
 * %100 UYUMLUDUR; tema değişiminde tüm sütunlar, kartlar, butonlar ve yazılar dinamik güncellenir.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationDetailSheet(
    currentWaterMl: Int,
    targetWaterMl: Int,
    onUpdateWater: (Int) -> Unit,
    onUpdateTarget: (Int) -> Unit,
    onResetWater: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val context = LocalContext.current
    val hapticEngine = rememberHapticEngine()
    val prefs = remember { context.getSharedPreferences("winter_arc_daily_tracker", Context.MODE_PRIVATE) }
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // İçecek Tanımları (3. görseldeki flat circle tarzındaki ikonlarla)
    val beverageDefinitions = remember(palette) {
        listOf(
            BeverageItem("soda", "Kola / Gazlı İçecek", "330 ml kutu", 330, R.drawable.ic_beverage_soda, Color(0xFFFFA000)),
            BeverageItem("coffee", "Kahve / Espresso", "200 ml kupa", 200, R.drawable.ic_beverage_coffee, Color(0xFF8D6E63)),
            BeverageItem("tea", "Çay / Bitki Çayı", "150 ml bardak", 150, R.drawable.ic_beverage_tea, Color(0xFF10B981)),
            BeverageItem("juice", "Taze Meyve Suyu", "250 ml bardak", 250, R.drawable.ic_beverage_juice, Color(0xFFFB923C)),
            BeverageItem("sparkling", "Maden Suyu / Soda", "200 ml şişe", 200, R.drawable.ic_beverage_water, Color(0xFF0284C7)),
            BeverageItem("energy", "Enerji İçeceği", "250 ml kutu", 250, R.drawable.ic_beverage_energy, palette.accentPurple)
        )
    }

    // İçecek miktarları state (SharedPreferences tabanlı)
    var beverageAmounts by remember(todayKey) {
        mutableStateOf(
            beverageDefinitions.associate { it.id to prefs.getInt("drink_${it.id}_$todayKey", 0) }
        )
    }

    fun updateBeverage(drinkId: String, deltaMl: Int) {
        hapticEngine.vibrateSelection()
        val current = beverageAmounts[drinkId] ?: 0
        val updated = (current + deltaMl).coerceAtLeast(0)
        beverageAmounts = beverageAmounts.toMutableMap().also { it[drinkId] = updated }
        prefs.edit().putInt("drink_${drinkId}_$todayKey", updated).apply()
    }

    // Haftalık Hidrasyon Verileri (1. Görseldeki 7 gün: Mon - Sun)
    val weeklyStats = remember(currentWaterMl, targetWaterMl) {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = mutableListOf<DayHydrationStat>()
        val defaultPercentages = listOf(58, 42, 70, 50, 52, 72, 75) // 1. Görseldeki örnek dağılım

        for (i in 0..6) {
            val date = cal.time
            val dKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            val dLabel = SimpleDateFormat("EEE", Locale.US).format(date)
            val dShort = when (dLabel.lowercase()) {
                "mon" -> "Mon"
                "tue" -> "Tue"
                "wed" -> "Web" // 1. görselde "Web" yazılmış
                "thu" -> "Thu"
                "fri" -> "Fri"
                "sat" -> "Sat"
                "sun" -> "Sun"
                else -> dLabel
            }
            val isToday = (dKey == todayKey)
            val consumed = if (isToday) {
                currentWaterMl
            } else {
                val stored = prefs.getInt("water_ml_$dKey", -1)
                if (stored != -1) stored else (targetWaterMl * defaultPercentages[i] / 100)
            }

            days.add(
                DayHydrationStat(
                    dayLabel = dLabel,
                    dayShort = dShort,
                    dateKey = dKey,
                    consumedMl = consumed,
                    targetMl = targetWaterMl,
                    isToday = isToday
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        days
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.panelNavy,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = palette.borderActive)
        },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 42.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ================================================================
            // 1. ÜST BAŞLIK (Header & Kapat Butonu)
            // ================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.accentCyan.copy(alpha = 0.15f))
                                .border(1.dp, palette.accentCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_tracker_water_drop),
                                contentDescription = null,
                                tint = palette.accentCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "HİDRASYON & SIVI MERKEZİ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = palette.textPrimary,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "Günlük su, haftalık istatistik ve içecekler",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = palette.textMuted,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(palette.panelNavyHighlight)
                            .border(1.dp, palette.borderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = palette.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ================================================================
            // 2. 1. RESİM TARZI: HAFTALIK HİDRASYON GRAFİĞİ (Hydration Stats)
            // ================================================================
            item {
                HydrationStatsWeeklyCard(weeklyStats = weeklyStats)
            }

            // ================================================================
            // 3. GÜNLÜK SU HEDEFİ AYARI & HIZLI EKLEME
            // ================================================================
            item {
                DailyWaterControlCard(
                    currentMl = currentWaterMl,
                    targetMl = targetWaterMl,
                    onAddWater = onUpdateWater,
                    onUpdateTarget = onUpdateTarget,
                    onReset = onResetWater
                )
            }

            // ================================================================
            // 4. 3. RESİM TARZI: DİĞER İÇECEKLER LİSTESİ (Flat Circular İkonlar)
            // ================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DİĞER İÇECEKLER & SIVILAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.accentCyan,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "İçilen miktarı kaydet",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = palette.textDarkMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            items(beverageDefinitions, key = { it.id }) { item ->
                val consumed = beverageAmounts[item.id] ?: 0
                BeverageRowItem(
                    item = item,
                    consumedMl = consumed,
                    onAdd = { updateBeverage(item.id, item.defaultMl) },
                    onSubtract = { updateBeverage(item.id, -item.defaultMl) }
                )
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN: 1. RESİM STİLİ 3D İZOMETRİK HAFTALIK GRAFİK
// ====================================================================
@Composable
private fun HydrationStatsWeeklyCard(
    weeklyStats: List<DayHydrationStat>,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, palette.borderSubtle, RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = palette.panelNavyElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Başlık & "This Week" Filtre Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hydration Stats",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        fontSize = 17.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.panelNavyHighlight,
                    border = BorderStroke(1.dp, palette.borderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = palette.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3D Çubuk Grafik Çizimi (Y-Ekseni: 100, 75, 50, 25, 0 ve 7 Sütun)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                // Y-Ekseni Etiketleri & Kılavuz Çizgileri
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("100", "75", "50", "25", "0").forEach { label ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textDarkMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.width(28.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Yatay Kılavuz Çizgisi
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(0.8.dp)
                                    .background(palette.borderSubtle.copy(alpha = 0.35f))
                            )
                        }
                    }
                }

                // 3D Sütunlar Katmanı (Canvas Çizimi)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 36.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyStats.forEach { dayStat ->
                        Isometric3DBarColumn(
                            percentage = dayStat.percentage,
                            dayShort = dayStat.dayShort,
                            isToday = dayStat.isToday,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Gün İsimleri (Mon, Tue, Web, Thu, Fri, Sat, Sun)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyStats.forEach { dayStat ->
                    Text(
                        text = dayStat.dayShort,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (dayStat.isToday) FontWeight.Black else FontWeight.SemiBold,
                            color = if (dayStat.isToday) palette.accentCyan else palette.textMuted,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * 1. Görseldeki 3D izometrik fasetli sütun barı.
 * - Arka plan boş kanal: temanın panel/border tonuyla uyumlu
 * - Dolu sütun ve fasetler: temanın accentCyan & accentIndigo renkleri ile 3D ışıklandırma
 */
@Composable
private fun Isometric3DBarColumn(
    percentage: Float,
    dayShort: String,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val animatedPercent by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 800),
        label = "barHeight_$dayShort"
    )

    // Temaya Dinamik Renkler:
    val trackColumnColor = if (palette.isLight) {
        palette.panelNavyHighlight.copy(alpha = 0.55f)
    } else {
        palette.panelNavyHighlight.copy(alpha = 0.75f)
    }
    val trackTopFacetColor = palette.borderSubtle.copy(alpha = 0.7f)

    val baseColor = if (isToday) palette.accentCyan else palette.accentIndigo
    val filledColumnColor = baseColor
    val facetTopColor = lerp(baseColor, Color.White, if (palette.isLight) 0.22f else 0.38f)
    val facetSideColor = lerp(baseColor, Color.Black, if (palette.isLight) 0.18f else 0.32f)

    Box(
        modifier = modifier.padding(horizontal = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width.coerceAtLeast(14.dp.toPx())
            val xLeft = (size.width - barWidth) / 2f
            val xRight = xLeft + barWidth
            val xMid = xLeft + barWidth / 2f

            val totalHeight = size.height - 14.dp.toPx()
            val facetHeight = 7.dp.toPx()

            // 1. Arka Plan Sütunu (Track Column)
            drawRect(
                color = trackColumnColor,
                topLeft = Offset(xLeft, 0f),
                size = Size(barWidth, totalHeight)
            )

            // Üst Arka Plan İzi Faseti
            val trackTopFacet = Path().apply {
                moveTo(xLeft, facetHeight / 2f)
                lineTo(xMid, 0f)
                lineTo(xRight, facetHeight / 2f)
                lineTo(xMid, facetHeight)
                close()
            }
            drawPath(trackTopFacet, color = trackTopFacetColor)

            // 2. Dolu Sütun (Filled Progress Column)
            val fillHeight = (totalHeight * (animatedPercent / 100f)).coerceIn(0f, totalHeight)
            val yTop = totalHeight - fillHeight

            if (fillHeight > 0f) {
                // Ana gövde
                drawRect(
                    color = filledColumnColor,
                    topLeft = Offset(xLeft, yTop),
                    size = Size(barWidth, fillHeight)
                )

                // Sağ Taraf Gölgelendirmesi (3D Derinlik)
                drawRect(
                    color = facetSideColor.copy(alpha = 0.45f),
                    topLeft = Offset(xMid, yTop),
                    size = Size(barWidth / 2f, fillHeight)
                )

                // 3D Üst Faset (Elmas / Isometric Diamond Face)
                val topFacet = Path().apply {
                    moveTo(xLeft, yTop)
                    lineTo(xMid, yTop - facetHeight / 2f)
                    lineTo(xRight, yTop)
                    lineTo(xMid, yTop + facetHeight / 2f)
                    close()
                }
                drawPath(topFacet, color = facetTopColor)
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN: GÜNLÜK SU KONTROLÜ VE HEDEF DÜZENLEME
// ====================================================================
@Composable
private fun DailyWaterControlCard(
    currentMl: Int,
    targetMl: Int,
    onAddWater: (Int) -> Unit,
    onUpdateTarget: (Int) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val hapticEngine = rememberHapticEngine()
    val progress = (currentMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)
    val percentageInt = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.borderSubtle, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = palette.panelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Hedef Seçimi Satırı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GÜNLÜK HEDEF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.accentCyan,
                            fontSize = 10.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                    Text(
                        text = "$targetMl ml / gün",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary,
                            fontSize = 16.sp
                        )
                    )
                }

                // Hedef Ayar Butonları (2000, 2500, 3000, 3500)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2000, 2500, 3000, 3500).forEach { goal ->
                        val isSelected = (goal == targetMl)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) palette.accentCyan else palette.panelNavyHighlight,
                            border = BorderStroke(1.dp, if (isSelected) palette.accentCyan else palette.borderSubtle),
                            modifier = Modifier.clickable {
                                hapticEngine.vibrateSelection()
                                onUpdateTarget(goal)
                            }
                        ) {
                            Text(
                                text = if (goal % 1000 == 0) "${goal / 1000}L" else "${goal / 1000f}L",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        if (palette.isLight) Color.White else palette.canvasDark
                                    } else {
                                        palette.textMuted
                                    },
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // İlerleme Çubuğu ve % Oran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bugün içilen: $currentMl ml",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "%$percentageInt Tamamlandı",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (currentMl >= targetMl) StatusCompleted else palette.accentCyan,
                        fontSize = 11.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (currentMl >= targetMl) StatusCompleted else palette.accentCyan,
                trackColor = palette.panelNavyHighlight
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hızlı Su Ekleme Butonları (+100, +200, +250, +500 ml)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    100 to "Yudum",
                    200 to "Bardak",
                    250 to "Kupa",
                    500 to "Şişe"
                ).forEach { (ml, label) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.panelNavyHighlight,
                        border = BorderStroke(1.dp, palette.accentCyan.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onAddWater(ml) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "+$ml ml",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = palette.accentCyan,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textDarkMuted,
                                    fontSize = 9.5.sp
                                )
                            )
                        }
                    }
                }

                // Sıfırlama Butonu
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.panelNavyHighlight,
                    border = BorderStroke(1.dp, palette.borderSubtle),
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onReset() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sıfırla",
                            tint = palette.textDarkMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ====================================================================
// ALT BİLEŞEN: DİĞER İÇECEK SATIRI (3. Resim Tarzı Flat İkon)
// ====================================================================
@Composable
private fun BeverageRowItem(
    item: BeverageItem,
    consumedMl: Int,
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.panelNavyElevated,
        border = BorderStroke(1.dp, if (consumedMl > 0) item.themeColor.copy(alpha = 0.5f) else palette.borderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sol Taraf: 3. Resim Tarzı Düz Dairesel Vektörel İkon + Başlık
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // 3. Görsel Stili Flat Circular İkon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.name,
                        tint = Color.Unspecified, // XML içindeki özgün renkleri korur
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            fontSize = 13.5.sp
                        )
                    )
                    Text(
                        text = "${item.servingName} • Toplam: $consumedMl ml",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (consumedMl > 0) item.themeColor else palette.textMuted,
                            fontWeight = if (consumedMl > 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Sağ Taraf: Eksi & Artı Sayaç Butonları
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Azalt butonu
                IconButton(
                    onClick = onSubtract,
                    enabled = consumedMl > 0,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (consumedMl > 0) palette.panelNavyHighlight else Color.Transparent)
                        .border(1.dp, palette.borderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Azalt",
                        tint = if (consumedMl > 0) palette.textPrimary else palette.textDarkMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Hızlı Ekle (+ Porsiyon)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = item.themeColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, item.themeColor.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onAdd() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ekle",
                            tint = item.themeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "+${item.defaultMl}ml",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = item.themeColor,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
