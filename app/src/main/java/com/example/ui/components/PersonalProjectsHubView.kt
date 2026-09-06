package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EngineeringProject
import com.example.data.model.ProjectWorkflowStage
import com.example.ui.theme.*

@Composable
fun PersonalProjectsHubView(
    projects: List<EngineeringProject>,
    onAdvanceStage: (String, ProjectWorkflowStage) -> Unit,
    onRegressStage: (String) -> Unit,
    onCreateProject: (String, String, String, ProjectWorkflowStage, String, String, String, List<String>) -> Unit,
    onDeleteProject: (String) -> Unit,
    onBack: () -> Unit,
    accentColor: Color = BranchDotNet,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var projectToDelete by remember { mutableStateOf<EngineeringProject?>(null) }

    BackHandler {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            onBack()
        }
    }

    if (selectedTab == 1) {
        // Tab 1: Step-by-step checklist of Publishing & Sharing Journeys
        SubItemChecklistView(
            subItemId = "sub_personal_projects",
            accentColor = accentColor,
            onBack = { selectedTab = 0 },
            modifier = modifier
        )
        return
    }

    val filteredProjects = remember(projects, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            projects
        } else {
            projects.filter { it.category.contains(selectedCategoryFilter!!, ignoreCase = true) }
        }
    }

    val liveCount = remember(projects) { projects.count { it.currentStage.order >= 6 } }
    val devCount = remember(projects) { projects.count { it.currentStage.order < 6 } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CanvasDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasDark)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Back Button Row
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
                        text = "KATEGORİLERE DÖN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            letterSpacing = 1.2.sp
                        )
                    )
                }
            }

            // 2. Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(accentColor.copy(alpha = 0.15f))
                                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🛠️", fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "KİŞİSEL PROJELER",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                    Text(
                                        text = "Lansman, Vitrin & Paylaşma Serüveni",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accentColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { isAddDialogOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("hub_add_project_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Yeni Proje",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Bir projeyi sadece kodlamak yetmez; mimarisini kurup, testlerini yazıp, Docker/AAB ile paketleyip Play Store'a, GitHub'a, Medium ve LinkedIn'e çıkararak görünür kılma serüveni.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricPill("📦 Toplam Proje", "${projects.size}", AccentCyan)
                            MetricPill("🚀 Canlıda / Yayında", "$liveCount", StatusCompleted)
                            MetricPill("💻 Geliştirmede", "$devCount", AccentAmber)
                        }
                    }
                }
            }

            // 3. Segmented Tab Switch
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PanelNavy)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    TabButton(
                        title = "🚀 Aktif Projelerim (${projects.size})",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        title = "📋 Yayın & Paylaşma Rehberi",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Category Filter Chips (For Tab 0)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterCategoryChip(
                        label = "Tümü (${projects.size})",
                        isSelected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null }
                    )
                    FilterCategoryChip(
                        label = "📱 Mobil Uygulama",
                        isSelected = selectedCategoryFilter == "Android",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "Android") null else "Android"
                        }
                    )
                    FilterCategoryChip(
                        label = "🌐 .NET Backend",
                        isSelected = selectedCategoryFilter == "Backend",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "Backend") null else "Backend"
                        }
                    )
                    FilterCategoryChip(
                        label = "🔬 Sistem / Tez",
                        isSelected = selectedCategoryFilter == "Systems" || selectedCategoryFilter == "Graduation",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter != null && (selectedCategoryFilter == "Systems" || selectedCategoryFilter == "Graduation")) null else "Systems"
                        }
                    )
                }
            }

            // 5. Active Projects List
            if (filteredProjects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = PanelNavy)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📭", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bu filtrede henüz proje bulunmuyor.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { isAddDialogOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PanelNavyHighlight),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderActive)
                            ) {
                                Text(text = "+ Yeni Proje Başlat", color = AccentCyan, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                items(filteredProjects, key = { it.id }) { project ->
                    HubProjectCard(
                        project = project,
                        onAdvanceStage = { nextStage -> onAdvanceStage(project.id, nextStage) },
                        onRegressStage = { onRegressStage(project.id) },
                        onDeleteClick = { projectToDelete = project }
                    )
                }
            }
        }
    }

    // Add Project Modal Dialog
    if (isAddDialogOpen) {
        AddProjectModal(
            onDismiss = { isAddDialogOpen = false },
            onSave = { title, category, description, stage, github, medium, notes, tags ->
                onCreateProject(title, category, description, stage, github, medium, notes, tags)
                isAddDialogOpen = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            containerColor = PanelNavyElevated,
            title = {
                Text(text = "Projeyi Sil?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "\"${projectToDelete?.title}\" projesini takip listenizden kaldırmak istediğinize emin misiniz?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { onDeleteProject(it.id) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(text = "Sil", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text(text = "İptal", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun MetricPill(label: String, value: String, valueColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PanelNavy)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = valueColor
                )
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) PanelNavyHighlight else Color.Transparent,
        label = "tab_bg"
    )
    val textColor = if (isSelected) TextPrimary else TextMuted

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun FilterCategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AccentCyan else BorderSubtle
    val bgColor = if (isSelected) AccentCyan.copy(alpha = 0.15f) else PanelNavy
    val textColor = if (isSelected) AccentCyan else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        )
    }
}

