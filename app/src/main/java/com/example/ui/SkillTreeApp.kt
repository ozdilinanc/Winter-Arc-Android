package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hapticEngine = rememberHapticEngine()

    // Handle Back button for dialogs and sheets
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
        topBar = {
            MinimalDarkTopBar(
                currentMode = uiState.currentViewMode,
                userXp = uiState.userXp,
                onOpenAchievements = { viewModel.setAchievementsDialogVisible(true) }
            )
        },
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
                        onNavigateToCategories = { viewModel.onSetViewMode(SkillDashboardViewMode.CATEGORIES) }
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
                        onCycleFocus = viewModel::cycleNextDailyFocusSkill
                    )
                }

                SkillDashboardViewMode.PROGRESS_ANALYTICS -> {
                    ProgressAnalyticsView(
                        uiState = uiState,
                        onOpenAchievements = { viewModel.setAchievementsDialogVisible(true) }
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
    }
}

@Composable
private fun MinimalDarkTopBar(
    currentMode: SkillDashboardViewMode,
    userXp: com.example.data.model.UserXpProfile,
    onOpenAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasDark)
            .border(1.dp, BorderSubtle.copy(alpha = 0.5f))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = when (currentMode) {
                    SkillDashboardViewMode.TREE_MAP -> "AĞAÇ HARİTASI"
                    SkillDashboardViewMode.CATEGORIES -> "KATEGORİLER"
                    SkillDashboardViewMode.DAILY_TRACKER -> "GÜNLÜK TAKİP"
                    SkillDashboardViewMode.PROGRESS_ANALYTICS -> "İLERLEME"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.2.sp
                )
            )
            Text(
                text = when (currentMode) {
                    SkillDashboardViewMode.TREE_MAP -> "Yetenek Ağacı (Yakında Eklenecektir)"
                    SkillDashboardViewMode.CATEGORIES -> "5 Temel Gelişim Sütunu"
                    SkillDashboardViewMode.DAILY_TRACKER -> "Bugünün Rutinleri & Odak"
                    SkillDashboardViewMode.PROGRESS_ANALYTICS -> "Seviye, Rozetler & İstatistik"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.5.sp
                )
            )
        }

        // Quick Level/XP Pill
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = PanelNavyElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
            modifier = Modifier.clickable { onOpenAchievements() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LVL ${userXp.level}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "•",
                    color = TextDarkMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${userXp.totalXp} XP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentAmber,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ElegantDarkBottomNav(
    currentMode: SkillDashboardViewMode,
    onModeSelected: (SkillDashboardViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasDark)
            .border(1.dp, BorderSubtle.copy(alpha = 0.7f))
            .navigationBarsPadding()
            .height(58.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            Triple(SkillDashboardViewMode.TREE_MAP, "🌳", "AĞAÇ"),
            Triple(SkillDashboardViewMode.CATEGORIES, "🗂️", "KATEGORİ"),
            Triple(SkillDashboardViewMode.DAILY_TRACKER, "📅", "GÜNLÜK"),
            Triple(SkillDashboardViewMode.PROGRESS_ANALYTICS, "📊", "İLERLEME")
        )

        navItems.forEach { (mode, icon, label) ->
            val isSelected = currentMode == mode
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onModeSelected(mode) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 1.4.sp,
                        color = if (isSelected) AccentCyan else TextDarkMuted
                    )
                )
            }
        }
    }
}

@Composable
private fun SkillTreeComingSoonView(
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
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
                    Text(text = "🌳", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentAmber.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "🚧 YAKINDA EKLENECEKTİR",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    )
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
                    Text(
                        text = "🗂️ Kategorilere Git",
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

