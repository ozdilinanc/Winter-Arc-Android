package com.example.ui.components.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class SchoolSubScreen {
    OVERVIEW,
    COURSES,
    GRADUATION_PROJECT,
    BOOKS
}

@Composable
fun SchoolHubView(
    onBackToCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSubScreen by remember { mutableStateOf(SchoolSubScreen.OVERVIEW) }

    when (currentSubScreen) {
        SchoolSubScreen.COURSES -> {
            SchoolCoursesView(onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW })
        }
        SchoolSubScreen.GRADUATION_PROJECT -> {
            GraduationProjectDetailView(onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW })
        }
        SchoolSubScreen.BOOKS -> {
            SchoolBooksView(onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW })
        }
        SchoolSubScreen.OVERVIEW -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(CanvasDark)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Back Button
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBackToCategories() }
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

                // Header Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BranchCS.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
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
                                    .background(BranchCS.copy(alpha = 0.15f))
                                    .border(1.dp, BranchCS.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎓", fontSize = 26.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "OKUL HUB",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Müfredat dersleri, bitirme projesi ve temel kitaplar.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Section Label
                item {
                    Text(
                        text = "OKUL GELİŞİM ALANLARI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Option 1: Okul Dersleri
                item {
                    SchoolOptionCard(
                        emoji = "📚",
                        title = "Okul Dersleri",
                        subtitle = "Müfredat, Krediler & Not Takibi",
                        description = "Dönemlik aldığın dersler, krediler, harf notları ve başarı durumu.",
                        tag = "Ders Ekle / Yönet",
                        accentColor = AccentCyan,
                        onClick = { currentSubScreen = SchoolSubScreen.COURSES }
                    )
                }

                // Option 2: Bitirme Projesi
                item {
                    SchoolOptionCard(
                        emoji = "🔬",
                        title = "Bitirme Projesi",
                        subtitle = "LLM KV Cache & v-Attention Optimizasyonu",
                        description = "Sistem araştırması, C++ çekirdek geliştirme, benchmarklar ve tez yazım aşamaları.",
                        tag = "Tez & Araştırma",
                        accentColor = BranchGraduation,
                        onClick = { currentSubScreen = SchoolSubScreen.GRADUATION_PROJECT }
                    )
                }

                // Option 3: CS Kitapları
                item {
                    SchoolOptionCard(
                        emoji = "📖",
                        title = "CS Kitapları",
                        subtitle = "4 Başucu Kitabı & Sayfa Takibi",
                        description = "OSTEP, CS:APP, DDIA ve Computer Networks kitaplarında okuma ilerlemesi ve sayfa takibi.",
                        tag = "Sayfa Takibi",
                        accentColor = BranchTools,
                        onClick = { currentSubScreen = SchoolSubScreen.BOOKS }
                    )
                }
            }
        }
    }
}

@Composable
private fun SchoolOptionCard(
    emoji: String,
    title: String,
    subtitle: String,
    description: String,
    tag: String,
    accentColor: Color,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = subtitle,
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
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PanelNavy,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
