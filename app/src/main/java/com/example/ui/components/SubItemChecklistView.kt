package com.example.ui.components

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
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
import com.example.data.model.RoadmapDataStore
import com.example.data.model.SubItemRoadmap
import com.example.data.model.TopicCheckItem
import com.example.data.model.TopicProgressState
import com.example.data.model.TopicSection
import com.example.ui.theme.*
import com.example.ui.util.rememberHapticEngine

@Composable
fun SubItemChecklistView(
    subItemId: String,
    accentColor: Color,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roadmap: SubItemRoadmap = RoadmapDataStore.allRoadmaps[subItemId] ?: return

    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("winter_arc_roadmap_progress", Context.MODE_PRIVATE) }
    val hapticEngine = rememberHapticEngine()

    // Load persistent progress states for topics
    var itemStates by remember(subItemId) {
        mutableStateOf(
            roadmap.sections.flatMap { it.items }.associate { item ->
                val savedKey = prefs.getString("status_${item.id}", null)
                item.id to TopicProgressState.fromKey(savedKey)
            }
        )
    }

    fun updateItemState(itemId: String, newState: TopicProgressState) {
        itemStates = itemStates + (itemId to newState)
        prefs.edit().putString("status_$itemId", newState.key).apply()
        if (newState == TopicProgressState.COMPLETED) {
            hapticEngine.vibrateLevelUp()
        } else {
            hapticEngine.vibrateSkillCompleted()
        }
    }

    val totalCount = roadmap.sections.sumOf { it.items.size }
    val notStartedCount = itemStates.values.count { it == TopicProgressState.NOT_STARTED }
    val theoryCount = itemStates.values.count { it == TopicProgressState.THEORY }
    val practiceCount = itemStates.values.count { it == TopicProgressState.PRACTICED }
    val completedCount = itemStates.values.count { it == TopicProgressState.COMPLETED }

    val earnedPoints = theoryCount * 0.35f + practiceCount * 0.70f + completedCount * 1.0f
    val progressFraction = if (totalCount > 0) (earnedPoints / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
    val progressPercent = (progressFraction * 100).toInt().coerceIn(0, 100)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back Navigation Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = AccentCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LİSTEYE DÖN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }

        // Sub-item Header Banner with Multi-State Progress
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = roadmap.emoji, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = roadmap.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = roadmap.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = accentColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PanelNavy,
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = roadmap.targetLevel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = roadmap.overview,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Metric Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KADEMELİ GELİŞİM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                fontSize = 10.5.sp,
                                letterSpacing = 1.sp
                            )
                        )

                        Text(
                            text = "%$progressPercent İlerleme",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (progressPercent > 0) accentColor else TextMuted,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = accentColor,
                        trackColor = PanelNavy
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-state Breakdown Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusSummaryChip(
                            emoji = "✅",
                            label = "$completedCount Tamam",
                            color = AccentEmerald
                        )
                        StatusSummaryChip(
                            emoji = "🛠️",
                            label = "$practiceCount Pratik",
                            color = AccentAmber
                        )
                        StatusSummaryChip(
                            emoji = "📘",
                            label = "$theoryCount Teori",
                            color = Color(0xFF38BDF8)
                        )
                        StatusSummaryChip(
                            emoji = "⚪",
                            label = "$notStartedCount Bekleyen",
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Section Cards with Deep Topic Cards
        items(roadmap.sections) { section ->
            TopicSectionCard(
                section = section,
                accentColor = accentColor,
                itemStates = itemStates,
                onUpdateState = { itemId, newState ->
                    updateItemState(itemId, newState)
                }
            )
        }
    }
}

@Composable
private fun StatusSummaryChip(
    emoji: String,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 10.5.sp
                )
            )
        }
    }
}

