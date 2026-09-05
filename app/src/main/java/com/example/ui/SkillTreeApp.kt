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
            StatsDashboardHeader(
                uiState = uiState,
                onSearchChange = viewModel::onSearchQueryChanged,
                onBranchFilter = viewModel::onSelectBranchFilter,
                onStatusFilter = viewModel::onSelectStatusFilter,
                onViewModeChange = viewModel::onSetViewMode,
                onOpenAchievements = { viewModel.setAchievementsDialogVisible(true) },
                onRestoreDailyBanner = viewModel::showDailyBanner
            )
        },
        bottomBar = {
            ElegantDarkBottomNav(
                currentMode = uiState.currentViewMode,
                onModeSelected = viewModel::onSetViewMode
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CanvasDark)
        ) {
            // Daily Skill Focus Banner
            DailySkillFocusBanner(
                focusSkill = uiState.currentDailyFocusSkill,
                inProgressCount = uiState.inProgressSkills.size,
                currentIndex = uiState.dailyFocusSkillIndex,
                isVisible = !uiState.isDailyBannerDismissed && uiState.currentDailyFocusSkill != null,
                onCompleteSkill = { skill ->
                    hapticEngine.vibrateSkillCompleted()
                    viewModel.onUpdateSkillStatus(skill.id, SkillStatus.COMPLETED)
                },
                onOpenSkill = { skill ->
                    viewModel.onSelectSkill(skill)
                },
                onCycleNextSkill = viewModel::cycleNextDailyFocusSkill,
                onDismiss = viewModel::dismissDailyBanner
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CanvasDark)
            ) {
            when (uiState.currentViewMode) {
                SkillDashboardViewMode.GRAPH_TREE -> {
                    SkillTreeGraphView(
                        skills = uiState.filteredSkills,
                        selectedSkill = uiState.selectedSkill,
                        onSkillClick = { skill -> viewModel.onSelectSkill(skill) }
                    )
                }

                SkillDashboardViewMode.BRANCH_LIST -> {
                    BranchExplorerView(
                        skills = uiState.filteredSkills,
                        selectedBranchFilter = uiState.selectedBranchFilter,
                        onSkillClick = { skill -> viewModel.onSelectSkill(skill) },
                        onStatusToggle = { skill ->
                            val next = when (skill.status) {
                                SkillStatus.NOT_STARTED -> SkillStatus.IN_PROGRESS
                                SkillStatus.IN_PROGRESS, SkillStatus.LEARNING -> SkillStatus.COMPLETED
                                SkillStatus.PRACTICED -> SkillStatus.COMPLETED
                                SkillStatus.COMPLETED -> SkillStatus.STRONG
                                SkillStatus.STRONG -> SkillStatus.NOT_STARTED
                            }
                            if (next.isCompletedOrMastered) {
                                hapticEngine.vibrateSkillCompleted()
                            }
                            viewModel.onCycleSkillStatus(skill.id)
                        }
                    )
                }

                SkillDashboardViewMode.PORTFOLIO_PIPELINE -> {
                    PortfolioPipelineView(
                        projects = uiState.projects,
                        onAdvanceStage = viewModel::advanceProjectStage,
                        onAddNewProjectClick = { viewModel.setAddProjectDialogVisible(true) }
                    )
                }

                SkillDashboardViewMode.KNOWLEDGE_LOOP -> {
                    KnowledgeLoopView()
                }
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
            Triple(SkillDashboardViewMode.GRAPH_TREE, "🧭", "MAP"),
            Triple(SkillDashboardViewMode.BRANCH_LIST, "🗂️", "MODULES"),
            Triple(SkillDashboardViewMode.PORTFOLIO_PIPELINE, "🚀", "PROJECTS"),
            Triple(SkillDashboardViewMode.KNOWLEDGE_LOOP, "📑", "JOURNAL")
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
                        letterSpacing = 1.6.sp,
                        color = if (isSelected) AccentIndigo else TextDarkMuted
                    )
                )
            }
        }
    }
}
