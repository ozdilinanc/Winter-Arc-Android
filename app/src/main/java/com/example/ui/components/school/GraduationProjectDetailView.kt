package com.example.ui.components.school

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
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
fun GraduationProjectDetailView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    var milestones by remember {
        mutableStateOf(
            listOf(
                ProjectMilestone("m1", "vAttention & GPU VMM Literatür Taraması", "NVIDIA GPU Virtual Memory Management (VMM) ve paged memory analizleri.", true),
                ProjectMilestone("m2", "KV Cache & Token Bellek Parçalanma Analizi", "Uzun bağlamlı LLM çıkarımlarında bellek israfının ve token paylaşımının ölçümü.", true),
                ProjectMilestone("m3", "Linux OS Non-Contiguous Paging Mimarisi", "Bellek tahsisini OS düzeyinde yöneten C++ sanal bellek prototip motoru.", false),
                ProjectMilestone("m4", "Ollama / LLaMA C++ Entegrasyonu", "Llama / Ollama çıkarım motoruna özel bellek yöneticisini bağlama.", false),
                ProjectMilestone("m5", "Karşılaştırmalı Benchmark & Performans Testleri", "Throughput, latency ve GPU/RAM bellek optimizasyon ölçümleri.", false),
                ProjectMilestone("m6", "IEEE Bitirme Tezi Raporu & Jüri Savunması", "Akademik tez yazımı, kod yayını ve canlı jüri sunumu.", false)
            )
        )
    }

    val completedCount = milestones.count { it.isCompleted }
    val progress = if (milestones.isNotEmpty()) completedCount.toFloat() / milestones.size.toFloat() else 0f

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

        // Hero Project Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BranchGraduation.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BranchGraduation.copy(alpha = 0.15f))
                                .border(1.dp, BranchGraduation.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔬", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BİTİRME TEZİ PROJESİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BranchGraduation,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "LLM KV Cache Optimizasyonu",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "%${(progress * 100).toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BranchGraduation,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "LLM'lerde token paylaşımında kullanılan vAttention yaklaşımını (non-contiguous sanal bellek sayfalaması) inceliyoruz. NVIDIA GPU VMM (Virtual Memory Management) driver çağrılarıyla yapılan bu bellek yönetimini Linux OS çekirdek ve bellek seviyesinde C++ ve Ollama/LLaMA entegrasyonu ile optimize etmeyi hedefleyen ileri seviye sistem mühendisliği tezi.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = BranchGraduation,
                        trackColor = PanelNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("vAttention", "KV Cache", "C++", "Linux OS Paging", "Ollama/Llama").forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Milestones
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TEZ VE GELİŞTİRME AŞAMALARI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    )
                )

                Text(
                    text = "$completedCount / ${milestones.size} Tamamlandı",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BranchGraduation,
                        fontSize = 11.sp
                    )
                )
            }
        }

        items(milestones, key = { it.id }) { milestone ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (milestone.isCompleted) StatusCompleted.copy(alpha = 0.35f) else BorderSubtle,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        milestones = milestones.map {
                            if (it.id == milestone.id) it.copy(isCompleted = !it.isCompleted)
                            else it
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (milestone.isCompleted) StatusCompleted else Color.Transparent)
                            .border(1.5.dp, if (milestone.isCompleted) StatusCompleted else TextDarkMuted, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (milestone.isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (milestone.isCompleted) TextMuted else TextPrimary,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
