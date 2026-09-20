package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SkillNode
import com.example.data.model.SkillStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import androidx.activity.compose.BackHandler
import com.example.ui.util.rememberHapticEngine

@Composable
fun SkillTreeApp(
    viewModel: SkillTreeViewModel,
    currentThemeId: AppThemeId = LocalAppPalette.current.id,
    onSelectTheme: (AppThemeId) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hapticEngine = rememberHapticEngine()
    var isThemePickerOpen by remember { mutableStateOf(false) }

    // Handle Back button for dialogs and sheets
    BackHandler(enabled = isThemePickerOpen) {
        isThemePickerOpen = false
    }
    BackHandler(enabled = uiState.activeRewardNotification != null) {
        viewModel.dismissRewardNotification()
    }
    BackHandler(enabled = uiState.isAchievementsDialogOpen) {
        viewModel.setAchievementsDialogVisible(false)
    }
    BackHandler(enabled = uiState.isAddProjectDialogOpen) {
        viewModel.setAddProjectDialogVisible(false)
    }
    BackHandler(enabled = uiState.selectedSkill != null) {
        viewModel.onSelectSkill(null)
    }
    // Return to root Categories tab if on another top-level tab
    BackHandler(enabled = uiState.currentViewMode != SkillDashboardViewMode.CATEGORIES) {
        viewModel.onSetViewMode(SkillDashboardViewMode.CATEGORIES)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("skilltree_main_scaffold"),
        containerColor = CanvasDark,
        bottomBar = {
            ElegantDarkBottomNav(
                currentMode = uiState.currentViewMode,
                onModeSelected = viewModel::onSetViewMode
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CanvasDark)
        ) {
            when (uiState.currentViewMode) {
                SkillDashboardViewMode.TREE_MAP -> {
                    SkillTreeComingSoonView(
                        onNavigateToCategories = { viewModel.onSetViewMode(SkillDashboardViewMode.CATEGORIES) },
                        onOpenThemePicker = { isThemePickerOpen = true }
                    )
                }

                SkillDashboardViewMode.CATEGORIES -> {
                    CategoriesDashboardView(
                        skills = uiState.allSkills,
                        projects = uiState.projects,
                        onAdvanceProjectStage = viewModel::advanceProjectStage,
                        onRegressProjectStage = viewModel::regressProjectStage,
                        onCreateProject = viewModel::createProject,
                        onDeleteProject = viewModel::deleteProject,
                        onOpenThemePicker = { isThemePickerOpen = true },
                        onCategorySelected = { /* Deep category details to be added later */ }
                    )
                }

                SkillDashboardViewMode.DAILY_TRACKER -> {
                    DailyTrackerView(
                        focusSkill = uiState.currentDailyFocusSkill,
                        onCompleteSkill = { skill ->
                            hapticEngine.vibrateSkillCompleted()
                            viewModel.onUpdateSkillStatus(skill.id, SkillStatus.COMPLETED)
                        },
                        onCycleFocus = viewModel::cycleNextDailyFocusSkill,
                        onOpenThemePicker = { isThemePickerOpen = true }
                    )
                }

                SkillDashboardViewMode.PROGRESS_ANALYTICS -> {
                    ProgressAnalyticsView(
                        uiState = uiState,
                        onOpenAchievements = { viewModel.setAchievementsDialogVisible(true) },
                        onOpenThemePicker = { isThemePickerOpen = true }
                    )
                }
            }
        }

        // Skill Detail Sheet
        uiState.selectedSkill?.let { selected ->
            SkillDetailSheet(
                skill = selected,
                onDismiss = { viewModel.onSelectSkill(null) },
                onStatusChange = { newStatus ->
                    if (newStatus == SkillStatus.COMPLETED || newStatus == SkillStatus.STRONG) {
                        hapticEngine.vibrateSkillCompleted()
                    }
                    viewModel.onUpdateSkillStatus(selected.id, newStatus)
                },
                onSaveNotes = { notes ->
                    viewModel.onSaveSkillNotes(selected.id, notes)
                },
                onAddResource = { resource ->
                    viewModel.onAddResource(selected.id, resource)
                },
                onAddProject = { projName ->
                    viewModel.onAddProjectToSkill(selected.id, projName)
                }
            )
        }

        // Add Project Dialog
        if (uiState.isAddProjectDialogOpen) {
            AddProjectDialog(
                onDismiss = { viewModel.setAddProjectDialogVisible(false) },
                onSave = { title, cat, desc, stage, gh, med, notes, tags ->
                    viewModel.createProject(title, cat, desc, stage, gh, med, notes, tags)
                }
            )
        }

        // Achievements & Badges Dialog
        if (uiState.isAchievementsDialogOpen) {
            BadgesAndAchievementsDialog(
                userXp = uiState.userXp,
                onDismiss = { viewModel.setAchievementsDialogVisible(false) }
            )
        }

        // Reward Celebration Dialog (Level-up & Badges)
        uiState.activeRewardNotification?.let { reward ->
            RewardCelebrationDialog(
                reward = reward,
                onDismiss = viewModel::dismissRewardNotification
            )
        }

        // Theme Picker Bottom Sheet
        if (isThemePickerOpen) {
            ThemePickerSheet(
                currentThemeId = currentThemeId,
                onSelectTheme = { themeId ->
                    hapticEngine.vibrateSelection()
                    onSelectTheme(themeId)
                },
                onDismiss = { isThemePickerOpen = false }
            )
        }
    }
}



