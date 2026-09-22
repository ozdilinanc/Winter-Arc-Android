package com.example.ui.components.anime

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.data.model.anime.AnimeWatchStatus
import com.example.data.repository.AnimeRepository
import com.example.ui.theme.LocalAppPalette
import com.example.ui.util.rememberHapticEngine
import kotlinx.coroutines.launch

private enum class AnimeFilter(val label: String) {
    ALL("Tümü"),
    WATCHING("İzleniyor"),
    PLAN_TO_WATCH("İzlenecek"),
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
    val animeList by AnimeRepository.animeFlow.collectAsState()
    LaunchedEffect(Unit) {
        AnimeRepository.getAnimeList(context)
    }

    var currentFilter by remember { mutableStateOf(AnimeFilter.ALL) }
    var showMalSyncDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var selectedAnimeForEdit by remember { mutableStateOf<AnimeItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val stats = remember(animeList) { AnimeRepository.computeStats(animeList) }

    val filteredList = remember(animeList, currentFilter, searchQuery) {
        animeList.filter { anime ->
            val matchesFilter = when (currentFilter) {
                AnimeFilter.ALL -> true
                AnimeFilter.WATCHING -> anime.status == AnimeWatchStatus.WATCHING
                AnimeFilter.PLAN_TO_WATCH -> anime.status == AnimeWatchStatus.PLAN_TO_WATCH
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
            AnimeTopBar(
                onBack = onBack,
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
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Row
            item {
                AnimeStatsHeader(stats = stats, accentColor = effectiveAccent)
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
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(AnimeFilter.entries) { filter ->
                            val isSelected = currentFilter == filter
                            val count = when (filter) {
                                AnimeFilter.ALL -> animeList.size
                                AnimeFilter.WATCHING -> stats.watchingCount
                                AnimeFilter.PLAN_TO_WATCH -> stats.planToWatchCount
                                AnimeFilter.COMPLETED -> stats.completedCount
                                AnimeFilter.OTHER -> animeList.count { it.status == AnimeWatchStatus.ON_HOLD || it.status == AnimeWatchStatus.DROPPED }
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    hapticEngine.vibrateSelection()
                                    currentFilter = filter
                                },
                                label = {
                                    Text(
                                        text = "${filter.label} ($count)",
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

            // Anime Cards
            if (filteredList.isEmpty()) {
                item {
                    EmptyAnimeState(
                        filter = currentFilter,
                        onAddAnime = { showSearchDialog = true },
                        onSyncMal = { showMalSyncDialog = true },
                        accentColor = effectiveAccent
                    )
                }
            } else {
                items(filteredList, key = { it.id }) { anime ->
                    AnimeListItemCard(
                        anime = anime,
                        onIncrementEpisode = {
                            hapticEngine.triggerSuccessHaptic()
                            AnimeRepository.incrementEpisode(context, anime.id)
                        },
                        onDecrementEpisode = {
                            hapticEngine.triggerVirtualTick()
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
            onSyncSuccess = { count ->
                coroutineScope.launch {
                    showMalSyncDialog = false
                    hapticEngine.triggerSuccessHaptic()
                }
            },
            accentColor = effectiveAccent
        )
    }

    // Jikan Anime Search & Add Dialog
    if (showSearchDialog) {
        AnimeSearchDialog(
            onDismiss = { showSearchDialog = false },
            onAnimeAdded = { item ->
                hapticEngine.triggerSuccessHaptic()
                AnimeRepository.addOrUpdateAnime(context, item)
            },
            accentColor = effectiveAccent
        )
    }

    // Anime Detail / Edit Dialog
    selectedAnimeForEdit?.let { anime ->
        AnimeEditDialog(
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
private fun AnimeTopBar(
    onBack: () -> Unit,
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
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = palette.textPrimary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Anime & Manhwa Hub",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "MyAnimeList Entegrasyonu & Bölüm Takibi",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = palette.textSecondary,
                        fontSize = 11.5.sp
                    )
                )
            }

            // MAL Sync Icon Button
            IconButton(
                onClick = onOpenMalSync,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.accentIndigo.copy(alpha = 0.15f))
            ) {
                Icon(
                    Icons.Outlined.Sync,
                    contentDescription = "MAL Eşitle",
                    tint = palette.accentIndigo,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Add Anime Button
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Anime Ekle",
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimeStatsHeader(
    stats: com.example.data.model.anime.AnimeUserStats,
    accentColor: Color
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Total Anime
        AnimeStatCard(
            title = "Toplam Seri",
            value = "${stats.totalAnime}",
            subLabel = "${stats.watchingCount} İzleniyor",
            accentColor = accentColor,
            modifier = Modifier.weight(1f)
        )

        // Watched Episodes
        AnimeStatCard(
            title = "İzlenen Bölüm",
            value = "${stats.totalWatchedEpisodes}",
            subLabel = "Kayıtlı Bölüm",
            accentColor = palette.accentEmerald,
            modifier = Modifier.weight(1f)
        )

        // Completed
        AnimeStatCard(
            title = "Tamamlanan",
            value = "${stats.completedCount}",
            subLabel = "Bitirilen Seri",
            accentColor = palette.accentGold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AnimeStatCard(
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
                .padding(horizontal = 10.dp, vertical = 10.dp),
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
private fun AnimeListItemCard(
    anime: AnimeItem,
    onIncrementEpisode: () -> Unit,
    onDecrementEpisode: () -> Unit,
    onClickEdit: () -> Unit,
    accentColor: Color
) {
    val context = LocalContext.current
    val palette = LocalAppPalette.current

    val animatedProgress by animateFloatAsState(
        targetValue = anime.progressFraction,
        label = "anime_progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = palette.panelNavy),
        border = BorderStroke(1.dp, if (anime.isCompleted) palette.accentGold.copy(alpha = 0.4f) else palette.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Anime Poster Image
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

                // Score Badge
                if (anime.score > 0) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
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
                                text = anime.status.label,
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
                            text = anime.genres.take(3).joinToString(" • "),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Info & Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalStr = if (anime.totalEpisodes > 0) "${anime.totalEpisodes}" else "?"
                        Text(
                            text = "${anime.watchedEpisodes} / $totalStr Bölüm",
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

                // Quick Episode Increment / Decrement & Edit Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Quick Decrement
                    IconButton(
                        onClick = onDecrementEpisode,
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

                    // +1 Bölüm Button
                    Button(
                        onClick = onIncrementEpisode,
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
                            text = if (anime.isCompleted) "Tekrar İzle" else "+1 Bölüm",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Edit Options Button
                    IconButton(
                        onClick = onClickEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.panelNavyElevated)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Düzenle",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyAnimeState(
    filter: AnimeFilter,
    onAddAnime: () -> Unit,
    onSyncMal: () -> Unit,
    accentColor: Color
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = palette.textDarkMuted,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = when (filter) {
                AnimeFilter.ALL -> "Henüz anime listeniz boş"
                AnimeFilter.WATCHING -> "Şu anda izlenen anime bulunamadı"
                AnimeFilter.PLAN_TO_WATCH -> "İzlenecekler listeniz boş"
                AnimeFilter.COMPLETED -> "Tamamlanan anime bulunamadı"
                AnimeFilter.OTHER -> "Bu filtrede anime bulunamadı"
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
            text = "MyAnimeList profilinizi senkronize edebilir veya yeni anime arayıp ekleyebilirsiniz.",
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
                onClick = onAddAnime,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, accentColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Anime Ara & Ekle", fontSize = 12.5.sp)
            }
        }
    }
}

@Composable
private fun MalSyncDialog(
    onDismiss: () -> Unit,
    onSyncSuccess: (Int) -> Unit,
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
                                "Tek tıkla tüm listenizi aktarın",
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
                    text = "MyAnimeList kullanıcı adınızı girin. Şifre veya yetkilendirme gerekmez; herkese açık anime listeniz bölümleri ve afişleriyle birlikte aktarılır.",
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
                    placeholder = { Text("Örn: Xinil, inancozdil", fontSize = 12.sp) },
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
                                val count = result.getOrDefault(0)
                                successMessage = "$count anime başarıyla eşitlendi!"
                                onSyncSuccess(count)
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
                        Text("Listeler İndiriliyor...", fontSize = 13.sp)
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
private fun AnimeSearchDialog(
    onDismiss: () -> Unit,
    onAnimeAdded: (AnimeItem) -> Unit,
    accentColor: Color
) {
    val context = LocalContext.current
    val palette = LocalAppPalette.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

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
            val result = AnimeApiService.searchAnime(query)
            isSearching = false
            if (result.isSuccess) {
                searchResults = result.getOrDefault(emptyList())
                if (searchResults.isEmpty()) {
                    searchError = "Aramanızla eşleşen anime bulunamadı."
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
                            text = "Anime / Manhwa Ara & Ekle",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Search Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Örn: Solo Leveling, Death Note, Naruto...", fontSize = 12.sp, color = palette.textDarkMuted) },
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
                            text = "Jikan API üzerinden binlerce anime arasında arama yapın ve listenize ekleyin.",
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
                        items(searchResults, key = { it.malId }) { item ->
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
                                                val animeItem = AnimeItem(
                                                    id = "mal_${item.malId}",
                                                    malId = item.malId,
                                                    title = item.title,
                                                    titleEnglish = item.titleEnglish,
                                                    imageUrl = item.imageUrl,
                                                    watchedEpisodes = 0,
                                                    totalEpisodes = item.totalEpisodes,
                                                    score = item.score,
                                                    status = AnimeWatchStatus.PLAN_TO_WATCH,
                                                    mediaType = item.mediaType,
                                                    genres = item.genres,
                                                    notes = "",
                                                    updatedAt = System.currentTimeMillis()
                                                )
                                                onAnimeAdded(animeItem)
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
private fun AnimeEditDialog(
    anime: AnimeItem,
    onDismiss: () -> Unit,
    onSave: (AnimeItem) -> Unit,
    onDelete: () -> Unit,
    accentColor: Color
) {
    val palette = LocalAppPalette.current

    var watchedText by remember { mutableStateOf("${anime.watchedEpisodes}") }
    var totalText by remember { mutableStateOf(if (anime.totalEpisodes > 0) "${anime.totalEpisodes}" else "") }
    var scoreText by remember { mutableStateOf(if (anime.score > 0) "${anime.score}" else "") }
    var selectedStatus by remember { mutableStateOf(anime.status) }
    var notesText by remember { mutableStateOf(anime.notes) }

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
                        Text(
                            text = "Anime Durumunu Düzenle",
                            color = palette.textSecondary,
                            fontSize = 11.5.sp
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

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Chips
                    item {
                        Text("İzleme Durumu", fontSize = 12.sp, color = palette.textSecondary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(AnimeWatchStatus.entries) { st ->
                                val isSelected = selectedStatus == st
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedStatus = st },
                                    label = { Text("${st.emoji} ${st.label}", fontSize = 11.sp) },
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

                    // Episodes Row
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = watchedText,
                                onValueChange = { watchedText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("İzlenen Bölüm", fontSize = 11.sp) },
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
                                placeholder = { Text("0 = Bilinmiyor", fontSize = 11.sp) },
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

                    // Score
                    item {
                        OutlinedTextField(
                            value = scoreText,
                            onValueChange = { scoreText = it },
                            label = { Text("Puan (0.0 - 10.0)", fontSize = 11.sp) },
                            placeholder = { Text("Örn: 9.0", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
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

                    // Notes
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
                            val score = scoreText.toFloatOrNull() ?: anime.score
                            val updated = anime.copy(
                                watchedEpisodes = watched,
                                totalEpisodes = total,
                                score = score.coerceIn(0f, 10f),
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
