package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BranchId
import com.example.ui.components.school.SchoolHubView
import com.example.ui.theme.*

data class CategoryMeta(
    val id: String,
    val title: String,
    val emoji: String,
    val subtitle: String,
    val description: String,
    val accentColor: Color,
    val tags: List<String>,
    val branchIds: List<BranchId> = emptyList(),
    val icon: ImageVector? = null
)

private val careerSubItems = listOf(
    CategorySubItem(
        id = "sub_backend_dotnet",
        title = "Backend / .NET",
        subtitle = "C#, ASP.NET Core, EF Core, CQRS & Mimari",
        emoji = "⚡",
        description = "Modern RESTful API'ler, Clean Architecture, MediatR/CQRS, Dapper, SignalR, Polly & Redis pratikleri.",
        tag = "ASP.NET Core",
        icon = Icons.Outlined.Storage
    ),
    CategorySubItem(
        id = "sub_devops_dist",
        title = "DevOps & dağıtık sistemler",
        subtitle = "Docker, Kubernetes, CI/CD & Dağıtık Mimari",
        emoji = "🐳",
        description = "Container orkestrasyonu, GitHub Actions CI/CD pipeline'ları, mesaj kuyrukları ve dayanıklı servis mimarileri.",
        tag = "Cloud Native",
        icon = Icons.Outlined.Cloud
    ),
    CategorySubItem(
        id = "sub_android",
        title = "Android",
        subtitle = "Kotlin, XML Layouts, ViewBinding & MVVM",
        emoji = "🤖",
        description = "Geleneksel XML View sistemi, ConstraintLayout, ViewBinding, RecyclerView, MVVM, Room DB, Hilt ve Retrofit mimarisi.",
        tag = "Kotlin & XML",
        icon = Icons.Outlined.Android
    ),
    CategorySubItem(
        id = "sub_btk_akademi",
        title = "BTK kursları",
        subtitle = "Seçtiğin eğitimleri takip et",
        emoji = "🎓",
        description = "Backend, Android ve destekleyici eğitimlerden seçtiğin kursları takip et.",
        tag = "Ücretsiz Sertifikalar",
        icon = Icons.Outlined.WorkspacePremium
    )
)

private val techCultureSubItems = listOf(
    CategorySubItem(
        id = "sub_swe_fundamentals",
        title = "Yazılım mühendisliği temelleri",
        subtitle = "SOLID, Clean Code, Tasarım Desenleri & Test",
        emoji = "📐",
        description = "Nesne yönelimli tasarım ilkeleri (SOLID), GoF tasarım desenleri, refactoring, DRY/KISS ve Test Piramidi (Unit & AAA).",
        tag = "SWE & Tasarım",
        icon = Icons.Outlined.Architecture
    ),
    CategorySubItem(
        id = "sub_data_structures_bigo",
        title = "Veri Yapıları & Algoritmalar",
        subtitle = "Big-O, Karmaşıklık & Temel Veri Tipleri",
        emoji = "⚡",
        description = "Zaman ve bellek analizi (Big-O), Hash Table, Ağaçlar (BST & Heap), İkili Arama ve QuickSort/MergeSort algoritmaları.",
        tag = "Big-O & Algoritma",
        icon = Icons.Outlined.DataObject
    ),
    CategorySubItem(
        id = "sub_system_design",
        title = "Sistem Tasarımı Temelleri",
        subtitle = "Ölçeklenebilirlik, Caching, CAP & Kuyruklar",
        emoji = "🏗️",
        description = "Yatay büyüme, Load Balancer, Caching (Cache-Aside), Rate Limiting, CAP Teoremi ve Event-Driven mesaj kuyrukları.",
        tag = "Sistem Tasarımı",
        icon = Icons.Outlined.Hub
    ),
    CategorySubItem(
        id = "sub_networking_web",
        title = "Bilgisayar Ağları & Web Protokolleri",
        subtitle = "DNS, TCP/IP, HTTP/1-2-3 & İletişim",
        emoji = "🌐",
        description = "google.com istek yolculuğu, TCP 3-Way Handshake, TLS/HTTPS güvenliği, WebSocket, CDN ve CORS politikası.",
        tag = "Ağ & Protokol",
        icon = Icons.Outlined.Lan
    ),
    CategorySubItem(
        id = "sub_git_github",
        title = "Git / GitHub",
        subtitle = "Versiyon Kontrol & İş Akışları",
        emoji = "🐙",
        description = "Git branching stratejileri (GitFlow, Trunk-based), rebase, conflict çözümü, PR inceleme kültürü ve GitHub Actions CI.",
        tag = "VCS & Git",
        icon = Icons.Outlined.ForkRight
    ),
    CategorySubItem(
        id = "sub_linux_terminal",
        title = "Linux / Terminal",
        subtitle = "Bash, CLI Araçları & Çekirdek Mantığı",
        emoji = "🐧",
        description = "Linux dosya hiyerarşisi, process yönetimi, Bash betikleme, boru hatları (pipes), SSH ve LazyVim geliştirme ortamı.",
        tag = "Linux & CLI",
        icon = Icons.Outlined.Terminal
    ),
    CategorySubItem(
        id = "sub_cyber_security",
        title = "Cyber Security",
        subtitle = "AppSec, Web & Ağ Güvenliği",
        emoji = "🛡️",
        description = "Web ve API güvenliği (SQLi, XSS, CSRF), JWT açıkları, şifreleme algoritmaları ve güvenli parola (BCrypt) saklama.",
        tag = "Siber Güvenlik",
        icon = Icons.Outlined.Security
    ),
    CategorySubItem(
        id = "sub_ai_llm",
        title = "Yapay Zeka & LLM Okuryazarlığı",
        subtitle = "Büyük Dil Modelleri, RAG & AI Ajanları",
        emoji = "🤖",
        description = "Token & Context Window sınırları, Prompt Mühendisliği (CoT), RAG & Vektör Veritabanları ve Function Calling mimarisi.",
        tag = "AI & LLM",
        icon = Icons.Outlined.Psychology
    )
)

