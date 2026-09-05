package com.example.ui.components.school

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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SchoolBooksView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var books by remember {
        mutableStateOf(
            listOf(
                TrackedBook(
                    id = "book_ostep",
                    title = "Operating Systems: Three Easy Pieces (OSTEP)",
                    shortTitle = "OSTEP",
                    authorOrDomain = "İşletim Sistemleri (Virtualization, Concurrency, Persistence)",
                    whyItMatters = "CPU sanallaştırma, süreç çizelgeleme, kilitler ve dosya sistemleri için altın standart.",
                    totalPages = 720,
                    currentPage = 145,
                    keyTopics = listOf("Virtualization", "Process & Thread", "Paging & TLB", "Semaphores", "Crash Consistency"),
                    coverEmoji = "💻"
                ),
                TrackedBook(
                    id = "book_csapp",
                    title = "Computer Systems: A Programmer's Perspective (CS:APP)",
                    shortTitle = "CS:APP",
                    authorOrDomain = "Sistem & Donanım Katmanı",
                    whyItMatters = "Yüksek seviyeli kodu donanım ve makine mimarisine bağlayan başucu eseri.",
                    totalPages = 1100,
                    currentPage = 80,
                    keyTopics = listOf("Machine-Level Code", "Processor Architecture", "Memory Hierarchy", "Virtual Memory", "Linker"),
                    coverEmoji = "⚙️"
                ),
                TrackedBook(
                    id = "book_ddia",
                    title = "Designing Data-Intensive Applications (DDIA)",
                    shortTitle = "DDIA",
                    authorOrDomain = "Dağıtık Sistemler & Veri Depolama",
                    whyItMatters = "Modern yazılım mimarisi, depolama motorları, mutabakat ve replikasyonun kutsal kitabı.",
                    totalPages = 616,
                    currentPage = 210,
                    keyTopics = listOf("LSM-Tree vs B-Tree", "Transactions & Isolation", "Replication", "Partitioning", "Stream Processing"),
                    coverEmoji = "🗄️"
                ),
                TrackedBook(
                    id = "book_networks",
                    title = "Computer Networks: A Top-Down Approach",
                    shortTitle = "Computer Networks",
                    authorOrDomain = "Bilgisayar Ağları & Protokoller",
                    whyItMatters = "Uygulama katmanından paket seviyesine, soket programlamadan TLS el sıkışmasına ağ temeli.",
                    totalPages = 860,
                    currentPage = 60,
                    keyTopics = listOf("Socket Programming", "TCP Flow & Congestion", "IP Routing", "DNS & TLS", "Packet Switching"),
                    coverEmoji = "🌐"
                )
            )
        )
    }

    var editingBookId by remember { mutableStateOf<String?>(null) }
    var pageInputText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
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
                    text = "OKUL HUB'A DÖN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }

        // Title Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📖", fontSize = 22.sp)
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
                    text = "4 temel mühendislik kitabındaki okuma ve sayfa ilerlemeni takip et.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }

        // Book Cards
        items(books, key = { it.id }) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = book.coverEmoji, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.shortTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = book.authorOrDomain,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentCyan,
                                    fontSize = 10.5.sp
                                )
                            )
                        }

                        // Percentage Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PanelNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "%${(book.progressPercent * 100).toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (book.progressPercent > 0.5f) StatusCompleted else AccentAmber,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = book.whyItMatters,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { book.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = AccentCyan,
                        trackColor = PanelNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Page Stepper Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current Page Pill (clickable to edit)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelNavyHighlight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                editingBookId = book.id
                                pageInputText = book.currentPage.toString()
                            }
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

                        // Quick +/- Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // -10
                            FilledTonalIconButton(
                                onClick = {
                                    books = books.map {
                                        if (it.id == book.id) it.copy(currentPage = (it.currentPage - 10).coerceAtLeast(0))
                                        else it
                                    }
                                },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = PanelNavy,
                                    contentColor = TextSecondary
                                )
                            ) {
                                Text(text = "-10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // +10
                            FilledTonalIconButton(
                                onClick = {
                                    books = books.map {
                                        if (it.id == book.id) it.copy(currentPage = (it.currentPage + 10).coerceAtMost(it.totalPages))
                                        else it
                                    }
                                },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = PanelNavy,
                                    contentColor = AccentCyan
                                )
                            ) {
                                Text(text = "+10", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // +1
                            FilledIconButton(
                                onClick = {
                                    books = books.map {
                                        if (it.id == book.id) it.copy(currentPage = (it.currentPage + 1).coerceAtMost(it.totalPages))
                                        else it
                                    }
                                },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = AccentCyan,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "+1", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
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
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Şu anda kaçıncı sayfadasın? (Maks: ${book.totalPages})",
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
