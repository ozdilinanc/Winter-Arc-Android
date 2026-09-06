package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.data.model.EngineeringProject
import com.example.data.model.ProjectWorkflowStage
import com.example.data.model.RoadmapDataStore
import com.example.data.model.RoadmapProgressHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class CategorySubItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val description: String,
    val tag: String
)

@Composable
fun CategorySubItemsHubView(
    categoryTitle: String,
    categoryEmoji: String,
    categorySubtitle: String,
    categoryDescription: String,
    accentColor: Color,
    subItems: List<CategorySubItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    projects: List<EngineeringProject> = emptyList(),
    onAdvanceProjectStage: (String, ProjectWorkflowStage) -> Unit = { _, _ -> },
    onRegressProjectStage: (String) -> Unit = {},
    onCreateProject: (String, String, String, ProjectWorkflowStage, String, String, String, List<String>) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onDeleteProject: (String) -> Unit = {}
) {
    var selectedSubItemId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val roadmapPrefs = remember { context.getSharedPreferences(RoadmapProgressHelper.PREFS_ROADMAP, Context.MODE_PRIVATE) }
    var prefsUpdateTrigger by remember { mutableIntStateOf(0) }

    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefsUpdateTrigger++
        }
    }

    DisposableEffect(roadmapPrefs) {
        roadmapPrefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            roadmapPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    LaunchedEffect(selectedSubItemId) {
        if (selectedSubItemId == null) {
            prefsUpdateTrigger++
        }
    }

    BackHandler {
        if (selectedSubItemId != null) {
            selectedSubItemId = null
        } else {
            onBack()
        }
    }

    if (selectedSubItemId != null) {
        if (selectedSubItemId == "sub_personal_projects" || selectedSubItemId == "sub_projects") {
            PersonalProjectsHubView(
                projects = projects,
                onAdvanceStage = onAdvanceProjectStage,
                onRegressStage = onRegressProjectStage,
                onCreateProject = onCreateProject,
                onDeleteProject = onDeleteProject,
                onBack = { selectedSubItemId = null },
                accentColor = accentColor,
                modifier = modifier
            )
            return
        }

        SubItemChecklistView(
            subItemId = selectedSubItemId!!,
            accentColor = accentColor,
            onBack = { selectedSubItemId = null },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CanvasDark,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = PanelNavyHighlight,
                    contentColor = TextPrimary,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = data.visuals.message, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasDark)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Back Button Bar
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

            // Category Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = categoryEmoji, fontSize = 26.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "$categoryTitle HUB",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = categorySubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = accentColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = categoryDescription,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }

            // Sub-modules Title
            item {
                Text(
                    text = "$categoryTitle ALT BAŞLIKLARI (${subItems.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Sub-items List
            items(subItems, key = { it.id }) { item ->
                val stats = remember(item.id, prefsUpdateTrigger, projects) {
                    RoadmapProgressHelper.getSubItemStats(item.id, roadmapPrefs, projects)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .clickable {
                            if (RoadmapDataStore.allRoadmaps.containsKey(item.id) || item.id == "sub_personal_projects" || item.id == "sub_projects") {
                                selectedSubItemId = item.id
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("${item.title} detayları hazırlanıyor...")
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(accentColor.copy(alpha = 0.12f))
                                        .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.emoji, fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = accentColor,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Metric Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (stats.progressPercent > 0) "%${stats.progressPercent} İlerleme" else "%0 Başlanmadı",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (stats.progressPercent > 0) accentColor else TextDarkMuted,
                                    fontSize = 11.5.sp
                                )
                            )

                            Text(
                                text = when (item.id) {
                                    "sub_personal_projects", "sub_projects" -> "${stats.completedCount}/${stats.totalCount} Proje Bitti"
                                    "sub_btk_akademi" -> "${stats.completedCount}/${stats.totalCount} Sertifika"
                                    else -> "${stats.completedCount}/${stats.totalCount} Tamamlandı"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (stats.completedCount > 0) TextSecondary else TextDarkMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { stats.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = accentColor,
                            trackColor = PanelNavy
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = item.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accentColor,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            if (stats.practiceCount > 0 || stats.theoryCount > 0) {
                                Text(
                                    text = "🛠️ ${stats.practiceCount} Pratik • 📘 ${stats.theoryCount} Teori",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextDarkMuted,
                                        fontSize = 10.sp
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