@Composable
private fun TopicSectionCard(
    section: TopicSection,
    accentColor: Color,
    itemStates: Map<String, TopicProgressState>,
    onUpdateState: (String, TopicProgressState) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val sectionCompletedCount = section.items.count { itemStates[it.id] == TopicProgressState.COMPLETED }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = section.emoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$sectionCompletedCount/${section.items.size}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (sectionCompletedCount == section.items.size && section.items.isNotEmpty()) AccentEmerald else TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    section.items.forEach { item ->
                        val currentState = itemStates[item.id] ?: TopicProgressState.NOT_STARTED
                        TopicItemCard(
                            item = item,
                            state = currentState,
                            accentColor = accentColor,
                            onUpdateState = { newState -> onUpdateState(item.id, newState) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicItemCard(
    item: TopicCheckItem,
    state: TopicProgressState,
    accentColor: Color,
    onUpdateState: (TopicProgressState) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val stateColor = when (state) {
        TopicProgressState.NOT_STARTED -> TextMuted
        TopicProgressState.THEORY -> Color(0xFF38BDF8) // Sky Blue
        TopicProgressState.PRACTICED -> AccentAmber      // Amber / Orange
        TopicProgressState.COMPLETED -> AccentEmerald    // Emerald
    }

    val stateBg = when (state) {
        TopicProgressState.NOT_STARTED -> PanelNavy
        TopicProgressState.THEORY -> Color(0xFF38BDF8).copy(alpha = 0.05f)
        TopicProgressState.PRACTICED -> AccentAmber.copy(alpha = 0.06f)
        TopicProgressState.COMPLETED -> AccentEmerald.copy(alpha = 0.08f)
    }

    val stateBorder = when (state) {
        TopicProgressState.NOT_STARTED -> BorderSubtle.copy(alpha = 0.5f)
        TopicProgressState.THEORY -> Color(0xFF38BDF8).copy(alpha = 0.3f)
        TopicProgressState.PRACTICED -> AccentAmber.copy(alpha = 0.35f)
        TopicProgressState.COMPLETED -> AccentEmerald.copy(alpha = 0.4f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(stateBg)
            .border(1.dp, stateBorder, RoundedCornerShape(10.dp))
            .animateContentSize()
    ) {
        // Main Row (Click anywhere except status chip to toggle practice details)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (state == TopicProgressState.NOT_STARTED) Color.Transparent
                        else stateColor.copy(alpha = 0.2f)
                    )
                    .border(
                        1.5.dp,
                        if (state == TopicProgressState.NOT_STARTED) TextMuted else stateColor,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    TopicProgressState.NOT_STARTED -> {}
                    TopicProgressState.THEORY -> {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = stateColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    TopicProgressState.PRACTICED -> {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = stateColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    TopicProgressState.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = stateColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Topic Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (state != TopicProgressState.NOT_STARTED) TextPrimary else TextSecondary,
                        fontSize = 12.5.sp
                    )
                )
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Interactive Status Chip: Click to advance to next state
            Surface(
                shape = RoundedCornerShape(7.dp),
                color = if (state == TopicProgressState.NOT_STARTED) PanelNavyElevated else stateColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, if (state == TopicProgressState.NOT_STARTED) BorderSubtle else stateColor.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { onUpdateState(state.next()) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${state.emoji} ${state.shortLabel}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = stateColor,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        // Expanded Practice Challenge & State Control Section
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            ) {
                HorizontalDivider(
                    color = BorderSubtle.copy(alpha = 0.6f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Direct State Selector Bar (Segmented Pills)
                Text(
                    text = "DURUM SEÇİN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TopicProgressState.entries.forEach { s ->
                        val isSelected = s == state
                        val pillColor = when (s) {
                            TopicProgressState.NOT_STARTED -> TextMuted
                            TopicProgressState.THEORY -> Color(0xFF38BDF8)
                            TopicProgressState.PRACTICED -> AccentAmber
                            TopicProgressState.COMPLETED -> AccentEmerald
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) pillColor.copy(alpha = 0.2f) else PanelNavy,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) pillColor else BorderSubtle.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.clickable { onUpdateState(s) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = s.emoji, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = s.shortLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) pillColor else TextSecondary,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. 🧪 Pratik Görevi (Hands-on Practice Box)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PanelNavy,
                    border = BorderStroke(
                        1.dp,
                        if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED)
                            AccentEmerald.copy(alpha = 0.35f)
                        else
                            AccentAmber.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🧪 PRATİK GÖREVİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED) AccentEmerald else AccentAmber,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED)
                                    AccentEmerald.copy(alpha = 0.15f)
                                else
                                    AccentAmber.copy(alpha = 0.15f),
                                border = BorderStroke(
                                    1.dp,
                                    if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED)
                                        AccentEmerald.copy(alpha = 0.4f)
                                    else
                                        AccentAmber.copy(alpha = 0.4f)
                                )
                            ) {
                                Text(
                                    text = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED) "✓ Uygulandı" else "⏳ Bekliyor",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED) AccentEmerald else AccentAmber
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.effectivePracticeTask(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Workflow Action Button
                        when (state) {
                            TopicProgressState.NOT_STARTED, TopicProgressState.THEORY -> {
                                OutlinedButton(
                                    onClick = { onUpdateState(TopicProgressState.PRACTICED) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.7f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = AccentAmber.copy(alpha = 0.12f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = AccentAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pratiği Tamamladım (Pratik Durumuna Geçir)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AccentAmber,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                            TopicProgressState.PRACTICED -> {
                                OutlinedButton(
                                    onClick = { onUpdateState(TopicProgressState.COMPLETED) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.7f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = AccentEmerald.copy(alpha = 0.12f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Konuyu Tamamla (Tamamlandı Olarak İşaretle)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AccentEmerald,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                            TopicProgressState.COMPLETED -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tebrikler! Hem teori hem de pratik görevi başarıyla tamamlandı.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = AccentEmerald,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
