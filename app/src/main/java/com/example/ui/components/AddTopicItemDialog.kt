package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CustomTopicItem
import com.example.data.model.CustomTopicRepository
import com.example.data.model.TopicGenre
import com.example.ui.theme.*
import com.example.ui.util.rememberHapticEngine
import java.util.UUID

@Composable
fun AddTopicItemDialog(
    subItemId: String,
    accentColor: Color,
    onDismiss: () -> Unit,
    onAdd: (CustomTopicItem) -> Unit
) {
    val hapticEngine = rememberHapticEngine()
    val genres = remember(subItemId) { CustomTopicRepository.getGenresForSubItem(subItemId) }
    var selectedGenre by remember { mutableStateOf(genres.firstOrNull()) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var practiceTask by remember { mutableStateOf("") }

    val (dialogTitle, titlePlaceholder, practicePlaceholder) = when (subItemId) {
        "sub_reading_books" -> Triple(
            "📚 Yeni Kitap Ekle",
            "Kitap Adı & Yazar (Örn: Yuval Noah Harari - 21. Yüzyıl İçin 21 Ders)",
            "Okuma Hedefi (Örn: Günde 25 sayfa oku ve önemli fikirleri not al)"
        )
        "sub_card_sleights" -> Triple(
            "🃏 Yeni Kart Numarası Ekle",
            "Numara / Teknik Adı (Örn: Classic Pass & Charlier Cut)",
            "Pratik Hedefi (Örn: Ayna karşısında 20 kez takılmadan prova yap)"
        )
        "sub_anime_manhwa" -> Triple(
            "🍿 Yeni Anime / Manhwa Ekle",
            "Seri Başlığı (Örn: Vinland Saga veya Omniscient Reader's Viewpoint)",
            "Hedef / Rutin (Örn: Güncel arkı tamamla ve dövüş panellerini incele)"
        )
        else -> Triple(
            "✨ Yeni İçerik Ekle",
            "Başlık girin...",
            "Hedef / Pratik görevi girin..."
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dialogTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                    )

                    IconButton(
                        onClick = {
                            hapticEngine.vibrateSelection()
                            onDismiss()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Genre Selection Label
                Text(
                    text = "TÜR / KATEGORİ (GENRE) SEÇİN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Genre Chips Row / Flow
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres, key = { it.title }) { genre ->
                        val isSelected = selectedGenre?.title == genre.title
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.22f) else PanelNavy,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) accentColor else BorderSubtle
                            ),
                            modifier = Modifier.clickable {
                                hapticEngine.vibrateSelection()
                                selectedGenre = genre
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = genre.emoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = genre.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) accentColor else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                if (selectedGenre != null && selectedGenre!!.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ℹ️ ${selectedGenre!!.description}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkMuted,
                            fontSize = 10.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                Text(
                    text = "BAŞLIK *",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text(text = titlePlaceholder, color = TextDarkMuted, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PanelNavy,
                        unfocusedContainerColor = PanelNavy,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Input
                Text(
                    text = "AÇIKLAMA VEYA NOT (İSTEĞE BAĞLI)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text(text = "Kısa konu, yazar, stüdyo veya teknik detaylar...", color = TextDarkMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PanelNavy,
                        unfocusedContainerColor = PanelNavy,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Practice Task Input
                Text(
                    text = "HEDEF / PRATİK GÖREVİ (İSTEĞE BAĞLI)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = practiceTask,
                    onValueChange = { practiceTask = it },
                    placeholder = { Text(text = practicePlaceholder, color = TextDarkMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PanelNavy,
                        unfocusedContainerColor = PanelNavy,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            hapticEngine.vibrateSelection()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = PanelNavy)
                    ) {
                        Text(
                            text = "İptal",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                hapticEngine.vibrateSkillCompleted()
                                val genre = selectedGenre ?: genres.first()
                                val newItem = CustomTopicItem(
                                    id = "custom_${subItemId}_${UUID.randomUUID().toString().take(8)}",
                                    subItemId = subItemId,
                                    genreTitle = genre.title,
                                    genreEmoji = genre.emoji,
                                    title = title.trim(),
                                    description = description.trim(),
                                    practiceTask = practiceTask.trim()
                                )
                                onAdd(newItem)
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            disabledContainerColor = PanelNavy
                        )
                    ) {
                        Text(
                            text = "Listeye Ekle",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (title.isNotBlank()) CanvasDark else TextDarkMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
