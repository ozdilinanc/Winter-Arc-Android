package com.example.ui.components.anime

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.anime.AnimeApiService
import com.example.data.model.anime.AnimeItem
import com.example.data.model.anime.AnimeSearchItem
import com.example.data.model.anime.AnimeUserStats
import com.example.data.model.anime.AnimeWatchStatus
import com.example.data.model.anime.MediaTypeCategory
import com.example.data.repository.AnimeRepository
import com.example.ui.theme.LocalAppPalette
import com.example.ui.util.rememberHapticEngine
import kotlinx.coroutines.launch

private enum class AnimeFilter(val label: String) {
    ALL("Tümü"),
    IN_PROGRESS("Devam Eden"),
    PLAN("Listemde"),
    COMPLETED("Tamamlandı"),
    OTHER("Diğer")
}

@Composable
fun AnimeTrackingHubView(
    onBack: () -> Unit,
    accentColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val palette = LocalAppPalette.current
    val effectiveAccent = accentColor ?: palette.accentCyan
    val hapticEngine = rememberHapticEngine()
    val coroutineScope = rememberCoroutineScope()

    // State from repository
    val allMediaList by AnimeRepository.animeFlow.collectAsState()
    LaunchedEffect(Unit) {
        AnimeRepository.getAnimeList(context)
    }

    var activeCategory by remember { mutableStateOf(MediaTypeCategory.ANIME) }
    var currentFilter by remember { mutableStateOf(AnimeFilter.ALL) }
    var showMalSyncDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var selectedAnimeForEdit by remember { mutableStateOf<AnimeItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val lastMalUser = remember(allMediaList) { AnimeRepository.getLastMalUsername(context) }

    // Media list filtered by active category (Anime vs Manga)
    val categoryList = remember(allMediaList, activeCategory) {
        allMediaList.filter { it.category == activeCategory }
    }

    val stats = remember(categoryList) {
        AnimeRepository.computeStats(categoryList, activeCategory)
    }

    val filteredList = remember(categoryList, currentFilter, searchQuery) {
        categoryList.filter { anime ->
            val matchesFilter = when (currentFilter) {
                AnimeFilter.ALL -> true
                AnimeFilter.IN_PROGRESS -> anime.status == AnimeWatchStatus.WATCHING
                AnimeFilter.PLAN -> anime.status == AnimeWatchStatus.PLAN_TO_WATCH
                AnimeFilter.COMPLETED -> anime.status == AnimeWatchStatus.COMPLETED
                AnimeFilter.OTHER -> anime.status == AnimeWatchStatus.ON_HOLD || anime.status == AnimeWatchStatus.DROPPED
            }
            val matchesSearch = searchQuery.isBlank() ||
                    anime.title.contains(searchQuery, ignoreCase = true) ||
                    (anime.titleEnglish?.contains(searchQuery, ignoreCase = true) == true)
            matchesFilter && matchesSearch
        }
    }

    BackHandler {
        if (showMalSyncDialog) {
            showMalSyncDialog = false
        } else if (showSearchDialog) {
            showSearchDialog = false
        } else if (selectedAnimeForEdit != null) {
            selectedAnimeForEdit = null
        } else {
            onBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = palette.canvasDark,
        topBar = {
            SleekTopBar(
                onBack = onBack,
                lastMalUsername = lastMalUser,
                onOpenMalSync = { showMalSyncDialog = true },
                onOpenSearch = { showSearchDialog = true },
                accentColor = effectiveAccent
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category Switcher: Anime vs Manga/Manhwa
            item {
                CategorySegmentedControl(
                    activeCategory = activeCategory,
                    animeCount = allMediaList.count { it.category == MediaTypeCategory.ANIME },
                    mangaCount = allMediaList.count { it.category == MediaTypeCategory.MANGA },
                    onSelect = {
                        hapticEngine.vibrateSelection()
                        activeCategory = it
                    },
                    accentColor = effectiveAccent
                )
            }

            // Stats Header
            item {
                MediaStatsHeader(
                    stats = stats,
                    category = activeCategory,
                    accentColor = effectiveAccent
                )
            }

            // Filter Tabs & Search Bar Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Local Quick Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Listenizde filtreleyin...",
                                color = palette.textDarkMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = "Filtrele",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Temizle",
                                        tint = palette.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = palette.panelNavy,
                            unfocusedContainerColor = palette.panelNavy.copy(alpha = 0.7f),
                            focusedBorderColor = effectiveAccent,
                            unfocusedBorderColor = palette.borderSubtle,
                            focusedTextColor = palette.textPrimary,
                            unfocusedTextColor = palette.textPrimary
                        )
                    )

                    // Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(AnimeFilter.entries) { filter ->
                            val isSelected = currentFilter == filter
                            val count = when (filter) {
                                AnimeFilter.ALL -> categoryList.size
                                AnimeFilter.IN_PROGRESS -> stats.inProgressCount
                                AnimeFilter.PLAN -> stats.planCount
                                AnimeFilter.COMPLETED -> stats.completedCount
                                AnimeFilter.OTHER -> categoryList.count { it.status == AnimeWatchStatus.ON_HOLD || it.status == AnimeWatchStatus.DROPPED }
                            }

                            val labelText = when (filter) {
                                AnimeFilter.ALL -> "Tümü"
                                AnimeFilter.IN_PROGRESS -> if (activeCategory == MediaTypeCategory.MANGA) "Okunuyor" else "İzleniyor"
                                AnimeFilter.PLAN -> if (activeCategory == MediaTypeCategory.MANGA) "Okunacak" else "İzlenecek"
                                AnimeFilter.COMPLETED -> "Tamamlandı"
                                AnimeFilter.OTHER -> "Diğer"
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    hapticEngine.vibrateSelection()
                                    currentFilter = filter
                                },
                                label = {
                                    Text(
                                        text = "$labelText ($count)",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = effectiveAccent.copy(alpha = 0.2f),
                                    selectedLabelColor = effectiveAccent,
                                    containerColor = palette.panelNavy,
                                    labelColor = palette.textSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = palette.borderSubtle,
                                    selectedBorderColor = effectiveAccent
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Media Cards List
            if (filteredList.isEmpty()) {
                item {
                    EmptyMediaState(
                        category = activeCategory,
                        filter = currentFilter,
                        onAdd = { showSearchDialog = true },
                        onSyncMal = { showMalSyncDialog = true },
                        accentColor = effectiveAccent
                    )
                }
            } else {
                items(filteredList, key = { it.id }) { anime ->
                    MediaListItemCard(
                        anime = anime,
                        onIncrementUnit = {
                            hapticEngine.vibrateSkillCompleted()
                            AnimeRepository.incrementEpisode(context, anime.id)
                        },
                        onDecrementUnit = {
                            hapticEngine.vibrateSelection()
                            AnimeRepository.decrementEpisode(context, anime.id)
                        },
                        onClickEdit = {
                            selectedAnimeForEdit = anime
                        },
                        accentColor = effectiveAccent
                    )
                }
            }
        }
    }

    // MAL Sync Dialog
    if (showMalSyncDialog) {
        MalSyncDialog(
            onDismiss = { showMalSyncDialog = false },
            onSyncSuccess = { animeCount, mangaCount ->
                coroutineScope.launch {
                    showMalSyncDialog = false
                    hapticEngine.vibrateSkillCompleted()
                }
            },
            accentColor = effectiveAccent
        )
    }

    // Live Search & Add Dialog
    if (showSearchDialog) {
        MediaSearchDialog(
            initialCategory = activeCategory,
            onDismiss = { showSearchDialog = false },
            onItemAdded = { item ->
                hapticEngine.vibrateSkillCompleted()
                AnimeRepository.addOrUpdateAnime(context, item)
            },
            accentColor = effectiveAccent
        )
    }

    // Edit Dialog (Score editing removed; MAL sync info added)
    selectedAnimeForEdit?.let { anime ->
        MediaDetailDialog(
            anime = anime,
            onDismiss = { selectedAnimeForEdit = null },
            onSave = { updated ->
                AnimeRepository.addOrUpdateAnime(context, updated)
                selectedAnimeForEdit = null
            },
            onDelete = {
                AnimeRepository.deleteAnime(context, anime.id)
                selectedAnimeForEdit = null
            },
            accentColor = effectiveAccent
        )
    }
}

@Composable
private fun SleekTopBar(
    onBack: () -> Unit,
    lastMalUsername: String?,
    onOpenMalSync: () -> Unit,
    onOpenSearch: () -> Unit,
    accentColor: Color
) {
    val palette = LocalAppPalette.current

    Surface(
        color = palette.panelNavy,
        border = BorderStroke(1.dp, palette.borderSubtle),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(38.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = palette.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Anime & Manga Hub",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        fontSize = 16.sp
                    )
                )

                // Sleek MAL Account Status Chip
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.panelNavyElevated)
                        .clickable { onOpenMalSync() }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Sync,
                        contentDescription = null,
                        tint = palette.accentIndigo,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (!lastMalUsername.isNullOrBlank()) "@$lastMalUsername • Eşitle" else "MAL Hesabı Bağla",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = palette.accentIndigo,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Compact, Sleek Search / Add Action Button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                modifier = Modifier.clickable { onOpenSearch() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Ekle",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ara / Ekle",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySegmentedControl(
    activeCategory: MediaTypeCategory,
    animeCount: Int,
    mangaCount: Int,
    onSelect: (MediaTypeCategory) -> Unit,
    accentColor: Color
) {
    val palette = LocalAppPalette.current

    Surface(
        color = palette.panelNavy,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, palette.borderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // Anime Tab
            val isAnime = activeCategory == MediaTypeCategory.ANIME
            Surface(
                color = if (isAnime) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                border = if (isAnime) BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)) else null,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(MediaTypeCategory.ANIME) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎬 Animeler ($animeCount)",
                        fontSize = 13.sp,
                        fontWeight = if (isAnime) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAnime) accentColor else palette.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Manga & Manhwa Tab
            val isManga = activeCategory == MediaTypeCategory.MANGA
            Surface(
                color = if (isManga) palette.accentPurple.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                border = if (isManga) BorderStroke(1.dp, palette.accentPurple.copy(alpha = 0.6f)) else null,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(MediaTypeCategory.MANGA) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 Manga & Manhwa ($mangaCount)",
                        fontSize = 13.sp,
                        fontWeight = if (isManga) FontWeight.Bold else FontWeight.Medium,
                        color = if (isManga) palette.accentPurple else palette.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaStatsHeader(
    stats: AnimeUserStats,
    category: MediaTypeCategory,
    accentColor: Color
) {
    val palette = LocalAppPalette.current
    val unitName = if (category == MediaTypeCategory.MANGA) "Bölüm" else "Bölüm"
    val inProgressLabel = if (category == MediaTypeCategory.MANGA) "Okunuyor" else "İzleniyor"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Total
        MediaStatCard(
            title = "Toplam Seri",
            value = "${stats.totalCount}",
            subLabel = "${stats.inProgressCount} $inProgressLabel",
            accentColor = accentColor,
            modifier = Modifier.weight(1f)
        )

        // Watched / Read Units
        MediaStatCard(
            title = if (category == MediaTypeCategory.MANGA) "Okunan" else "İzlenen",
            value = "${stats.totalWatchedUnits}",
            subLabel = "Kayıtlı $unitName",
            accentColor = palette.accentEmerald,
            modifier = Modifier.weight(1f)
        )

        // Completed
        MediaStatCard(
            title = "Tamamlanan",
            value = "${stats.completedCount}",
            subLabel = "Bitirilen Seri",
            accentColor = palette.accentGold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MediaStatCard(
    title: String,
    value: String,
    subLabel: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = palette.panelNavy),
        border = BorderStroke(1.dp, palette.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = palette.textSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = palette.textDarkMuted,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MediaListItemCard(
    anime: AnimeItem,
    onIncrementUnit: () -> Unit,
    onDecrementUnit: () -> Unit,
    onClickEdit: () -> Unit,
    accentColor: Color
) {
    val context = LocalContext.current
    val palette = LocalAppPalette.current

    val animatedProgress by animateFloatAsState(
        targetValue = anime.progressFraction,
        label = "media_progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = palette.panelNavy),
        border = BorderStroke(
            1.dp,
            if (anime.isOutOfSync) palette.accentAmber.copy(alpha = 0.5f)
            else if (anime.isCompleted) palette.accentGold.copy(alpha = 0.35f)
            else palette.borderSubtle
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Poster Image
                Box(
                    modifier = Modifier
                        .width(84.dp)
                        .height(118.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.panelNavyElevated)
                        .border(1.dp, palette.borderSubtle, RoundedCornerShape(12.dp))
                ) {
                    if (anime.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(anime.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = anime.displayTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = palette.textDarkMuted,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Score Badge (Directly from MAL)
                    if (anime.score > 0) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = palette.accentGold,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = String.format("%.1f", anime.score),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Details Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title and Status
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = anime.displayTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                    fontSize = 14.5.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Status Badge
                            val statusBg = when (anime.status) {
                                AnimeWatchStatus.WATCHING -> palette.accentEmerald.copy(alpha = 0.15f)
                                AnimeWatchStatus.COMPLETED -> palette.accentGold.copy(alpha = 0.15f)
                                AnimeWatchStatus.PLAN_TO_WATCH -> palette.accentIndigo.copy(alpha = 0.15f)
                                else -> palette.panelNavyElevated
                            }
                            val statusColor = when (anime.status) {
                                AnimeWatchStatus.WATCHING -> palette.accentEmerald
                                AnimeWatchStatus.COMPLETED -> palette.accentGold
                                AnimeWatchStatus.PLAN_TO_WATCH -> palette.accentIndigo
                                else -> palette.textSecondary
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusBg,
                                border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.4f)),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = anime.status.getLabel(anime.category),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (anime.titleEnglish != null && anime.title != anime.titleEnglish) {
                            Text(
                                text = anime.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textDarkMuted,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Genres or Type
                        if (anime.genres.isNotEmpty()) {
                            Text(
                                text = (listOf(anime.mediaType) + anime.genres.take(2)).joinToString(" • "),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textSecondary,
                                    fontSize = 10.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Info & Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val totalStr = if (anime.totalEpisodes > 0) "${anime.totalEpisodes}" else "?"
                            val unitWord = if (anime.category == MediaTypeCategory.MANGA) "Bölüm" else "Bölüm"
                            Text(
                                text = "${anime.watchedEpisodes} / $totalStr $unitWord",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            )

                            Text(
                                text = "${(anime.progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (anime.isCompleted) palette.accentGold else accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (anime.isCompleted) palette.accentGold else accentColor,
                            trackColor = palette.panelNavyElevated
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onDecrementUnit,
                            enabled = anime.watchedEpisodes > 0,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(palette.panelNavyElevated)
                        ) {
                            Text(
                                text = "-1",
                                color = if (anime.watchedEpisodes > 0) palette.textSecondary else palette.textDarkMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onIncrementUnit,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (anime.isCompleted) palette.accentGold.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.2f),
                                contentColor = if (anime.isCompleted) palette.accentGold else accentColor
                            ),
                            border = BorderStroke(1.dp, if (anime.isCompleted) palette.accentGold.copy(alpha = 0.5f) else accentColor.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (anime.isCompleted) "Tekrar" else "+1 Bölüm",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onClickEdit,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(palette.panelNavyElevated)
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Detay & Düzenle",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // OUT OF SYNC INDICATOR BANNER
            if (anime.isOutOfSync) {
                Surface(
                    color = palette.accentAmber.copy(alpha = 0.12f),
                    border = BorderStroke(0.5.dp, palette.accentAmber.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MAL: ${anime.malWatchedEpisodes}. Bölüm • Yerel: ${anime.watchedEpisodes} (${if (anime.syncDiff > 0) "+${anime.syncDiff}" else "${anime.syncDiff}"} Güncellenecek)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.accentAmber,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMediaState(
    category: MediaTypeCategory,
    filter: AnimeFilter,
    onAdd: () -> Unit,
    onSyncMal: () -> Unit,
    accentColor: Color
) {
    val palette = LocalAppPalette.current
    val typeTitle = if (category == MediaTypeCategory.MANGA) "Manga & Manhwa" else "Anime"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (category == MediaTypeCategory.MANGA) Icons.Filled.Bookmark else Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = palette.textDarkMuted,
            modifier = Modifier.size(52.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = when (filter) {
                AnimeFilter.ALL -> "Listenizde henüz $typeTitle bulunmuyor"
                AnimeFilter.IN_PROGRESS -> "Şu anda devam eden $typeTitle yok"
                AnimeFilter.PLAN -> "Planlanan $typeTitle listeniz boş"
                AnimeFilter.COMPLETED -> "Tamamlanan $typeTitle bulunamadı"
                AnimeFilter.OTHER -> "Bu filtrede içerik bulunamadı"
            },
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                fontSize = 15.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "MyAnimeList profilinizdeki $typeTitle listesini eşitleyebilir veya canlı arama yapıp ekleyebilirsiniz.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = palette.textSecondary,
                fontSize = 12.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onSyncMal,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accentIndigo,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("MAL Senkronize Et", fontSize = 12.5.sp)
            }

            OutlinedButton(
                onClick = onAdd,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, accentColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Canlı Ara & Ekle", fontSize = 12.5.sp)
            }
        }
    }
}

@Composable
private fun MalSyncDialog(
    onDismiss: () -> Unit,
    onSyncSuccess: (Int, Int) -> Unit,
    accentColor: Color
) {
    val context = LocalContext.current
    val palette = LocalAppPalette.current
    val coroutineScope = rememberCoroutineScope()

    var username by remember {
        mutableStateOf(AnimeRepository.getLastMalUsername(context) ?: "")
    }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.panelNavy),
            border = BorderStroke(1.dp, palette.borderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = palette.accentIndigo.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Sync,
                                    contentDescription = null,
                                    tint = palette.accentIndigo,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "MyAnimeList Senkronizasyonu",
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                "Anime ve Manga listenizi eşitleyin",
                                color = palette.textSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Kapat",
                            tint = palette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MyAnimeList kullanıcı adınızı girin. Açık olan hem Anime hem de Manga & Manhwa listeleriniz kaydedilmiş bölümleri ve orijinal puanlarıyla birlikte cihazınıza aktarılır.",
                    color = palette.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("MAL Kullanıcı Adı", fontSize = 12.sp) },
                    placeholder = { Text("Örn: ozdilinanc13", fontSize = 12.sp) },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.panelNavyElevated,
                        unfocusedContainerColor = palette.panelNavyElevated,
                        focusedBorderColor = palette.accentIndigo,
                        unfocusedBorderColor = palette.borderSubtle,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                if (successMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = successMessage ?: "",
                        color = palette.accentEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (username.isBlank()) {
                            errorMessage = "Lütfen bir kullanıcı adı girin"
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        successMessage = null

                        coroutineScope.launch {
                            val result = AnimeRepository.syncWithMyAnimeList(context, username)
                            isLoading = false
                            if (result.isSuccess) {
                                val (animeCount, mangaCount) = result.getOrDefault(Pair(0, 0))
                                successMessage = "$animeCount Anime ve $mangaCount Manga başarıyla eşitlendi!"
                                onSyncSuccess(animeCount, mangaCount)
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Bağlantı hatası oluştu"
                            }
                        }
                    },
                    enabled = !isLoading && username.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentIndigo,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Listeler Eşitleniyor...", fontSize = 13.sp)
                    } else {
                        Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Şimdi Eşitle", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaSearchDialog(
    initialCategory: MediaTypeCategory,
    onDismiss: () -> Unit,
    onItemAdded: (AnimeItem) -> Unit,
    accentColor: Color
) {
    val context = LocalContext.current
    val palette = LocalAppPalette.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var selectedSearchType by remember { mutableStateOf(initialCategory) }
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AnimeSearchItem>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var addedIds by remember { mutableStateOf(setOf<Int>()) }

    fun executeSearch() {
        if (query.isBlank()) return
        focusManager.clearFocus()
        isSearching = true
        searchError = null

        coroutineScope.launch {
            val result = if (selectedSearchType == MediaTypeCategory.MANGA) {
                AnimeApiService.searchManga(query)
            } else {
                AnimeApiService.searchAnime(query)
            }
            isSearching = false
            if (result.isSuccess) {
                searchResults = result.getOrDefault(emptyList())
                if (searchResults.isEmpty()) {
                    searchError = "Aramanızla eşleşen içerik bulunamadı."
                }
            } else {
                searchError = result.exceptionOrNull()?.message ?: "Arama sırasında bir hata oluştu"
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.panelNavy),
            border = BorderStroke(1.dp, palette.borderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = accentColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Canlı Ara & Ekle",
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Kapat",
                            tint = palette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle: Anime vs Manga search
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isAnime = selectedSearchType == MediaTypeCategory.ANIME
                    FilterChip(
                        selected = isAnime,
                        onClick = {
                            selectedSearchType = MediaTypeCategory.ANIME
                            if (query.isNotBlank()) executeSearch()
                        },
                        label = { Text("🎬 Anime Ara", fontSize = 11.5.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor.copy(alpha = 0.2f),
                            selectedLabelColor = accentColor,
                            containerColor = palette.panelNavyElevated,
                            labelColor = palette.textSecondary
                        )
                    )

                    val isManga = selectedSearchType == MediaTypeCategory.MANGA
                    FilterChip(
                        selected = isManga,
                        onClick = {
                            selectedSearchType = MediaTypeCategory.MANGA
                            if (query.isNotBlank()) executeSearch()
                        },
                        label = { Text("📖 Manga Ara", fontSize = 11.5.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.accentPurple.copy(alpha = 0.2f),
                            selectedLabelColor = palette.accentPurple,
                            containerColor = palette.panelNavyElevated,
                            labelColor = palette.textSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            if (selectedSearchType == MediaTypeCategory.MANGA) "Örn: Berserk, Haikyuu, Solo Leveling..." else "Örn: Death Note, Attack on Titan...",
                            fontSize = 12.sp,
                            color = palette.textDarkMuted
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { executeSearch() }),
                    trailingIcon = {
                        IconButton(onClick = { executeSearch() }) {
                            Icon(Icons.Filled.Search, contentDescription = "Ara", tint = accentColor)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.panelNavyElevated,
                        unfocusedContainerColor = palette.panelNavyElevated,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = palette.borderSubtle,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (searchError != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = searchError ?: "",
                            color = palette.textSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Binlerce anime ve manga/manhwa serisini arayıp tek tıkla listenize ekleyebilirsiniz.",
                            color = palette.textDarkMuted,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(searchResults, key = { "${it.category.name}_${it.malId}" }) { item ->
                            val isAdded = addedIds.contains(item.malId)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = palette.panelNavyElevated),
                                border = BorderStroke(1.dp, palette.borderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thumbnail
                                    Box(
                                        modifier = Modifier
                                            .width(54.dp)
                                            .height(76.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(palette.panelNavy)
                                    ) {
                                        if (item.imageUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(item.imageUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = item.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                            fontSize = 13.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val epStr = if (item.totalEpisodes > 0) "${item.totalEpisodes} Bölüm" else "Devam Ediyor"
                                        val scoreStr = if (item.score > 0) "⭐ ${item.score}" else ""
                                        Text(
                                            text = listOf(item.mediaType, epStr, scoreStr).filter { it.isNotBlank() }.joinToString(" • "),
                                            color = palette.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            if (!isAdded) {
                                                val idPrefix = if (item.category == MediaTypeCategory.MANGA) "mal_manga" else "mal"
                                                val animeItem = AnimeItem(
                                                    id = "${idPrefix}_${item.malId}",
                                                    malId = item.malId,
                                                    title = item.title,
                                                    titleEnglish = item.titleEnglish,
                                                    imageUrl = item.imageUrl,
                                                    watchedEpisodes = 0,
                                                    totalEpisodes = item.totalEpisodes,
                                                    malWatchedEpisodes = 0,
                                                    score = item.score,
                                                    status = AnimeWatchStatus.PLAN_TO_WATCH,
                                                    mediaType = item.mediaType,
                                                    category = item.category,
                                                    genres = item.genres,
                                                    notes = "",
                                                    updatedAt = System.currentTimeMillis()
                                                )
                                                onItemAdded(animeItem)
                                                addedIds = addedIds + item.malId
                                            }
                                        },
                                        enabled = !isAdded,
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isAdded) palette.accentEmerald else accentColor,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        if (isAdded) {
                                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Eklendi", fontSize = 11.sp)
                                        } else {
                                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Ekle", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaDetailDialog(
    anime: AnimeItem,
    onDismiss: () -> Unit,
    onSave: (AnimeItem) -> Unit,
    onDelete: () -> Unit,
    accentColor: Color
) {
    val palette = LocalAppPalette.current

    var watchedText by remember { mutableStateOf("${anime.watchedEpisodes}") }
    var totalText by remember { mutableStateOf(if (anime.totalEpisodes > 0) "${anime.totalEpisodes}" else "") }
    var selectedStatus by remember { mutableStateOf(anime.status) }
    var notesText by remember { mutableStateOf(anime.notes) }

    val currentWatchedInt = watchedText.toIntOrNull() ?: anime.watchedEpisodes
    val isLocallyOutOfSync = anime.malId != null && currentWatchedInt != anime.malWatchedEpisodes

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = palette.panelNavy),
            border = BorderStroke(1.dp, palette.borderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = anime.displayTitle,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${anime.category.emoji} ${anime.category.label}",
                                color = palette.textSecondary,
                                fontSize = 11.5.sp
                            )
                            if (anime.score > 0) {
                                Text(
                                    text = " • ⭐ ${anime.score} (MAL Puanı)",
                                    color = palette.accentGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Kapat",
                            tint = palette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // MAL Sync Status Banner
                    item {
                        Surface(
                            color = if (isLocallyOutOfSync) palette.accentAmber.copy(alpha = 0.15f) else palette.panelNavyElevated,
                            border = BorderStroke(1.dp, if (isLocallyOutOfSync) palette.accentAmber.copy(alpha = 0.5f) else palette.borderSubtle),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "MAL'deki Son Bölüm: ${anime.malWatchedEpisodes}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textSecondary
                                    )
                                    Text(
                                        text = "Uygulama İlerlemesi: $currentWatchedInt",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLocallyOutOfSync) palette.accentAmber else palette.accentEmerald
                                    )
                                }
                                if (isLocallyOutOfSync) {
                                    val diff = currentWatchedInt - anime.malWatchedEpisodes
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚡ MyAnimeList'te henüz güncellenmedi (${if (diff > 0) "+$diff" else "$diff"} bölüm). MAL profilinize gidip güncelleyebilirsiniz.",
                                        fontSize = 11.sp,
                                        color = palette.accentAmber
                                    )
                                }
                            }
                        }
                    }

                    // Status Chips
                    item {
                        Text(
                            if (anime.category == MediaTypeCategory.MANGA) "Okuma Durumu" else "İzleme Durumu",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(AnimeWatchStatus.entries) { st ->
                                val isSelected = selectedStatus == st
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedStatus = st },
                                    label = { Text("${st.emoji} ${st.getLabel(anime.category)}", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentColor.copy(alpha = 0.25f),
                                        selectedLabelColor = accentColor,
                                        containerColor = palette.panelNavyElevated,
                                        labelColor = palette.textSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = palette.borderSubtle,
                                        selectedBorderColor = accentColor
                                    )
                                )
                            }
                        }
                    }

                    // Episodes / Chapters Row
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = watchedText,
                                onValueChange = { watchedText = it.filter { ch -> ch.isDigit() } },
                                label = { Text(if (anime.category == MediaTypeCategory.MANGA) "Okunan Bölüm" else "İzlenen Bölüm", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = palette.panelNavyElevated,
                                    unfocusedContainerColor = palette.panelNavyElevated,
                                    focusedBorderColor = accentColor,
                                    unfocusedBorderColor = palette.borderSubtle,
                                    focusedTextColor = palette.textPrimary,
                                    unfocusedTextColor = palette.textPrimary
                                )
                            )

                            OutlinedTextField(
                                value = totalText,
                                onValueChange = { totalText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Toplam Bölüm", fontSize = 11.sp) },
                                placeholder = { Text("0 = Devam Ediyor", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = palette.panelNavyElevated,
                                    unfocusedContainerColor = palette.panelNavyElevated,
                                    focusedBorderColor = accentColor,
                                    unfocusedBorderColor = palette.borderSubtle,
                                    focusedTextColor = palette.textPrimary,
                                    unfocusedTextColor = palette.textPrimary
                                )
                            )
                        }
                    }

                    // Personal Notes
                    item {
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            label = { Text("Kişisel Notlar", fontSize = 11.sp) },
                            placeholder = { Text("Örn: En sevdiğim arc, sezon 3 çıkacak...", fontSize = 11.sp) },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = palette.panelNavyElevated,
                                unfocusedContainerColor = palette.panelNavyElevated,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = palette.borderSubtle,
                                focusedTextColor = palette.textPrimary,
                                unfocusedTextColor = palette.textPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sil", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val watched = watchedText.toIntOrNull() ?: anime.watchedEpisodes
                            val total = totalText.toIntOrNull() ?: anime.totalEpisodes
                            val updated = anime.copy(
                                watchedEpisodes = watched,
                                totalEpisodes = total,
                                status = selectedStatus,
                                notes = notesText,
                                updatedAt = System.currentTimeMillis()
                            )
                            onSave(updated)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Kaydet", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
