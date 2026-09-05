package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillNode
import com.example.ui.components.school.GraduationProjectDetailView
import com.example.ui.components.school.SchoolBooksView
import com.example.ui.theme.*

@Composable
fun SkillTreeGraphView(
    skills: List<SkillNode>,
    selectedSkill: SkillNode?,
    onSkillClick: (SkillNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubItemId by remember { mutableStateOf<String?>(null) }
    var showSchoolBooks by remember { mutableStateOf(false) }
    var showGraduationProject by remember { mutableStateOf(false) }

    BackHandler(enabled = activeSubItemId != null || showSchoolBooks || showGraduationProject) {
        activeSubItemId = null
        showSchoolBooks = false
        showGraduationProject = false
    }

    if (showSchoolBooks) {
        SchoolBooksView(onBack = { showSchoolBooks = false }, modifier = modifier)
        return
    }

    if (showGraduationProject) {
        GraduationProjectDetailView(onBack = { showGraduationProject = false }, modifier = modifier)
        return
    }

    if (activeSubItemId != null) {
        val color = when (activeSubItemId) {
            "sub_backend_dotnet" -> BranchDotNet
            "sub_devops_dist" -> BranchDevOps
            "sub_android" -> BranchAndroid
            "sub_english" -> BranchEnglish
            "sub_second_language" -> BranchLanguage
            "sub_git_github", "sub_linux_terminal", "sub_swe_fundamentals" -> BranchTools
            "sub_cyber_security" -> BranchSecurity
            "sub_personal_projects", "sub_projects", "sub_github", "sub_play_store", "sub_medium", "sub_linkedin" -> BranchPortfolio
            else -> AccentCyan
        }

        SubItemChecklistView(
            subItemId = activeSubItemId!!,
            accentColor = color,
            onBack = { activeSubItemId = null },
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
    ) {
        // Engineering Dot Grid Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEngineeringGrid()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Badge
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PanelNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = "🗺️ İNANÇ — COMPUTER ENGINEERING ROADMAP",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                letterSpacing = 1.sp,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "2026–2027 Winter Arc • Mühendislik Skill Tree",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            // ROOT NODE: 🎓 MEZUNİYET (2027)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.5.dp, AccentCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .border(1.5.dp, AccentCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎓", fontSize = 26.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "MEZUNİYET (2027)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.2.sp,
                                fontSize = 17.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Solid Computer Engineer: .NET Backend + C++ Systems + Native Android",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AccentCyan,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TreePill("L3/L4 Hedef Seviye", AccentEmerald)
                            TreePill("2 Yıllık Jr. Seviyesi", AccentAmber)
                        }
                    }
                }
            }

            // DOWNWARD STEM CONNECTOR
            item {
                TreeStemLine()
            }

            // PILLAR 1: SOFTWARE ENGINEER
            item {
                PillarSectionHeader(
                    emoji = "👨‍💻",
                    title = "SOFTWARE ENGINEER",
                    subtitle = "Backend, Native Mobil ve Gerçek Dünya Projeleri",
                    color = BranchDotNet
                )
            }

            item {
                TreeNodeCard(
                    title = "1. BACKEND / .NET",
                    subtitle = "C#, ASP.NET Core, EF Core, Clean Architecture",
                    emoji = "⚡",
                    color = BranchDotNet,
                    priorityStars = "⭐⭐⭐⭐⭐",
                    tags = listOf("C#", "ASP.NET Core", "PostgreSQL", "Clean Arch", "Redis & RabbitMQ"),
                    onClick = { activeSubItemId = "sub_backend_dotnet" }
                )
            }

            item {
                TreeNodeCard(
                    title = "2. DISTRIBUTED SYSTEMS / DEVOPS",
                    subtitle = "Docker, RabbitMQ, Microservices & Kubernetes",
                    emoji = "☸️",
                    color = BranchDevOps,
                    priorityStars = "⭐⭐⭐⭐",
                    tags = listOf("Docker", "RabbitMQ", "Microservices", "K8s (Lens)", "Prometheus"),
                    onClick = { activeSubItemId = "sub_devops_dist" }
                )
            }

            item {
                TreeNodeCard(
                    title = "3. NATIVE ANDROID",
                    subtitle = "Kotlin, Jetpack Compose, MVVM, Room & Hilt",
                    emoji = "🤖",
                    color = BranchAndroid,
                    priorityStars = "⭐⭐⭐",
                    tags = listOf("Kotlin Native", "Compose UI", "MVVM", "Play Store", "AI Agent"),
                    onClick = { activeSubItemId = "sub_android" }
                )
            }

            item {
                TreeNodeCard(
                    title = "4. PROJELER & PORTFÖY",
                    subtitle = "Eczane Sistemi, Mobil Uygulamalar, Medium Yazıları",
                    emoji = "🚀",
                    color = BranchPortfolio,
                    priorityStars = "⭐⭐⭐⭐",
                    tags = listOf("Eczane Sistemi", "GitHub Vitrini", "Medium Teknik Notları"),
                    onClick = { activeSubItemId = "sub_projects" }
                )
            }

            // CONNECTOR
            item {
                TreeStemLine()
            }

            // PILLAR 2: SYSTEMS & COMPUTER SCIENCE
            item {
                PillarSectionHeader(
                    emoji = "🧠",
                    title = "SYSTEMS & SCIENCE",
                    subtitle = "Mühendislik Temeli, CS Kitapları & Bitirme Projesi",
                    color = BranchCS
                )
            }

            item {
                TreeNodeCard(
                    title = "5. CS BAŞUCU KİTAPLARI (4 KUTSAL ESER)",
                    subtitle = "OSTEP, CS:APP, Computer Networks, DDIA",
                    emoji = "📚",
                    color = BranchCS,
                    priorityStars = "⭐⭐⭐⭐",
                    tags = listOf("OSTEP (OS)", "CS:APP (Hardware)", "Networks", "DDIA (Data Systems)"),
                    onClick = { showSchoolBooks = true }
                )
            }

            item {
                TreeNodeCard(
                    title = "6. BİTİRME TEZİ: LLM KV CACHE",
                    subtitle = "vAttention Paging, NVIDIA VMM ➔ Linux OS, C++ & LLaMA",
                    emoji = "🔬",
                    color = BranchGraduation,
                    priorityStars = "⭐⭐⭐⭐",
                    tags = listOf("vAttention", "KV Cache", "Linux Paging", "C++", "Ollama/Llama"),
                    onClick = { showGraduationProject = true }
                )
            }

            item {
                TreeNodeCard(
                    title = "7. ENGINEERING TOOLS & LINUX",
                    subtitle = "Linux Terminal, LazyVim, Git / GitHub & Cyber Security",
                    emoji = "🛠️",
                    color = BranchTools,
                    priorityStars = "⭐⭐⭐",
                    tags = listOf("Linux & CLI", "LazyVim", "Git Rebase", "AppSec Temelleri"),
                    onClick = { activeSubItemId = "sub_linux_terminal" }
                )
            }

            // CONNECTOR
            item {
                TreeStemLine()
            }

            // PILLAR 3: COMMUNICATION & HABITS
            item {
                PillarSectionHeader(
                    emoji = "🌍",
                    title = "COMMUNICATION & CULTURE",
                    subtitle = "Aktif İngilizce & Gelecekteki İkinci Yabancı Dil",
                    color = BranchEnglish
                )
            }

            item {
                TreeNodeCard(
                    title = "8. İNGİLİZCE (B2+ AKTİF)",
                    subtitle = "Oxford 5000, AI Speaking, Teknik Okuma & Medium",
                    emoji = "🇬🇧",
                    color = BranchEnglish,
                    priorityStars = "⭐⭐⭐⭐",
                    tags = listOf("Oxford 5000", "AI Speaking", "Tech Docs", "Medium Notes"),
                    onClick = { activeSubItemId = "sub_english" }
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .clickable { activeSubItemId = "sub_second_language" },
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BranchLanguage.copy(alpha = 0.1f))
                                    .border(1.dp, BranchLanguage.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🇪🇸", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "9. 2. Yabancı Dil: İspanyolca",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Kilitli",
                                        tint = AccentAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "Hobi Dili (İngilizce B2+ sonrası açılacak)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentAmber,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TreeStemLine() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(24.dp)
                .background(AccentCyan.copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(AccentCyan)
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(24.dp)
                .background(AccentCyan.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun PillarSectionHeader(
    emoji: String,
    title: String,
    subtitle: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 1.sp,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun TreeNodeCard(
    title: String,
    subtitle: String,
    emoji: String,
    color: Color,
    priorityStars: String,
    tags: List<String>,
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
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color.copy(alpha = 0.12f))
                            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 19.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 13.5.sp
                                )
                            )
                        }
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = color,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = priorityStars,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.take(4).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PanelNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
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

@Composable
private fun TreePill(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = PanelNavy,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        )
    }
}

private fun DrawScope.drawEngineeringGrid() {
    val dotSpacing = 32f
    val width = size.width
    val height = size.height

    var x = 0f
    while (x < width) {
        var y = 0f
        while (y < height) {
            drawCircle(
                color = Color(0xFF334155).copy(alpha = 0.25f),
                radius = 1.2f,
                center = Offset(x, y)
            )
            y += dotSpacing
        }
        x += dotSpacing
    }
}
