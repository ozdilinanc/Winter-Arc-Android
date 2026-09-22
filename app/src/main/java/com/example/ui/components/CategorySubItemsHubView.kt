package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
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
    val emoji: String = "",
    val description: String,
    val tag: String,
    val icon: ImageVector? = null
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
    categoryIcon: ImageVector? = null,
    onOpenThemePicker: () -> Unit = {},
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

        if (selectedSubItemId == "sub_anime_manhwa") {
            com.example.ui.components.anime.AnimeTrackingHubView(
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

    // Top-level stats calculation for 3-card header
    val allSubItemStats = remember(subItems, prefsUpdateTrigger, projects) {
        subItems.map { RoadmapProgressHelper.getSubItemStats(it.id, roadmapPrefs, projects) }
    }
    val totalCategoryItems = allSubItemStats.sumOf { it.totalCount }
    val totalCategoryCompleted = allSubItemStats.sumOf { it.completedCount }
    val totalCategoryProgressFraction = if (totalCategoryItems > 0) {
        (totalCategoryCompleted.toFloat() / totalCategoryItems.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val totalCategoryProgressPercent = (totalCategoryProgressFraction * 100).toInt().coerceIn(0, 100)

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header Navigation Row with Back, Title & Theme Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(vertical = 4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PanelNavyElevated,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Geri",
                                    tint = AccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = categoryTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 1.1.sp,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = categorySubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = accentColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
                }
            }

            // 2. Three Sleek Stat Cards (Inspired by Reference 5)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryMiniStatCard(
                        title = "Alt Alan",
                        value = "${subItems.size}",
                        accentColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryMiniStatCard(
                        title = "Toplam Konu",
                        value = "$totalCategoryItems",
                        accentColor = AccentAmber,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryMiniStatCard(
                        title = "İlerleme",
                        value = "%$totalCategoryProgressPercent",
                        accentColor = if (totalCategoryProgressPercent > 0) AccentEmerald else TextDarkMuted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Section Title
            item {
                Text(
                    text = "GELİŞİM MODÜLLERİ (${subItems.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        letterSpacing = 1.sp,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // 4. Sub-items List (Inspired by Reference 3 & 5 Left Side)
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 14.dp,
                                    end = 14.dp,
                                    top = 14.dp,
                                    bottom = if (stats.progressFraction > 0f) 12.dp else 14.dp
                                )
                        ) {
                            // Top Row: 44dp Icon + (Title & Subtitle) + Circular Action/Percentage Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // 44x44dp Icon Box
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(accentColor.copy(alpha = 0.12f))
                                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.icon != null) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = accentColor,
                                                modifier = Modifier.size(23.dp)
                                            )
                                        } else {
                                            Text(text = item.emoji, fontSize = 21.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(13.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                fontSize = 15.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Circular Action / Percentage Button (Image 5 style)
                                Surface(
                                    shape = CircleShape,
                                    color = if (stats.progressPercent > 0) accentColor.copy(alpha = 0.12f) else PanelNavyHighlight,
                                    border = BorderStroke(1.dp, if (stats.progressPercent > 0) accentColor.copy(alpha = 0.35f) else BorderSubtle),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (stats.progressPercent == 100) {
                                            Icon(
                                                imageVector = Icons.Outlined.CheckCircle,
                                                contentDescription = null,
                                                tint = AccentEmerald,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (stats.progressPercent > 0) {
                                            Text(
                                                text = "%${stats.progressPercent}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = accentColor,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = TextMuted,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom Row: Spacious Tag Pill & Topic Counter Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PanelNavy,
                                        border = BorderStroke(1.dp, BorderSubtle)
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

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PanelNavy,
                                        border = BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Text(
                                            text = when (item.id) {
                                                "sub_personal_projects", "sub_projects" -> "${stats.completedCount}/${stats.totalCount} Proje"
                                                "sub_btk_akademi" -> "${stats.completedCount}/${stats.totalCount} Sertifika"
                                                "sub_anime_manhwa" -> "${stats.completedCount}/${stats.totalCount} Seri"
                                                else -> "${stats.completedCount}/${stats.totalCount} Konu"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (stats.completedCount > 0) AccentEmerald else TextDarkMuted,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 10.5.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (stats.progressFraction > 0f) {
                            LinearProgressIndicator(
                                progress = { stats.progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = accentColor,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryMiniStatCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontSize = 17.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
