package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import com.example.data.model.CustomTopicItem
import com.example.data.model.CustomTopicRepository
import com.example.data.model.MediumRoadmapSeed
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
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("winter_arc_roadmap_progress", Context.MODE_PRIVATE) }
    val hapticEngine = rememberHapticEngine()

    var refreshTrigger by remember { mutableIntStateOf(0) }
    val isCustomizable = remember(subItemId) { CustomTopicRepository.isCustomizable(subItemId) }
    val genres = remember(subItemId) { CustomTopicRepository.getGenresForSubItem(subItemId) }

    var isAddDialogOpen by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<TopicCheckItem?>(null) }
    var selectedGenreFilter by remember(subItemId) { mutableStateOf<String?>(null) }

    val roadmap: SubItemRoadmap = remember(subItemId, refreshTrigger) {
        CustomTopicRepository.getEffectiveRoadmap(prefs, subItemId)
            ?: RoadmapDataStore.allRoadmaps[subItemId]
    } ?: return

    // Load persistent progress states for topics
    var itemStates by remember(subItemId, refreshTrigger) {
        mutableStateOf(
            roadmap.sections.flatMap { it.items }.associate { item ->
                val savedKey = prefs.getString("status_${item.id}", null)
                item.id to TopicProgressState.fromKey(savedKey)
            }
        )
    }

    fun updateItemState(itemId: String, newState: TopicProgressState) {
        itemStates = itemStates + (itemId to newState)
        prefs.edit().putString("status_$itemId", newState.key).commit()
        if (newState == TopicProgressState.COMPLETED) {
            hapticEngine.vibrateLevelUp()
        } else {
            hapticEngine.vibrateSkillCompleted()
        }
    }

    fun handleAddItem(newItem: CustomTopicItem) {
        CustomTopicRepository.addCustomItem(prefs, newItem)
        refreshTrigger++
        isAddDialogOpen = false
        Toast.makeText(context, "${newItem.title} başarıyla eklendi!", Toast.LENGTH_SHORT).show()
    }

    fun handleDeleteItem(item: TopicCheckItem) {
        CustomTopicRepository.deleteItem(prefs, subItemId, item.id)
        refreshTrigger++
        itemToDelete = null
        hapticEngine.vibrateSelection()
        Toast.makeText(context, "${item.title} silindi.", Toast.LENGTH_SHORT).show()
    }

    val totalCount = roadmap.sections.sumOf { it.items.size }
    val notStartedCount = itemStates.values.count { it == TopicProgressState.NOT_STARTED }
    val theoryCount = itemStates.values.count { it == TopicProgressState.THEORY }
    val practiceCount = itemStates.values.count { it == TopicProgressState.PRACTICED }
    val completedCount = itemStates.values.count { it == TopicProgressState.COMPLETED }

    val earnedPoints = theoryCount * 0.35f + practiceCount * 0.70f + completedCount * 1.0f
    val progressFraction = if (totalCount > 0) (earnedPoints / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
    val progressPercent = (progressFraction * 100).toInt().coerceIn(0, 100)

    var searchQuery by remember { mutableStateOf("") }

    val filteredSections = remember(roadmap.sections, searchQuery, selectedGenreFilter) {
        val baseSections = if (selectedGenreFilter == null) {
            roadmap.sections
        } else {
            roadmap.sections.filter { it.title.equals(selectedGenreFilter, ignoreCase = true) }
        }

        if (searchQuery.isBlank()) {
            baseSections
        } else {
            baseSections.mapNotNull { section ->
                val matchesSectionTitle = section.title.contains(searchQuery, ignoreCase = true)
                val matchingItems = section.items.filter { item ->
                    item.title.contains(searchQuery, ignoreCase = true) ||
                            item.description.contains(searchQuery, ignoreCase = true)
                }
                if (matchesSectionTitle) {
                    section
                } else if (matchingItems.isNotEmpty()) {
                    section.copy(items = matchingItems)
                } else null
            }
        }
    }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBack() }
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

                if (subItemId == "sub_btk_akademi") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentCyan.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.btkakademi.gov.tr"))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌐 btkakademi.gov.tr", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        }
                    }
                }
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

                    if (subItemId == "sub_btk_akademi") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentEmerald.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Taktik: Videoları 1.5x hızda izleyebilir, final testinden 70+ alarak e-Devlet barkodlu resmi sertifikanı ücretsiz indirebilirsin.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AccentEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

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
                            icon = Icons.Outlined.CheckCircle,
                            label = if (subItemId == "sub_btk_akademi") "$completedCount Sertifika Alındı" else "$completedCount Tamam",
                            color = AccentEmerald
                        )
                        StatusSummaryChip(
                            icon = Icons.Outlined.Build,
                            label = if (subItemId == "sub_btk_akademi") "$practiceCount Sınav / Quiz" else "$practiceCount Pratik",
                            color = AccentAmber
                        )
                        StatusSummaryChip(
                            icon = Icons.AutoMirrored.Outlined.MenuBook,
                            label = if (subItemId == "sub_btk_akademi") "$theoryCount İzleniyor" else "$theoryCount Teori",
                            color = Color(0xFF38BDF8)
                        )
                        StatusSummaryChip(
                            icon = Icons.Outlined.RadioButtonUnchecked,
                            label = "$notStartedCount Bekleyen",
                            color = TextMuted
                        )
                    }

                    if (isCustomizable) {
                        Spacer(modifier = Modifier.height(14.dp))
                        val addLabel = when (subItemId) {
                            "sub_reading_books" -> "+ Yeni Kitap Ekle"
                            "sub_card_sleights" -> "+ Yeni Kart Numarası Ekle"
                            "sub_anime_manhwa" -> "+ Yeni Anime / Manhwa Ekle"
                            else -> "+ Yeni İçerik Ekle"
                        }
                        Button(
                            onClick = {
                                hapticEngine.vibrateSelection()
                                isAddDialogOpen = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = CanvasDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = addLabel,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CanvasDark,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Search Bar (Essential for large roadmaps like Medium 285 articles)
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (subItemId == "sub_medium") "285 makale içinde ara (örn: JWT, LLM, Docker)..." else "Konu veya pratik ara...",
                        color = TextDarkMuted,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Temizle",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PanelNavyElevated,
                    unfocusedContainerColor = PanelNavyElevated,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        // Genre Filter Chips Bar for Customizable Topics
        if (isCustomizable) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalAllCount = roadmap.sections.sumOf { it.items.size }
                    val isAllSelected = selectedGenreFilter == null
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAllSelected) accentColor.copy(alpha = 0.22f) else PanelNavyElevated,
                        border = BorderStroke(1.dp, if (isAllSelected) accentColor else BorderSubtle),
                        modifier = Modifier.clickable {
                            hapticEngine.vibrateSelection()
                            selectedGenreFilter = null
                        }
                    ) {
                        Text(
                            text = "Tümü ($totalAllCount)",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) accentColor else TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    genres.forEach { genre ->
                        val isSelected = selectedGenreFilter.equals(genre.title, ignoreCase = true)
                        val count = roadmap.sections.find { it.title.equals(genre.title, ignoreCase = true) }?.items?.size ?: 0
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.22f) else PanelNavyElevated,
                            border = BorderStroke(1.dp, if (isSelected) accentColor else BorderSubtle),
                            modifier = Modifier.clickable {
                                hapticEngine.vibrateSelection()
                                selectedGenreFilter = if (isSelected) null else genre.title
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = genre.emoji, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (count > 0) "${genre.title} ($count)" else genre.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accentColor else TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Empty state when filtering by a genre with no entries yet
        if (isCustomizable && filteredSections.isEmpty() && selectedGenreFilter != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "'$selectedGenreFilter' türünde henüz eklenmiş içerik yok.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                hapticEngine.vibrateSelection()
                                isAddDialogOpen = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Bu Türe Yeni İçerik Ekle",
                                color = CanvasDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Universal 12-Step Technical Article Template Card for Medium
        if (subItemId == "sub_medium") {
            item {
                MediumArticleTemplateCard(accentColor = accentColor)
            }
        }

        // Section Cards with Deep Topic Cards
        items(filteredSections, key = { it.title }) { section ->
            TopicSectionCard(
                section = section,
                accentColor = accentColor,
                itemStates = itemStates,
                onUpdateState = { itemId, newState ->
                    updateItemState(itemId, newState)
                },
                onDeleteItem = if (isCustomizable) { item -> itemToDelete = item } else null
            )
        }
    }

    if (isAddDialogOpen) {
        AddTopicItemDialog(
            subItemId = subItemId,
            accentColor = accentColor,
            onDismiss = { isAddDialogOpen = false },
            onAdd = { newItem -> handleAddItem(newItem) }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = "İçeriği Sil",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "\"${itemToDelete!!.title}\" içeriğini kaldırmak istediğinize emin misiniz?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = { handleDeleteItem(itemToDelete!!) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Sil", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("İptal", color = TextMuted)
                }
            },
            containerColor = PanelNavyElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun StatusSummaryChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
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
    onUpdateState: (String, TopicProgressState) -> Unit,
    onDeleteItem: ((TopicCheckItem) -> Unit)? = null
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
                            onUpdateState = { newState -> onUpdateState(item.id, newState) },
                            onDeleteItem = if (onDeleteItem != null) { { onDeleteItem(item) } } else null
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
    onUpdateState: (TopicProgressState) -> Unit,
    onDeleteItem: (() -> Unit)? = null
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
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (state == TopicProgressState.NOT_STARTED) Color.Transparent
                        else stateColor.copy(alpha = 0.2f)
                    )
                    .border(
                        1.5.dp,
                        if (state == TopicProgressState.NOT_STARTED) TextMuted else stateColor,
                        CircleShape
                    )
                    .clickable { onUpdateState(state.next()) },
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

            if (onDeleteItem != null) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { onDeleteItem() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Sil",
                        tint = TextDarkMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
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

                // 1. Sabit Hedef Görevi (Her konunun kalıcı uygulama hedefi - durumdan bağımsız)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PanelNavy,
                    border = BorderStroke(
                        1.dp,
                        if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED)
                            AccentEmerald.copy(alpha = 0.35f)
                        else
                            BorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HEDEF UYGULAMA GÖREVİ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentCyan,
                                        fontSize = 10.5.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED)
                                    AccentEmerald.copy(alpha = 0.15f)
                                else
                                    PanelNavyElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED)
                                        AccentEmerald.copy(alpha = 0.4f)
                                    else
                                        BorderSubtle
                                )
                            ) {
                                Text(
                                    text = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED) "✓ Kodlandı" else "⏳ Bekliyor",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state == TopicProgressState.PRACTICED || state == TopicProgressState.COMPLETED) AccentEmerald else TextMuted
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.effectivePracticeTask(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Kişisel Çalışma Aşaması (Senin İlerlemen)
                Text(
                    text = "ÇALIŞMA AŞAMAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 4 Eşit Aşamalı Durum Seçici Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                        val stageLabel = when (s) {
                            TopicProgressState.NOT_STARTED -> "Başlanmadı"
                            TopicProgressState.THEORY -> "Teori"
                            TopicProgressState.PRACTICED -> "Pratik"
                            TopicProgressState.COMPLETED -> "Bitti"
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) pillColor.copy(alpha = 0.22f) else PanelNavy,
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) pillColor else BorderSubtle.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onUpdateState(s) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = s.emoji, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stageLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) pillColor else TextSecondary,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Sıradaki Adım Hızlı Aksiyon Butonu
                when (state) {
                    TopicProgressState.NOT_STARTED -> {
                        OutlinedButton(
                            onClick = { onUpdateState(TopicProgressState.THEORY) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF38BDF8).copy(alpha = 0.12f)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Teoriyi İnceledim Olarak İşaretle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                    TopicProgressState.THEORY -> {
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
                                text = "Görevi Kodladım & Uyguladım (Pratik Tamam)",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentEmerald.copy(alpha = 0.1f))
                                .border(1.dp, AccentEmerald.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AccentEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tebrikler! Hem teorisi hem de pratik görevi başarıyla tamamlandı.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AccentEmerald,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediumArticleTemplateCard(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val hapticEngine = rememberHapticEngine()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Article,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "12 MADDELİK YAZI ŞABLONU",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.8.sp,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "Tüm 285 makale için evrensel iskelet & format",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = accentColor
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Bir konuyu 'gerçekten anladım' diyebilmek ve hafızaya kazımak için önerilen 12 alt başlık:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MediumRoadmapSeed.templateSteps.chunked(2).forEach { rowSteps ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSteps.forEach { step ->
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(6.dp),
                                    color = PanelNavyHighlight,
                                    border = BorderStroke(1.dp, BorderSubtle)
                                ) {
                                    Text(
                                        text = step,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                            if (rowSteps.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            hapticEngine.vibrateSkillCompleted()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("MediumArticleTemplate", MediumRoadmapSeed.ARTICLE_TEMPLATE_MARKDOWN)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "12 maddelik makale şablonu panoya kopyalandı!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = CanvasDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Markdown Şablonunu Panoya Kopyala",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CanvasDark,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
