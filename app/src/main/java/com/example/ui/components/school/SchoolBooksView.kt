package com.example.ui.components.school

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.util.rememberHapticEngine

@Composable
fun SchoolBooksView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var books by remember { mutableStateOf(defaultTrackedBooks()) }
    var selectedBookId by remember { mutableStateOf<String?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<BookReadingStatus?>(null) }
    var editingBookId by remember { mutableStateOf<String?>(null) }
    var pageInputText by remember { mutableStateOf("") }

    val hapticEngine = rememberHapticEngine()

    // Handle back button: if inside deep study detail, return to book list; else return to SchoolHub
    BackHandler {
        if (selectedBookId != null) {
            selectedBookId = null
        } else {
            onBack()
        }
    }

    // If a book is selected for deep study, show the deep study detail view
    val activeBook = books.find { it.id == selectedBookId }
    if (activeBook != null) {
        BookStudyDetailView(
            book = activeBook,
            onBack = { selectedBookId = null },
            onToggleChapter = { sectionId, chapterId ->
                hapticEngine.vibrateSkillCompleted()
                books = books.map { b ->
                    if (b.id == activeBook.id) {
                        val updatedSections = b.sections.map { sec ->
                            if (sec.id == sectionId) {
                                val updatedChapters = sec.chapters.map { ch ->
                                    if (ch.id == chapterId) ch.copy(isCompleted = !ch.isCompleted)
                                    else ch
                                }
                                sec.copy(chapters = updatedChapters)
                            } else sec
                        }
                        // If all chapters completed, auto update status to COMPLETED
                        val allCompleted = updatedSections.all { sec -> sec.chapters.all { it.isCompleted } }
                        val newStatus = if (allCompleted && b.status != BookReadingStatus.COMPLETED) {
                            BookReadingStatus.COMPLETED
                        } else b.status
                        b.copy(sections = updatedSections, status = newStatus)
                    } else b
                }
            },
            onUpdateStatus = { newStatus ->
                books = books.map { b ->
                    if (b.id == activeBook.id) b.copy(status = newStatus) else b
                }
            },
            onUpdateNotes = { newNotes ->
                books = books.map { b ->
                    if (b.id == activeBook.id) b.copy(personalNotes = newNotes) else b
                }
            },
            onOpenPageDialog = {
                editingBookId = activeBook.id
                pageInputText = activeBook.currentPage.toString()
            },
            onQuickPageChange = { delta ->
                books = books.map { b ->
                    if (b.id == activeBook.id) {
                        val updatedPage = (b.currentPage + delta).coerceIn(0, b.totalPages)
                        b.copy(currentPage = updatedPage)
                    } else b
                }
            },
            modifier = modifier
        )
    } else {
        // Main Books Catalog View
        val filteredBooks = remember(books, selectedStatusFilter) {
            if (selectedStatusFilter == null) books
            else books.filter { it.status == selectedStatusFilter }
        }

        // Global statistics
        val totalBooksCount = books.size
        val totalPagesAll = books.sumOf { it.totalPages }
        val totalReadPagesAll = books.sumOf { it.currentPage }
        val totalChaptersAll = books.sumOf { it.totalChaptersCount }
        val totalCompletedChaptersAll = books.sumOf { it.completedChaptersCount }
        val overallProgress = if (totalChaptersAll > 0) totalCompletedChaptersAll.toFloat() / totalChaptersAll.toFloat() else 0f

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(CanvasDark)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Back Button Row
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
                        text = "OKUL HUB'A DÖN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            letterSpacing = 1.2.sp
                        )
                    )
                }
            }

            // Header Title
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📚", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CS BAŞUCU KİTAPLARI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "4 temel mühendislik kitabında sayfa, bölüm ve konu bazlı derin okuma takibi.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Global Reading Statistics Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        PanelNavyElevated,
                                        PanelNavy.copy(alpha = 0.95f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GENEL OKUMA İLERLEMESİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 1.2.sp
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PanelNavyHighlight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = "%${(overallProgress * 100).toInt()} Tamamlandı",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentEmerald,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Grid Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                title = "Kitaplar",
                                value = "$totalBooksCount Eser",
                                emoji = "📖"
                            )
                            StatItem(
                                title = "Okunan Sayfa",
                                value = "$totalReadPagesAll / $totalPagesAll",
                                emoji = "📄"
                            )
                            StatItem(
                                title = "Biten Bölüm",
                                value = "$totalCompletedChaptersAll / $totalChaptersAll",
                                emoji = "✅"
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Global Progress Bar
                        LinearProgressIndicator(
                            progress = { overallProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(CircleShape),
                            color = AccentEmerald,
                            trackColor = PanelNavy
                        )
                    }
                }
            }

            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChipButton(
                        label = "Tümü (${books.size})",
                        isSelected = selectedStatusFilter == null,
                        onClick = { selectedStatusFilter = null },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChipButton(
                        label = "Okunuyor",
                        isSelected = selectedStatusFilter == BookReadingStatus.READING,
                        onClick = { selectedStatusFilter = BookReadingStatus.READING },
                        modifier = Modifier.weight(1.1f)
                    )
                    FilterChipButton(
                        label = "Bitti",
                        isSelected = selectedStatusFilter == BookReadingStatus.COMPLETED,
                        onClick = { selectedStatusFilter = BookReadingStatus.COMPLETED },
                        modifier = Modifier.weight(0.9f)
                    )
                    FilterChipButton(
                        label = "Başlanmadı",
                        isSelected = selectedStatusFilter == BookReadingStatus.NOT_STARTED,
                        onClick = { selectedStatusFilter = BookReadingStatus.NOT_STARTED },
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }

            // Book Cards
            items(filteredBooks, key = { it.id }) { book ->
                BookCatalogCard(
                    book = book,
                    onOpenDetail = { selectedBookId = book.id },
                    onStatusClick = {
                        // Cycle status: NOT_STARTED -> READING -> COMPLETED -> NOT_STARTED
                        val nextStatus = when (book.status) {
                            BookReadingStatus.NOT_STARTED -> BookReadingStatus.READING
                            BookReadingStatus.READING -> BookReadingStatus.COMPLETED
                            BookReadingStatus.COMPLETED -> BookReadingStatus.NOT_STARTED
                        }
                        books = books.map { if (it.id == book.id) it.copy(status = nextStatus) else it }
                    },
                    onOpenPageDialog = {
                        editingBookId = book.id
                        pageInputText = book.currentPage.toString()
                    },
                    onQuickPageChange = { delta ->
                        books = books.map { b ->
                            if (b.id == book.id) {
                                val updated = (b.currentPage + delta).coerceIn(0, b.totalPages)
                                b.copy(currentPage = updated)
                            } else b
                        }
                    }
                )
            }
        }
    }

    // Direct Page Edit Dialog
    editingBookId?.let { targetId ->
        val book = books.find { it.id == targetId }
        if (book != null) {
            AlertDialog(
                onDismissRequest = { editingBookId = null },
                containerColor = PanelNavyElevated,
                title = {
                    Text(
                        text = "${book.shortTitle} - Sayfa Güncelle",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Şu anda kaçıncı sayfadasın? (Maksimum: ${book.totalPages})",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = pageInputText,
                            onValueChange = { pageInputText = it.filter { char -> char.isDigit() } },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newPage = pageInputText.toIntOrNull() ?: book.currentPage
                            books = books.map {
                                if (it.id == targetId) it.copy(currentPage = newPage.coerceIn(0, it.totalPages))
                                else it
                            }
                            editingBookId = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                    ) {
                        Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingBookId = null }) {
                        Text("İptal", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    emoji: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 13.5.sp
                )
            )
        }
    }
}