@Composable
private fun HubProjectCard(
    project: EngineeringProject,
    onAdvanceStage: (ProjectWorkflowStage) -> Unit,
    onRegressStage: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val stages = ProjectWorkflowStage.values()
    val currentIndex = project.currentStage.order - 1

    val (catLabel, catColor, catEmoji) = when {
        project.category.contains("Android", ignoreCase = true) || project.category.contains("Mobile", ignoreCase = true) ->
            Triple("MOBİL UYGULAMA", BranchAndroid, "📱")
        project.category.contains("Backend", ignoreCase = true) || project.category.contains(".NET", ignoreCase = true) ->
            Triple(".NET BACKEND", BranchDotNet, "🌐")
        project.category.contains("Graduation", ignoreCase = true) || project.category.contains("Systems", ignoreCase = true) ->
            Triple("SİSTEM / BİTİRME TEZİ", BranchGraduation, "🔬")
        else ->
            Triple(project.category.uppercase(), AccentPurple, "🛠️")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavy)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category Badge & Current Stage Indicator & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(catColor.copy(alpha = 0.15f))
                            .border(1.dp, catColor.copy(alpha = 0.35f), RoundedCornerShape(5.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$catEmoji $catLabel",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = catColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(StatusPracticed.copy(alpha = 0.18f))
                            .border(1.dp, StatusPracticed.copy(alpha = 0.4f), RoundedCornerShape(5.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${project.currentStage.order}. Aşama: ${project.currentStage.stageName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = StatusPracticed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Projeyi Sil",
                        tint = TextDarkMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Project Title
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            )

            // Tags
            if (project.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    project.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PanelNavyElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Workflow Pipeline Progress Steps
            Text(
                text = "LANSMAN & YAYIN PİPELİNE'I",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stages.forEachIndexed { index, stage ->
                    val isCompleted = index < currentIndex
                    val isCurrent = index == currentIndex

                    val badgeBorderColor = when {
                        isCurrent -> AccentCyan
                        isCompleted -> StatusCompleted
                        else -> BorderSubtle
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isCurrent) AccentCyan.copy(alpha = 0.2f)
                                else if (isCompleted) StatusCompleted.copy(alpha = 0.12f)
                                else PanelNavyElevated
                            )
                            .border(1.dp, badgeBorderColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = StatusCompleted,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "${stage.order}. ${stage.stageName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) AccentCyan else if (isCompleted) TextPrimary else TextMuted
                                )
                            )
                        }
                    }

                    if (index < stages.size - 1) {
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // URLs and Stage Control Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // URLs Column
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    if (project.githubRepo.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = project.githubRepo,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                    if (project.mediumArticleUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Article,
                                contentDescription = null,
                                tint = StatusCompleted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = project.mediumArticleUrl,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = StatusCompleted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Stage Stepping Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Regress Stage Button
                    if (currentIndex > 0) {
                        FilledTonalButton(
                            onClick = onRegressStage,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = PanelNavyHighlight
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Geri Al",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Advance Stage Button
                    if (currentIndex < stages.size - 1) {
                        val nextStage = stages[currentIndex + 1]
                        Button(
                            onClick = { onAdvanceStage(nextStage) },
                            colors = ButtonDefaults.buttonColors(containerColor = PanelNavyHighlight),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderActive)
                        ) {
                            Text(
                                text = nextStage.stageName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    } else {
                        // Completed badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StatusCompleted.copy(alpha = 0.2f))
                                .border(1.dp, StatusCompleted, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🎉 Yayında & CV'de",
                                color = StatusCompleted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddProjectModal(
    onDismiss: () -> Unit,
    onSave: (String, String, String, ProjectWorkflowStage, String, String, String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Android / Mobil") }
    var description by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var medium by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }

    val categories = listOf("Android / Mobil", "Backend / .NET", "Sistem / Bitirme Tezi", "Full-Stack")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderActive, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚀 Yeni Proje Başlat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Proje Adı", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Selector Chips
                Text(
                    text = "Kategori Seçimi",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else PanelNavy)
                                .border(1.dp, if (isSelected) AccentCyan else BorderSubtle, RoundedCornerShape(6.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                color = if (isSelected) AccentCyan else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama & Hedef", fontSize = 12.sp) },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // GitHub Repo
                OutlinedTextField(
                    value = github,
                    onValueChange = { github = it },
                    label = { Text("GitHub Repo (isteğe bağlı)", fontSize = 12.sp) },
                    placeholder = { Text("github.com/user/project", fontSize = 11.sp, color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Etiketler (Virgülle ayır)", fontSize = 12.sp) },
                    placeholder = { Text("Kotlin, Room, EF Core, Docker", fontSize = 11.sp, color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val tags = tagsText.split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                            onSave(
                                title,
                                selectedCategory,
                                description,
                                ProjectWorkflowStage.DEVELOP,
                                github,
                                medium,
                                "",
                                tags
                            )
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Projeyi Kaydet & Takibe Al",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