private val personalDevSubItems = listOf(
    CategorySubItem(
        id = "sub_reading_books",
        title = "Kitaplar",
        subtitle = "Tarih, Kişisel Gelişim, Roman & Felsefe",
        emoji = "📚",
        description = "Bilgisayar mühendisliği başucu kitaplarının haricinde tarih, biyografi, Stoacı felsefe ve dünya klasikleri.",
        tag = "Genel Kültür",
        icon = Icons.Outlined.AutoStories
    ),
    CategorySubItem(
        id = "sub_card_sleights",
        title = "Kart numaraları",
        subtitle = "Numaraların ve hatırlatma notların",
        emoji = "🎴",
        description = "Double Lift, False Shuffles, Elmsley Count ve repertuardaki illüzyon & kart sihirbazlığı numaraları.",
        tag = "Sleight of Hand",
        icon = Icons.Outlined.Style
    ),
    CategorySubItem(
        id = "sub_anime_manhwa",
        title = "Anime & Manhwa",
        subtitle = "İzlenen Seriler & Güncel Chapterlar",
        emoji = "🍿",
        description = "Solo Leveling, ORV, AoT, HxH gibi favori anime serileri ve güncel manhwa bölüm takipleri.",
        tag = "Anime & Manhwa",
        icon = Icons.Outlined.Tv
    ),
    CategorySubItem(
        id = "sub_english",
        title = "İngilizce",
        subtitle = "Teknik Dokümantasyon, Speaking & Dinleme",
        emoji = "🇬🇧",
        description = "Oxford 5000 kelime dağarcığı, AI sesli konuşma pratiği, teknik podcast'ler ve mesleki akıcılık.",
        tag = "B2+ Fluency",
        icon = Icons.Outlined.Language
    ),
    CategorySubItem(
        id = "sub_second_language",
        title = "İspanyolca",
        subtitle = "Kelime Uygulaması & Keyifli Hobi",
        emoji = "🇪🇸",
        description = "Kişisel kelime uygulamasına eklenecek temel İspanyolca kelimeler ve stressiz hobi dili temelleri.",
        tag = "İspanyolca (Hobi)",
        icon = Icons.Outlined.Language
    )
)