@Composable
private fun FilterChipButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AccentCyan else PanelNavyElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AccentCyan else BorderSubtle
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun BookCatalogCard(
    book: TrackedBook,
    onOpenDetail: () -> Unit,
    onStatusClick: () -> Unit,
    onOpenPageDialog: () -> Unit,
    onQuickPageChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Emoji, Title, Authors, Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentCyan.copy(alpha = 0.15f))
                        .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = book.coverEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = book.shortTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        // Status Chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (book.status) {
                                BookReadingStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.15f)
                                BookReadingStatus.READING -> AccentCyan.copy(alpha = 0.15f)
                                BookReadingStatus.NOT_STARTED -> PanelNavy
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (book.status) {
                                    BookReadingStatus.COMPLETED -> StatusCompleted
                                    BookReadingStatus.READING -> AccentCyan
                                    BookReadingStatus.NOT_STARTED -> BorderSubtle
                                }
                            ),
                            modifier = Modifier.clickable { onStatusClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = book.status.emoji, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = book.status.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (book.status) {
                                            BookReadingStatus.COMPLETED -> StatusCompleted
                                            BookReadingStatus.READING -> AccentCyan
                                            BookReadingStatus.NOT_STARTED -> TextMuted
                                        },
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = book.authors,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = book.authorOrDomain,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentCyan,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Why it matters box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PanelNavy,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f))
            ) {
                Text(
                    text = book.whyItMatters,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Metrics & Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bölüm İlerlemesi: ${book.completedChaptersCount}/${book.totalChaptersCount}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 11.5.sp
                    )
                )

                Text(
                    text = "%${(book.chapterProgressPercent * 100).toInt()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (book.chapterProgressPercent >= 1f) StatusCompleted else AccentCyan,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { book.chapterProgressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (book.chapterProgressPercent >= 1f) StatusCompleted else AccentCyan,
                trackColor = PanelNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Page Counter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PanelNavyHighlight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { onOpenPageDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sayfa: ${book.currentPage} / ${book.totalPages}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Quick Stepper Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalIconButton(
                        onClick = { onQuickPageChange(-10) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = PanelNavy,
                            contentColor = TextSecondary
                        )
                    ) {
                        Text(text = "-10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalIconButton(
                        onClick = { onQuickPageChange(10) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = PanelNavy,
                            contentColor = AccentCyan
                        )
                    ) {
                        Text(text = "+10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledIconButton(
                        onClick = { onQuickPageChange(1) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AccentCyan,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "+1 Sayfa",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Button: Open Chapter Checklist
            Button(
                onClick = onOpenDetail,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PanelNavyHighlight),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Bölümler",
                            tint = AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bölüm & Konu Listesini Aç",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${book.completedChaptersCount}/${book.totalChaptersCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookStudyDetailView(
    book: TrackedBook,
    onBack: () -> Unit,
    onToggleChapter: (sectionId: String, chapterId: String) -> Unit,
    onUpdateStatus: (BookReadingStatus) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onOpenPageDialog: () -> Unit,
    onQuickPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var notesText by remember(book.id) { mutableStateOf(book.personalNotes) }
    var isNotesSaved by remember { mutableStateOf(false) }

    // Set of collapsed section IDs (initially none are collapsed)
    var collapsedSectionIds by remember { mutableStateOf(setOf<String>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back Bar
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
                    text = "KİTAPLAR LİSTESİNE DÖN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }

        // Book Detail Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    PanelNavyElevated,
                                    PanelNavy.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = book.coverEmoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = book.authors,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = book.authorOrDomain,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reading Status Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BookReadingStatus.values().forEach { st ->
                            val isSelected = book.status == st
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) {
                                    when (st) {
                                        BookReadingStatus.COMPLETED -> StatusCompleted
                                        BookReadingStatus.READING -> AccentCyan
                                        BookReadingStatus.NOT_STARTED -> PanelNavyHighlight
                                    }
                                } else PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color.Transparent else BorderSubtle
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateStatus(st) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = st.emoji, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = st.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Overview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Konu Tamamlama: ${book.completedChaptersCount}/${book.totalChaptersCount} Bölüm",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "%${(book.chapterProgressPercent * 100).toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (book.chapterProgressPercent >= 1f) StatusCompleted else AccentCyan,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { book.chapterProgressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape),
                        color = if (book.chapterProgressPercent >= 1f) StatusCompleted else AccentCyan,
                        trackColor = PanelNavy
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Page controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelNavyHighlight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { onOpenPageDialog() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Sayfa: ${book.currentPage} / ${book.totalPages}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 11.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Düzenle",
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalIconButton(
                                onClick = { onQuickPageChange(-10) },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = PanelNavy,
                                    contentColor = TextSecondary
                                )
                            ) {
                                Text(text = "-10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalIconButton(
                                onClick = { onQuickPageChange(10) },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = PanelNavy,
                                    contentColor = AccentCyan
                                )
                            ) {
                                Text(text = "+10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledIconButton(
                                onClick = { onQuickPageChange(1) },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = AccentCyan,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "+1 Sayfa",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Chapters Header
        item {
            Text(
                text = "KİTAP İÇERİĞİ & KONTROL LİSTESİ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Render each section and its chapters
        items(book.sections, key = { it.id }) { section ->
            val isCollapsed = collapsedSectionIds.contains(section.id)
            val completedInSection = section.chapters.count { it.isCompleted }
            val totalInSection = section.chapters.size
            val sectionProgress = if (totalInSection > 0) completedInSection.toFloat() / totalInSection.toFloat() else 0f

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Section Accordion Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                collapsedSectionIds = if (isCollapsed) {
                                    collapsedSectionIds - section.id
                                } else {
                                    collapsedSectionIds + section.id
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = section.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = "$completedInSection / $totalInSection",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (completedInSection == totalInSection && totalInSection > 0) StatusCompleted else AccentCyan,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                contentDescription = if (isCollapsed) "Genişlet" else "Daralt",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Section Progress Indicator
                    LinearProgressIndicator(
                        progress = { sectionProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = if (sectionProgress >= 1f) StatusCompleted else AccentCyan,
                        trackColor = PanelNavy
                    )

                    // Section Chapters List
                    AnimatedVisibility(visible = !isCollapsed) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            section.chapters.forEach { chapter ->
                                ChapterItemRow(
                                    chapter = chapter,
                                    onToggle = { onToggleChapter(section.id, chapter.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personal Notes & Takeaways Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Notlar",
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KİŞİSEL NOTLAR & ÇIKARIMLAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Button(
                            onClick = {
                                onUpdateNotes(notesText)
                                isNotesSaved = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isNotesSaved) "Kaydedildi ✓" else "Kaydet",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            isNotesSaved = false
                        },
                        placeholder = {
                            Text(
                                text = "Bu kitaptan öğrendiğin en can alıcı noktaları, mimari prensipleri veya hatırlanacak sayfaları buraya not al...",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterItemRow(
    chapter: BookChapterItem,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (chapter.isCompleted) PanelNavy.copy(alpha = 0.6f) else PanelNavyHighlight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (chapter.isCompleted) StatusCompleted.copy(alpha = 0.3f) else BorderSubtle.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox Icon
            Icon(
                imageVector = if (chapter.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (chapter.isCompleted) "Tamamlandı" else "Tamamlanmadı",
                tint = if (chapter.isCompleted) StatusCompleted else TextMuted,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chapter.chapterNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (chapter.isCompleted) FontWeight.Medium else FontWeight.SemiBold,
                            color = if (chapter.isCompleted) TextSecondary else TextPrimary,
                            fontSize = 12.sp
                        )
                    )
                }

                if (chapter.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = chapter.description,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                    )
                }
            }
        }
    }
}
