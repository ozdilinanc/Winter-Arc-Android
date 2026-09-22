package com.example.ui.components.school

import android.content.Context
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
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SchoolBooksRepository
import com.example.ui.theme.*
import com.example.ui.util.rememberHapticEngine

enum class BookListFilter(val label: String) {
    ALL("Tümü"),
    TO_READ("Okunacak"),
    READING("Okunuyor"),
    FINISHED("Bitti")
}

@Composable
fun SchoolBooksView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var books by remember { mutableStateOf(SchoolBooksRepository.getBooks(context)) }
    var selectedBookId by remember { mutableStateOf<String?>(null) }
    var currentFilter by remember { mutableStateOf(BookListFilter.ALL) }
    var editingBookId by remember { mutableStateOf<String?>(null) }
    var pageInputText by remember { mutableStateOf("") }

    val hapticEngine = rememberHapticEngine()

    fun persistBooks(updated: List<TrackedBook>) {
        books = updated
        SchoolBooksRepository.saveBooks(context, updated)
    }

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
                val updated = books.map { b ->
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
                        val allCompleted = updatedSections.all { sec -> sec.chapters.all { it.isCompleted } }
                        val newStatus = if (allCompleted && b.status != BookReadingStatus.COMPLETED) {
                            BookReadingStatus.COMPLETED
                        } else if (!allCompleted && b.status == BookReadingStatus.NOT_STARTED) {
                            BookReadingStatus.READING
                        } else b.status
                        b.copy(sections = updatedSections, status = newStatus)
                    } else b
                }
                persistBooks(updated)
            },
            onUpdateStatus = { newStatus ->
                hapticEngine.vibrateSkillCompleted()
                val updated = books.map { b ->
                    if (b.id == activeBook.id) b.copy(status = newStatus) else b
                }
                persistBooks(updated)
            },
            onUpdateNotes = { newNotes ->
                val updated = books.map { b ->
                    if (b.id == activeBook.id) b.copy(personalNotes = newNotes) else b
                }
                persistBooks(updated)
            },
            onOpenPageDialog = {
                editingBookId = activeBook.id
                pageInputText = activeBook.currentPage.toString()
            },
            onQuickPageChange = { delta ->
                val updated = books.map { b ->
                    if (b.id == activeBook.id) {
                        val updatedPage = (b.currentPage + delta).coerceIn(0, b.totalPages)
                        val newStatus = if (updatedPage > 0 && b.status == BookReadingStatus.NOT_STARTED) {
                            BookReadingStatus.READING
                        } else if (updatedPage >= b.totalPages) {
                            BookReadingStatus.COMPLETED
                        } else b.status
                        b.copy(currentPage = updatedPage, status = newStatus)
                    } else b
                }
                persistBooks(updated)
            },
            modifier = modifier
        )
    } else {
        // Main Books Catalog View (Clean iOS Style)
        val filteredBooks = remember(books, currentFilter) {
            when (currentFilter) {
                BookListFilter.ALL -> books
                BookListFilter.TO_READ -> books.filter { it.status == BookReadingStatus.NOT_STARTED }
                BookListFilter.READING -> books.filter { it.status == BookReadingStatus.READING }
                BookListFilter.FINISHED -> books.filter { it.status == BookReadingStatus.COMPLETED }
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
            // Top Navigation Bar (Back + Title)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OKUL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Total Count Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PanelNavyElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = "${books.size} Eser",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Minimal Title Header
            item {
                Column {
                    Text(
                        text = "CS Kitaplığı",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Temel bilgisayar mühendisliği başucu kitapları ve okuma takibi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Top Segmented Pill Filter Bar (All | To Read | Reading | Finished)
            item {
                BookFilterSegmentedControl(
                    selectedFilter = currentFilter,
                    onFilterSelected = {
                        hapticEngine.vibrateSkillCompleted()
                        currentFilter = it
                    }
                )
            }

            // Book Catalog List or Empty State
            if (filteredBooks.isEmpty()) {
                item {
                    EmptyBooksFilterState(filter = currentFilter)
                }
            } else {
                items(filteredBooks, key = { it.id }) { book ->
                    CleanBookListItem(
                        book = book,
                        onClick = { selectedBookId = book.id }
                    )
                }
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
                            val updatedPage = newPage.coerceIn(0, book.totalPages)
                            val newStatus = if (updatedPage >= book.totalPages) {
                                BookReadingStatus.COMPLETED
                            } else if (updatedPage > 0 && book.status == BookReadingStatus.NOT_STARTED) {
                                BookReadingStatus.READING
                            } else book.status

                            val updated = books.map {
                                if (it.id == targetId) it.copy(currentPage = updatedPage, status = newStatus)
                                else it
                            }
                            persistBooks(updated)
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
private fun BookFilterSegmentedControl(
    selectedFilter: BookListFilter,
    onFilterSelected: (BookListFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = PanelNavyElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BookListFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PanelNavyHighlight else Color.Transparent)
                        .then(
                            if (isSelected) Modifier.border(
                                1.dp,
                                BorderActive.copy(alpha = 0.35f),
                                RoundedCornerShape(8.dp)
                            )
                            else Modifier
                        )
                        .clickable { onFilterSelected(filter) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanBookListItem(
    book: TrackedBook,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Realistic Physical Book Cover Thumbnail
            BookCoverThumbnail(
                bookId = book.id,
                shortTitle = book.shortTitle,
                authors = book.authors
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Right Info Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Header Row: Title + Chevron
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.shortTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Detay",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Authors
                Text(
                    text = book.authors,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Metadata Row: Category Tag, Status Tag, and Progress Percentage
                val category = getBookCategory(book.id)
                val percent = (book.progressPercent * 100).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category Chip
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏷️", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Status Chip
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (book.status) {
                                    BookReadingStatus.COMPLETED -> "✓"
                                    BookReadingStatus.READING -> "📖"
                                    BookReadingStatus.NOT_STARTED -> "⏳"
                                },
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = when (book.status) {
                                    BookReadingStatus.COMPLETED -> "Bitti"
                                    BookReadingStatus.READING -> "Okunuyor"
                                    BookReadingStatus.NOT_STARTED -> "Okunacak"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = when (book.status) {
                                        BookReadingStatus.COMPLETED -> StatusCompleted
                                        BookReadingStatus.READING -> AccentCyan
                                        BookReadingStatus.NOT_STARTED -> TextMuted
                                    },
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Percentage on far right
                    Text(
                        text = "%$percent",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Thin Elegant Progress Bar
                LinearProgressIndicator(
                    progress = { book.progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .clip(CircleShape),
                    color = when (book.status) {
                        BookReadingStatus.COMPLETED -> StatusCompleted
                        BookReadingStatus.READING -> AccentCyan
                        BookReadingStatus.NOT_STARTED -> BorderSubtle
                    },
                    trackColor = PanelNavy
                )
            }
        }
    }
}

@Composable
fun BookCoverThumbnail(
    bookId: String,
    shortTitle: String,
    authors: String,
    modifier: Modifier = Modifier
) {
    val (gradientColors, accentColor, titleOnCover, subTitleOnCover, authorOnCover, coverIcon) = when (bookId) {
        "book_ostep" -> CoverConfig(
            gradient = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
            accent = Color(0xFF38BDF8),
            title = "OSTEP",
            subTitle = "THREE EASY PIECES",
            author = "Arpaci-Dusseau",
            icon = Icons.Outlined.Terminal
        )

        "book_csapp" -> CoverConfig(
            gradient = listOf(Color(0xFF1E1B4B), Color(0xFF2E1065)),
            accent = Color(0xFFFBBF24),
            title = "CS:APP",
            subTitle = "SYSTEMS & ARCH",
            author = "Bryant & O'Hallaron",
            icon = Icons.Outlined.Code
        )

        "book_ddia" -> CoverConfig(
            gradient = listOf(Color(0xFF064E3B), Color(0xFF022C22)),
            accent = Color(0xFF34D399),
            title = "DDIA",
            subTitle = "DATA-INTENSIVE",
            author = "Martin Kleppmann",
            icon = Icons.Outlined.Storage
        )

        "book_networks" -> CoverConfig(
            gradient = listOf(Color(0xFF0C2444), Color(0xFF1E3A8A)),
            accent = Color(0xFF60A5FA),
            title = "NETWORKS",
            subTitle = "TOP-DOWN APPROACH",
            author = "Kurose & Ross",
            icon = Icons.Outlined.Hub
        )

        else -> CoverConfig(
            gradient = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
            accent = Color(0xFF38BDF8),
            title = shortTitle,
            subTitle = "CS CLASSIC",
            author = authors.take(15),
            icon = Icons.Outlined.AutoStories
        )
    }

    Box(
        modifier = modifier
            .width(66.dp)
            .height(96.dp)
            .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 7.dp, bottomEnd = 7.dp))
            .background(Brush.verticalGradient(gradientColors))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 7.dp, bottomEnd = 7.dp)
            )
    ) {
        // Spine Shadow effect on left edge (creating realistic physical 3D book curve)
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.25f)
                        )
                    )
                )
        )

        // Cover Typography and Graphics
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 5.dp, top = 6.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = titleOnCover,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    ),
                    maxLines = 1
                )
                Text(
                    text = subTitleOnCover,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor.copy(alpha = 0.9f),
                        fontSize = 6.5.sp,
                        letterSpacing = 0.3.sp
                    ),
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = coverIcon,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = authorOnCover,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 6.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class CoverConfig(
    val gradient: List<Color>,
    val accent: Color,
    val title: String,
    val subTitle: String,
    val author: String,
    val icon: ImageVector
)

@Composable
private fun EmptyBooksFilterState(filter: BookListFilter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.AutoStories,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "\"${filter.label}\" durumunda kitap bulunmuyor",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    fontSize = 13.sp
                )
            )
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
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "KİTAPLAR LİSTESİ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Book Detail Hero Banner with Cover
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        BookCoverThumbnail(
                            bookId = book.id,
                            shortTitle = book.shortTitle,
                            authors = book.authors
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = book.authors,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
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
                                    Icon(
                                        imageVector = getStatusIcon(st),
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (st) {
                                            BookReadingStatus.COMPLETED -> "Bitti"
                                            BookReadingStatus.READING -> "Okunuyor"
                                            BookReadingStatus.NOT_STARTED -> "Okunacak"
                                        },
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
                    val percent = (book.progressPercent * 100).toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bölüm Tamamlama: ${book.completedChaptersCount}/${book.totalChaptersCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "%$percent",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (book.progressPercent >= 1f) StatusCompleted else AccentCyan,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { book.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                        color = if (book.progressPercent >= 1f) StatusCompleted else AccentCyan,
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
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Render each section and its chapters
        items(book.sections, key = { it.id }) { section ->
            val isCollapsed = collapsedSectionIds.contains(section.id)
            val completedInSection = section.chapters.count { it.isCompleted }
            val totalInSection = section.chapters.size
            val sectionProgress =
                if (totalInSection > 0) completedInSection.toFloat() / totalInSection.toFloat() else 0f

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
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
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

        // Personal Notes Card
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
                                text = "Bu kitaptan öğrendiğin en can alıcı noktaları ve mimari prensipleri buraya not al...",
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

private fun getBookCategory(bookId: String): String {
    return when (bookId) {
        "book_ostep" -> "İşletim Sistemleri"
        "book_csapp" -> "Sistem Mimarisi"
        "book_ddia" -> "Dağıtık Sistemler"
        "book_networks" -> "Ağ Protokolleri"
        else -> "Mühendislik"
    }
}

private fun getStatusIcon(status: BookReadingStatus): ImageVector {
    return when (status) {
        BookReadingStatus.COMPLETED -> Icons.Outlined.CheckCircle
        BookReadingStatus.READING -> Icons.Outlined.AutoStories
        BookReadingStatus.NOT_STARTED -> Icons.Filled.RadioButtonUnchecked
    }
}
