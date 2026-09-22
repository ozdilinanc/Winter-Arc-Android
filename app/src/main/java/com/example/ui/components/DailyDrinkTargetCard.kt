package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.shadow
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
 * - Yumuşak dalga (fluid wave) arka planı
 * - "Drink 200 ml" hap butonu ve bardak ikonu
 * - Gösterge boncuklu (knob) dairesel ilerleme sayacı
 * - Karta tıklandığında detay sayfası açılır.
 */
@Composable
fun DailyDrinkTargetCard(
    currentMl: Int,
    targetMl: Int,
    onQuickAdd: (Int) -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentMl.toFloat() / targetMl.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900),
        label = "drinkTargetProgress"
    )

    val glassesCount = (currentMl / 200).coerceAtLeast(0)
    val isGoalMet = currentMl >= targetMl

    // Dalga ve kart renkleri: Temaya uyumlu yumuşak lila / indigo paleti
    val cardBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFEDE9FE).copy(alpha = 0.95f),
            Color(0xFFE0E7FF).copy(alpha = 0.90f)
        )
    )
    val waveColor1 = Color(0xFFC7D2FE).copy(alpha = 0.65f)
    val waveColor2 = Color(0xFFA5B4FC).copy(alpha = 0.45f)
    val trackRingColor = Color(0xFFC7D2FE).copy(alpha = 0.7f)
    val progressArcColor = Color(0xFF6366F1) // Canlı Indigo / Mor
    val knobColor = Color(0xFF4F46E5)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onCardClick() }
            .border(
                1.2.dp,
                if (isGoalMet) StatusCompleted.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.8f),
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
            // 1. ARKA PLAN: AKIŞKAN DALGA EĞRİLERİ (Fluid Waves)
            // ================================================================
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                val width = size.width
                val height = size.height

                // Arka dalga katmanı 1
                val path1 = Path().apply {
                    moveTo(0f, height * 0.42f)
                    cubicTo(
                        width * 0.25f, height * 0.35f,
                        width * 0.60f, height * 0.55f,
                        width, height * 0.45f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(path1, color = waveColor1)

                // Ön dalga katmanı 2
                val path2 = Path().apply {
                    moveTo(0f, height * 0.54f)
                    cubicTo(
                        width * 0.35f, height * 0.62f,
                        width * 0.70f, height * 0.46f,
                        width, height * 0.56f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(path2, color = waveColor2)
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
                            color = Color(0xFF1E1B4B),
                            fontSize = 19.sp,
                            letterSpacing = (-0.3).sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${currentMl}ml su ($glassesCount Bardak) • Hedef: ${targetMl}ml",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF4338CA),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hızlı Aksiyon Butonları (Pill Button + Bardak İkon Butonu)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // "Drink 200 ml" Hap Buton
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.dp, Color(0xFFE0E7FF)),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onQuickAdd(200) }
                        ) {
                            Text(
                                text = "Drink 200 ml",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 13.sp
                                )
                            )
                        }

                        // Bardak Butonu (Cam/Su İkonu)
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.dp, Color(0xFFE0E7FF)),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable { onCardClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_water_glass_action),
                                    contentDescription = "Detay & Su Ekle",
                                    tint = Color.Unspecified,
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

                        // 2. İlerleme arkı (Solid Violet / Indigo)
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

                            // Dış halka parlaması / gölgesi
                            drawCircle(
                                color = Color.White.copy(alpha = 0.5f),
                                radius = strokeWidthPx * 0.85f,
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
                                color = Color(0xFF1E1B4B),
                                fontSize = 16.sp
                            )
                        )

                        // İnce ayırıcı çizgi
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(1.dp)
                                .background(Color(0xFFCBD5E1))
                        )

                        Text(
                            text = "${targetMl}ml",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