private val portfolioSubItems = listOf(
    CategorySubItem(
        id = "sub_github",
        title = "GitHub",
        subtitle = "Backend ve mobil projelerin",
        emoji = "🐙",
        description = "Depoları doldurmak, açık kaynak projeler, mimari şemalı profesyonel README'ler ve düzenli yeşil katkı takvimi.",
        tag = "Open Source",
        icon = Icons.Outlined.Code
    ),
    CategorySubItem(
        id = "sub_play_store",
        title = "Uygulamalarım",
        subtitle = "Clawssary ve sonraki uygulamaların",
        emoji = "📱",
        description = "Google Play Console süreci, yayınlanan utility/hobi/araç native Android uygulamaları ve güncellemeler.",
        tag = "Production Apps",
        icon = Icons.Outlined.Smartphone
    ),
    CategorySubItem(
        id = "sub_medium",
        title = "Medium",
        subtitle = "Öğrenilenleri Yazıya Dökme & Teknik Hafıza",
        emoji = "✍️",
        description = "Öğrenilen ufak tefek her şeyi, mimari analizleri, backend ipuçlarını ve hata çözümlerini teknik makaleye dönüştürmek.",
        tag = "Tech Blog",
        icon = Icons.AutoMirrored.Outlined.Article
    )
)

@Composable
fun CategoriesDashboardView(
    modifier: Modifier = Modifier,
    onOpenThemePicker: () -> Unit = {}
) {
    var activeCategory by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    when (activeCategory) {
        "school" -> {
            SchoolHubView(onBackToCategories = { activeCategory = null }, modifier = modifier)
            return
        }
        "software", "portfolio", "hobbies" -> {
            val title = when (activeCategory) { "software" -> "Yazılım"; "portfolio" -> "Ürettiklerim"; else -> "Hobiler" }
            val entries = when (activeCategory) {
                "software" -> careerSubItems.filter { it.id in setOf("sub_backend_dotnet", "sub_android", "sub_btk_akademi") } +
                    CategorySubItem("sub_knowledge_archive", "Konu arşivi", "İhtiyaç duyduğunda aç", "📚", "", "", Icons.Outlined.FolderOpen)
                "portfolio" -> portfolioSubItems
                else -> personalDevSubItems
            }
            CategorySubItemsHubView(
                categoryTitle = title, categoryEmoji = "", categorySubtitle = when (activeCategory) {
                    "software" -> "Backend ve Android odağında."
                    "portfolio" -> "Projelerin, uygulamaların ve yazıların."
                    else -> "Kitapların, notların ve takip ettiklerin."
                }, categoryDescription = "", accentColor = AccentCyan, subItems = entries,
                onBack = { activeCategory = null }, onOpenThemePicker = onOpenThemePicker, modifier = modifier
            )
            return
        }
    }
    val entries = listOf(
        CategoryMeta("school", "Okul", "🎓", "Dersler, yoklama, notlar ve haftalık çalışma", "", BranchCS, emptyList(), icon = Icons.Outlined.School),
        CategoryMeta("software", "Yazılım", "💻", "Backend, Android ve BTK kursları", "", BranchDotNet, emptyList(), icon = Icons.Outlined.Code),
        CategoryMeta("portfolio", "Ürettiklerim", "🚀", "GitHub, uygulamalarım ve Medium", "", BranchPortfolio, emptyList(), icon = Icons.Outlined.RocketLaunch),
        CategoryMeta("hobbies", "Hobiler", "📚", "Kitaplar, kart numaraları, anime ve diller", "", AccentAmber, emptyList(), icon = Icons.Outlined.AutoStories)
    )
    LazyColumn(
        modifier = modifier.fillMaxSize().background(CanvasDark).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Alanlarım", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
            }
        }
        items(entries, key = { it.id }) { entry ->
            Card(onClick = { activeCategory = entry.id }, colors = CardDefaults.cardColors(containerColor = PanelNavy)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    entry.icon?.let { Icon(it, null, tint = entry.accentColor, modifier = Modifier.size(26.dp)) }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text(entry.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                }
            }
        }
        item { Text("Günlük alışkanlıkların aşağıdaki Günlük sekmesinde.", color = TextSecondary, style = MaterialTheme.typography.bodySmall) }
    }
}

internal val knowledgeArchiveItems: List<CategorySubItem>
    get() = techCultureSubItems + careerSubItems.filter { it.id == "sub_devops_dist" }