private data class NavDestination(
    val mode: SkillDashboardViewMode,
    val icon: ImageVector,
    val label: String
)

@Composable
private fun ElegantDarkBottomNav(
    currentMode: SkillDashboardViewMode,
    onModeSelected: (SkillDashboardViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = PanelNavyElevated.copy(alpha = 0.95f),
            border = BorderStroke(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(
                        BorderSubtle.copy(alpha = 0.7f),
                        AccentCyan.copy(alpha = 0.35f),
                        BorderSubtle.copy(alpha = 0.7f)
                    )
                )
            ),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navItems = listOf(
                    NavDestination(SkillDashboardViewMode.TREE_MAP, Icons.Outlined.AccountTree, "AĞAÇ"),
                    NavDestination(SkillDashboardViewMode.CATEGORIES, Icons.Outlined.Dashboard, "KATEGORİ"),
                    NavDestination(SkillDashboardViewMode.DAILY_TRACKER, Icons.Outlined.CalendarToday, "GÜNLÜK"),
                    NavDestination(SkillDashboardViewMode.PROGRESS_ANALYTICS, Icons.Outlined.BarChart, "İLERLEME")
                )

                navItems.forEach { item ->
                    val isSelected = currentMode == item.mode

                    val animatedBgColor by animateColorAsState(
                        targetValue = if (isSelected) AccentCyan.copy(alpha = 0.14f) else Color.Transparent,
                        animationSpec = tween(durationMillis = 220),
                        label = "nav_bg_${item.label}"
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) AccentCyan.copy(alpha = 0.3f) else Color.Transparent,
                        animationSpec = tween(durationMillis = 220),
                        label = "nav_border_${item.label}"
                    )
                    val animatedContentColor by animateColorAsState(
                        targetValue = if (isSelected) AccentCyan else TextDarkMuted,
                        animationSpec = tween(durationMillis = 200),
                        label = "nav_tint_${item.label}"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.08f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "nav_scale_${item.label}"
                    )
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 14.dp else 0.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "nav_indicator_${item.label}"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 3.dp, horizontal = 2.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(animatedBgColor)
                            .border(1.dp, animatedBorderColor, RoundedCornerShape(18.dp))
                            .clickable {
                                if (!isSelected) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onModeSelected(item.mode)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = animatedContentColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .scale(iconScale)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    letterSpacing = 1.1.sp,
                                    color = animatedContentColor
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .height(2.5.dp)
                                    .width(indicatorWidth)
                                    .clip(RoundedCornerShape(1.5.dp))
                                    .background(if (isSelected) AccentCyan else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillTreeComingSoonView(
    onNavigateToCategories: () -> Unit,
    onOpenThemePicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ThemeToggleButton(
            onOpenThemePicker = onOpenThemePicker,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Glowing Icon Box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AccentCyan.copy(alpha = 0.12f))
                        .border(1.5.dp, AccentCyan.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountTree,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentAmber.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Construction,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "YAKINDA EKLENECEKTİR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Mühendislik Yetenek Ağacı",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "İnteraktif 2D düğüm grafiği ve yetenek dallanma görselleştirmesi daha akıcı bir deneyim için yeniden tasarlanıyor.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tüm yol haritalarına, .NET & Android yetkinliklerine, projelere ve BTK Akademi diploma takibine şu an Kategoriler sekmesinden eksiksiz ulaşabilirsin.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNavigateToCategories,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Dashboard,
                            contentDescription = null,
                            tint = CanvasDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Kategorilere Git",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CanvasDark,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

