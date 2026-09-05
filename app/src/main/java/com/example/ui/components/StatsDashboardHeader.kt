package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BranchId
import com.example.data.model.SkillStatus
import com.example.ui.SkillDashboardViewMode
import com.example.ui.SkillTreeUiState
import com.example.ui.theme.*

@Composable
fun StatsDashboardHeader(
    uiState: SkillTreeUiState,
    onSearchChange: (String) -> Unit,
    onBranchFilter: (BranchId?) -> Unit,
    onStatusFilter: (SkillStatus?) -> Unit,
    onViewModeChange: (SkillDashboardViewMode) -> Unit,
    onOpenAchievements: () -> Unit = {},
    onRestoreDailyBanner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isSearchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Bar: Elegant Dark Header with Avatar & Completion
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ENGINEERING ROADMAP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenAchievements() }
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AccentCyan, AccentPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "KS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Kerem S. • 🎓 MEZUNİYET",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lvl ${uiState.userXp.level} • ${uiState.userXp.rankTitle}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentIndigo,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PanelNavyHighlight)
                                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⭐ ${uiState.userXp.totalXp} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "🏆 ${uiState.userXp.unlockedBadges.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TOTAL COMPLETION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(76.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(BorderSubtle)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (uiState.masteryPercent / 100f).coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(AccentCyan)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${uiState.masteryPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentIndigo,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (uiState.isDailyBannerDismissed) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onRestoreDailyBanner,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PanelNavyElevated)
                            .border(1.dp, AccentAmber.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .testTag("restore_daily_banner_button")
                    ) {
                        Text(text = "⚡", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Search toggle button
                IconButton(
                    onClick = { isSearchExpanded = !isSearchExpanded },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSearchExpanded || uiState.searchQuery.isNotEmpty()) PanelNavyHighlight else PanelNavyElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        .testTag("toggle_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (uiState.searchQuery.isNotEmpty()) AccentIndigo else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Expandable Search Bar
        AnimatedVisibility(visible = isSearchExpanded || uiState.searchQuery.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    placeholder = {
                        Text(
                            "Search skills, e.g. LINQ, KV Cache, Docker, Room, TCP...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = AccentCyan
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PanelNavyElevated,
                        unfocusedContainerColor = PanelNavy,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress & Mastery Distribution Bar (Elegant Dark obsidian card)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PanelNavy)
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ROADMAP PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.2.sp,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${uiState.masteryPercent}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentIndigo,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ".NET Priority: ",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                        Text(
                            text = "${uiState.backendMasteryPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BranchDotNet,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = " (${uiState.completedCount + uiState.strongCount}/${uiState.totalCount})",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Segmented Elegant progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BorderSubtle)
                ) {
                    val total = if (uiState.totalCount > 0) uiState.totalCount.toFloat() else 1f
                    if (uiState.strongCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(uiState.strongCount / total)
                                .fillMaxHeight()
                                .background(StatusStrong)
                        )
                    }
                    if (uiState.completedCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(uiState.completedCount / total)
                                .fillMaxHeight()
                                .background(StatusCompleted)
                        )
                    }
                    if (uiState.practicedCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(uiState.practicedCount / total)
                                .fillMaxHeight()
                                .background(StatusPracticed)
                        )
                    }
                    if (uiState.learningCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(uiState.learningCount / total)
                                .fillMaxHeight()
                                .background(StatusLearning)
                        )
                    }
                    if (uiState.notStartedCount > 0) {
                        Box(
                            modifier = Modifier
                                .weight(uiState.notStartedCount / total)
                                .fillMaxHeight()
                                .background(StatusNotStarted.copy(alpha = 0.4f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusLegendPill(
                        label = "All (${uiState.totalCount})",
                        color = TextSecondary,
                        isSelected = uiState.selectedStatusFilter == null,
                        onClick = { onStatusFilter(null) }
                    )
                    StatusLegendPill(
                        label = "In Progress (${uiState.inProgressCount})",
                        color = StatusLearning,
                        isSelected = uiState.selectedStatusFilter == SkillStatus.IN_PROGRESS,
                        onClick = { onStatusFilter(SkillStatus.IN_PROGRESS) }
                    )
                    StatusLegendPill(
                        label = "Completed (${uiState.completedCount})",
                        color = StatusCompleted,
                        isSelected = uiState.selectedStatusFilter == SkillStatus.COMPLETED,
                        onClick = { onStatusFilter(SkillStatus.COMPLETED) }
                    )
                    StatusLegendPill(
                        label = "Mastered (${uiState.strongCount})",
                        color = StatusStrong,
                        isSelected = uiState.selectedStatusFilter == SkillStatus.STRONG,
                        onClick = { onStatusFilter(SkillStatus.STRONG) }
                    )
                    StatusLegendPill(
                        label = "Not Started (${uiState.notStartedCount})",
                        color = StatusNotStarted,
                        isSelected = uiState.selectedStatusFilter == SkillStatus.NOT_STARTED,
                        onClick = { onStatusFilter(SkillStatus.NOT_STARTED) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // View Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PanelNavy)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SkillDashboardViewMode.values().forEach { mode ->
                val isSelected = uiState.currentViewMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PanelNavyHighlight else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) AccentCyan.copy(alpha = 0.4f) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onViewModeChange(mode) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AccentIndigo else TextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        // Branch Filter Carousel
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BranchFilterChip(
                title = "All Branches",
                icon = "🌐",
                isSelected = uiState.selectedBranchFilter == null,
                accentColor = AccentCyan,
                onClick = { onBranchFilter(null) }
            )
            BranchId.values().forEach { branch ->
                BranchFilterChip(
                    title = branch.shortName,
                    icon = branch.iconEmoji,
                    isSelected = uiState.selectedBranchFilter == branch,
                    accentColor = branch.accentColor,
                    isPriority = branch.isCorePriority,
                    onClick = { onBranchFilter(branch) }
                )
            }
        }
    }
}

@Composable
private fun StatusLegendPill(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color.copy(alpha = 0.2f) else PanelNavyElevated)
            .border(
                1.dp,
                if (isSelected) color else BorderSubtle,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@Composable
private fun BranchFilterChip(
    title: String,
    icon: String,
    isSelected: Boolean,
    accentColor: Color,
    isPriority: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.18f) else PanelNavy)
            .border(
                1.dp,
                if (isSelected) accentColor else if (isPriority) BranchDotNet.copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) accentColor else TextSecondary,
                    fontWeight = if (isSelected || isPriority) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            )
            if (isPriority) {
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(BranchDotNet)
                )
            }
        }
    }
}
