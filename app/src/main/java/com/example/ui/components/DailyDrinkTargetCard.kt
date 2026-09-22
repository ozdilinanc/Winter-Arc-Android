package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 2. Referans Görsel tarzında modern Günlük Su Hedefi Kartı (Daily Drink Target).
 * - İçilen günlük suya (currentMl / targetMl) göre dinamik olarak yükselen/alçalan
 *   gerçek zamanlı akışkan su seviyesi (Fluid Water Level)
 * - Sürekli ve huzur verici şekilde dalgalanan organik su yüzeyi (Infinite Wave Transition)
 * - Su içinde süzülen mikro baloncuklar ve ışıldayan yüzey parıltısı
 * - Tüm temalara %100 uyumlu renk paleti ve dairesel gösterge
 * - Karta tıklandığında detay sayfası (HydrationDetailSheet) açılır.
 */
@Composable
fun DailyDrinkTargetCard(
    currentMl: Int,
    targetMl: Int,
    onQuickAdd: (Int) -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val rawProgress = (currentMl.toFloat() / targetMl.coerceAtLeast(1).toFloat()).coerceAtLeast(0f)
    val progress = rawProgress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "drinkTargetProgress"
    )

    // Dinamik su seviyesi (0f = boş dip, 1f = hedefe ulaşıldı, 1.25f = hedef aşıldı)
    val targetFillFactor = rawProgress.coerceIn(0f, 1.25f)
    val animatedWaterLevel by animateFloatAsState(
        targetValue = targetFillFactor,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "waterFillLevel"
    )

    // Canlı, organik su dalgalanması (Infinite animation)
    val infiniteTransition = rememberInfiniteTransition(label = "waterWaveTransition")
    val wavePhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase1"
    )
    val wavePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase2"
    )
    val bubblePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubblePhase"
    )

    val glassesCount = (currentMl / 200).coerceAtLeast(0)
    val isGoalMet = currentMl >= targetMl

    // ================================================================
    // TEMAYA DİNAMİK UYUMLU RENK PALETİ
    // ================================================================
    val cardBackground = if (palette.isLight) {
        Brush.verticalGradient(
            listOf(
                palette.panelNavyElevated,
                palette.panelNavyHighlight.copy(alpha = 0.9f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                palette.panelNavyElevated,
                palette.panelNavyHighlight.copy(alpha = 0.65f)
            )
        )
    }

    // Akışkan dalga renkleri: Temanın birincil ve ikincil accent renklerinin yarı saydam katmanları
    val waveColor1 = palette.accentCyan.copy(alpha = if (palette.isLight) 0.18f else 0.15f)
    val waveColor2 = palette.accentIndigo.copy(alpha = if (palette.isLight) 0.14f else 0.11f)

    val trackRingColor = palette.accentCyan.copy(alpha = 0.16f)
    val progressArcColor = if (isGoalMet) StatusCompleted else palette.accentCyan
    val knobColor = if (isGoalMet) StatusCompleted else palette.accentCyan
    val knobGlowColor = (if (isGoalMet) StatusCompleted else palette.accentCyan).copy(alpha = 0.35f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onCardClick() }
            .border(
                1.dp,
                if (isGoalMet) StatusCompleted.copy(alpha = 0.5f) else palette.borderSubtle,
                RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackground)
        ) {
            // ================================================================
            // 1. ARKA PLAN: DİNAMİK SU SEVİYESİ & AKIŞKAN DALGA ANİMASYONU
            // ================================================================
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                val width = size.width
                val height = size.height

                if (animatedWaterLevel > 0.005f) {
                    // Maksimum doluluk seviyesi (başlık metninin arkasında ferah nefes alanı)
                    val maxFillHeight = height * 0.84f
                    val currentFillHeight = animatedWaterLevel.coerceIn(0f, 1.15f) * maxFillHeight
                    val baseWaterY = height - currentFillHeight

                    // Dalga genlikleri (Su miktarı azken sakin, doldukça canlı dalgalar)
                    val amplitude1 = (7.dp.toPx() * animatedWaterLevel.coerceIn(0.2f, 1f))
                    val amplitude2 = (5.5.dp.toPx() * animatedWaterLevel.coerceIn(0.2f, 1f))

                    // 1. Arka dalga katmanı (Daha yavaş, hafif koyu/indigo ton, zengin derinlik)
                    val pathBack = Path().apply {
                        moveTo(0f, height)
                        var x = 0f
                        val step = 16f
                        while (x <= width + step) {
                            val waveY = (baseWaterY - 4.dp.toPx()) + amplitude1 * sin(
                                (x / width * 2.4f * Math.PI.toFloat() + wavePhase1)
                            )
                            if (x == 0f) lineTo(0f, waveY) else lineTo(x, waveY)
                            x += step
                        }
                        lineTo(width, height)
                        close()
                    }
                    drawPath(
                        path = pathBack,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                waveColor2.copy(alpha = waveColor2.alpha * 1.4f),
                                waveColor2.copy(alpha = waveColor2.alpha * 0.35f)
                            ),
                            startY = (baseWaterY - 10.dp.toPx()).coerceAtLeast(0f),
                            endY = height
                        )
                    )

                    // 2. Ön dalga katmanı (Daha hızlı, parlak cyan ton)
                    val frontWavePoints = mutableListOf<Offset>()
                    val pathFront = Path().apply {
                        moveTo(0f, height)
                        var x = 0f
                        val step = 16f
                        while (x <= width + step) {
                            val waveY = baseWaterY + amplitude2 * sin(
                                (x / width * 2.8f * Math.PI.toFloat() + wavePhase2 + 1.2f)
                            )
                            val pt = Offset(x.coerceAtMost(width), waveY)
                            frontWavePoints.add(pt)
                            if (x == 0f) lineTo(0f, waveY) else lineTo(pt.x, pt.y)
                            x += step
                        }
                        lineTo(width, height)
                        close()
                    }
                    drawPath(
                        path = pathFront,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                waveColor1.copy(alpha = if (isGoalMet) 0.38f else 0.28f),
                                waveColor1.copy(alpha = if (isGoalMet) 0.16f else 0.08f)
                            ),
                            startY = (baseWaterY - 10.dp.toPx()).coerceAtLeast(0f),
                            endY = height
                        )
                    )

                    // 3. Su yüzeyi parıltı çizgisi (Surface Crest Highlight)
                    val crestPath = Path().apply {
                        if (frontWavePoints.isNotEmpty()) {
                            moveTo(frontWavePoints.first().x, frontWavePoints.first().y)
                            for (i in 1 until frontWavePoints.size) {
                                lineTo(frontWavePoints[i].x, frontWavePoints[i].y)
                            }
                        }
                    }
                    drawPath(
                        path = crestPath,
                        color = (if (isGoalMet) StatusCompleted else palette.accentCyan).copy(
                            alpha = if (palette.isLight) 0.32f else 0.45f
                        ),
                        style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 4. Mikro su baloncukları (Canlı yükselen baloncuklar)
                    val bubbleXs = listOf(0.16f, 0.34f, 0.54f, 0.74f)
                    bubbleXs.forEachIndexed { index, relX ->
                        val bubbleProgress = (bubblePhase + index * 0.25f) % 1f
                        val bY = height - (bubbleProgress * currentFillHeight * 0.90f)
                        val bX = width * relX + sin((bubbleProgress * 2f * Math.PI + index).toFloat()) * 6.dp.toPx()
                        val bAlpha = ((1f - bubbleProgress) * 0.35f * animatedWaterLevel.coerceIn(0f, 1f)).coerceIn(0f, 0.35f)
                        val bRadius = (2.dp.toPx() + (index % 2) * 1.dp.toPx())

                        if (bY > baseWaterY && bY < height) {
                            drawCircle(
                                color = Color.White.copy(alpha = bAlpha),
                                radius = bRadius,
                                center = Offset(bX, bY)
                            )
                        }
                    }
                }
            }

            // ================================================================
            // 2. KART İÇERİĞİ
            // ================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SOL KOLON: Başlıklar & Hızlı Butonlar
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = "Daily Drink Target",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary,
                            fontSize = 18.sp,
                            letterSpacing = (-0.2).sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${currentMl}ml su ($glassesCount Bardak) • Hedef: ${targetMl}ml",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = palette.accentCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Hızlı Aksiyon Butonları (Pill Button + Bardak İkon Butonu)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // "Drink 200 ml" Kapsül (Pill) Buton
                        Surface(
                            shape = CircleShape,
                            color = palette.panelNavyHighlight,
                            border = BorderStroke(1.dp, palette.accentCyan.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onQuickAdd(200) }
                        ) {
                            Text(
                                text = "Drink 200 ml",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        // Bardak Butonu (Cam/Su İkonu)
                        Surface(
                            shape = CircleShape,
                            color = palette.panelNavyHighlight,
                            border = BorderStroke(1.dp, palette.borderSubtle),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable { onCardClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_water_glass_action),
                                    contentDescription = "Detay & Su Ekle",
                                    tint = palette.accentCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // SAĞ KOLON: Gösterge Boncuklu (Knob) Dairesel İlerleme Sayacı
                Box(
                    modifier = Modifier.size(108.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        val strokeWidthPx = 9.dp.toPx()
                        val diameter = minOf(size.width, size.height) - strokeWidthPx
                        val radius = diameter / 2f
                        val centerOffset = Offset(size.width / 2f, size.height / 2f)
                        val arcTopLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                        val arcSize = Size(diameter, diameter)

                        // 1. Arka plan halkası (Track)
                        drawArc(
                            color = trackRingColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidthPx)
                        )

                        // 2. İlerleme arkı (Temanın Canlı Accent Rengi)
                        val startAngle = -90f
                        val sweepAngle = animatedProgress * 360f

                        if (animatedProgress > 0f) {
                            drawArc(
                                color = progressArcColor,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = arcTopLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                            )

                            // 3. Gösterge Boncuğu (Knob / Indicator Bead)
                            val endAngleRad = Math.toRadians((startAngle + sweepAngle).toDouble())
                            val knobCenter = Offset(
                                x = (centerOffset.x + radius * cos(endAngleRad)).toFloat(),
                                y = (centerOffset.y + radius * sin(endAngleRad)).toFloat()
                            )

                            // Dış halka parlaması / halesi
                            drawCircle(
                                color = knobGlowColor,
                                radius = strokeWidthPx * 0.9f,
                                center = knobCenter
                            )
                            // Ana boncuk çekirdeği
                            drawCircle(
                                color = knobColor,
                                radius = strokeWidthPx * 0.65f,
                                center = knobCenter
                            )
                            // İç beyaz parlama noktası
                            drawCircle(
                                color = Color.White,
                                radius = strokeWidthPx * 0.25f,
                                center = knobCenter
                            )
                        }
                    }

                    // Halka İçi Metin: 2210ml / 3000ml
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${currentMl}ml",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = palette.textPrimary,
                                fontSize = 16.sp
                            )
                        )

                        // İnce ayırıcı çizgi
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(1.dp)
                                .background(palette.borderSubtle)
                        )

                        Text(
                            text = "${targetMl}ml",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
