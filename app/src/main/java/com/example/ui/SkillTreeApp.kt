package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.ui.util.rememberHapticEngine

@Composable
fun SkillTreeApp(
    viewModel: SkillTreeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hapticEngine = rememberHapticEngine()

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
                    SkillTreeGraphView(
                        skills = uiState.allSkills,
                        selectedSkill = uiState.selectedSkill,
                        onSkillClick = { skill -> viewModel.onSelectSkill(skill) }
                    )
                }

                SkillDashboardViewMode.CATEGORIES -> {
                    CategoriesDashboardView(
                        skills = uiState.allSkills,
                        projects = uiState.projects,
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
                    SkillDashboardViewMode.TREE_MAP -> "Mühendislik Yetenek Ağacı"
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
