package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BranchId
import com.example.data.model.EngineeringProject
import com.example.data.model.ProjectWorkflowStage
import com.example.data.model.RoadmapProgressHelper
import com.example.data.model.SkillNode
import com.example.data.model.SubItemProgressStats
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
    val branchIds: List<BranchId> = emptyList()
)

private val careerSubItems = listOf(
    CategorySubItem(
        id = "sub_backend_dotnet",
        title = "Backend / .NET",
        subtitle = "C#, ASP.NET Core, EF Core, CQRS & Mimari",
        emoji = "⚡",
        description = "Modern RESTful API'ler, Clean Architecture, MediatR/CQRS, Dapper, SignalR, Polly & Redis pratikleri.",
        tag = "ASP.NET Core"
    ),
    CategorySubItem(
        id = "sub_devops_dist",
        title = "DevOps / Distributed Systems",
        subtitle = "Docker, Kubernetes, CI/CD & Dağıtık Mimari",
        emoji = "🐳",
        description = "Container orkestrasyonu, GitHub Actions CI/CD pipeline'ları, mesaj kuyrukları ve dayanıklı servis mimarileri.",
        tag = "Cloud Native"
    ),
    CategorySubItem(
        id = "sub_android",
        title = "Native Android (XML)",
        subtitle = "Kotlin, XML Layouts, ViewBinding & MVVM",
        emoji = "🤖",
        description = "Geleneksel XML View sistemi, ConstraintLayout, ViewBinding, RecyclerView, MVVM, Room DB, Hilt ve Retrofit mimarisi.",
        tag = "Kotlin & XML"
    ),
    CategorySubItem(
        id = "sub_personal_projects",
        title = "Kişisel Projeler",
        subtitle = "Uçtan Uca Geliştirilen Bağımsız Projeler",
        emoji = "🛠️",
        description = "Teorik bilgileri ürüne dönüştüren uçtan uca mimariye sahip full-stack ve mobil projeler.",
        tag = "Side Projects"
    ),
    CategorySubItem(
        id = "sub_btk_akademi",
        title = "BTK Akademi Diploma Avı",
        subtitle = "e-Devlet & Barkodlu Sertifika Takibi",
        emoji = "🎓",
        description = "Android, .NET, PostgreSQL, Docker, Test ve hızlı diploma kasmalık tüm ücretsiz e-Devlet onaylı sertifika takip listesi.",
        tag = "Ücretsiz Sertifikalar"
    )
)

private val techCultureSubItems = listOf(
    CategorySubItem(
        id = "sub_swe_fundamentals",
        title = "Software Engineering Fundamentals",
        subtitle = "SOLID, Clean Code, Tasarım Desenleri & Test",
        emoji = "📐",
        description = "Nesne yönelimli tasarım ilkeleri (SOLID), GoF tasarım desenleri, refactoring, DRY/KISS ve Test Piramidi (Unit & AAA).",
        tag = "SWE & Tasarım"
    ),
    CategorySubItem(
        id = "sub_data_structures_bigo",
        title = "Veri Yapıları & Algoritmalar",
        subtitle = "Big-O, Karmaşıklık & Temel Veri Tipleri",
        emoji = "⚡",
        description = "Zaman ve bellek analizi (Big-O), Hash Table, Ağaçlar (BST & Heap), İkili Arama ve QuickSort/MergeSort algoritmaları.",
        tag = "Big-O & Algoritma"
    ),
    CategorySubItem(
        id = "sub_system_design",
        title = "Sistem Tasarımı Temelleri",
        subtitle = "Ölçeklenebilirlik, Caching, CAP & Kuyruklar",
        emoji = "🏗️",
        description = "Yatay büyüme, Load Balancer, Caching (Cache-Aside), Rate Limiting, CAP Teoremi ve Event-Driven mesaj kuyrukları.",
        tag = "Sistem Tasarımı"
    ),
    CategorySubItem(
        id = "sub_networking_web",
        title = "Bilgisayar Ağları & Web Protokolleri",
        subtitle = "DNS, TCP/IP, HTTP/1-2-3 & İletişim",
        emoji = "🌐",
        description = "google.com istek yolculuğu, TCP 3-Way Handshake, TLS/HTTPS güvenliği, WebSocket, CDN ve CORS politikası.",
        tag = "Ağ & Protokol"
    ),
    CategorySubItem(
        id = "sub_git_github",
        title = "Git / GitHub",
        subtitle = "Versiyon Kontrol & İş Akışları",
        emoji = "🐙",
        description = "Git branching stratejileri (GitFlow, Trunk-based), rebase, conflict çözümü, PR inceleme kültürü ve GitHub Actions CI.",
        tag = "VCS & Git"
    ),
    CategorySubItem(
        id = "sub_linux_terminal",
        title = "Linux / Terminal",
        subtitle = "Bash, CLI Araçları & Çekirdek Mantığı",
        emoji = "🐧",
        description = "Linux dosya hiyerarşisi, process yönetimi, Bash betikleme, boru hatları (pipes), SSH ve LazyVim geliştirme ortamı.",
        tag = "Linux & CLI"
    ),
    CategorySubItem(
        id = "sub_cyber_security",
        title = "Cyber Security",
        subtitle = "AppSec, Web & Ağ Güvenliği",
        emoji = "🛡️",
        description = "Web ve API güvenliği (SQLi, XSS, CSRF), JWT açıkları, şifreleme algoritmaları ve güvenli parola (BCrypt) saklama.",
        tag = "Siber Güvenlik"
    ),
    CategorySubItem(
        id = "sub_ai_llm",
        title = "Yapay Zeka & LLM Okuryazarlığı",
        subtitle = "Büyük Dil Modelleri, RAG & AI Ajanları",
        emoji = "🤖",
        description = "Token & Context Window sınırları, Prompt Mühendisliği (CoT), RAG & Vektör Veritabanları ve Function Calling mimarisi.",
        tag = "AI & LLM"
    )
)

private val personalDevSubItems = listOf(
    CategorySubItem(
        id = "sub_winter_arc_discipline",
        title = "Winter Arc & Sağlık Protokolü",
        subtitle = "Uyku, Dopamin Detoksu, Su, Beslenme & Spor",
        emoji = "❄️",
        description = "Bozdum/Bozmadım dopamin detoksu, sirkadiyen uyku ritmi, 3.5L su, temiz beslenme, fitness, tenis ve yürüyüş.",
        tag = "Disiplin & Rutin"
    ),
    CategorySubItem(
        id = "sub_reading_books",
        title = "Kitap Dünyası",
        subtitle = "Tarih, Kişisel Gelişim, Roman & Felsefe",
        emoji = "📚",
        description = "Bilgisayar mühendisliği başucu kitaplarının haricinde tarih, biyografi, Stoacı felsefe ve dünya klasikleri.",
        tag = "Genel Kültür"
    ),
    CategorySubItem(
        id = "sub_card_sleights",
        title = "Kart Numaraları & İllüzyon",
        subtitle = "Sleight of Hand, Mekanikler & Repertuar",
        emoji = "🎴",
        description = "Double Lift, False Shuffles, Elmsley Count ve repertuardaki illüzyon & kart sihirbazlığı numaraları.",
        tag = "Sleight of Hand"
    ),
    CategorySubItem(
        id = "sub_anime_manhwa",
        title = "Anime & Manhwa Takibi",
        subtitle = "İzlenen Seriler & Güncel Chapterlar",
        emoji = "🍿",
        description = "Solo Leveling, ORV, AoT, HxH gibi favori anime serileri ve güncel manhwa bölüm takipleri.",
        tag = "Anime & Manhwa"
    ),
    CategorySubItem(
        id = "sub_english",
        title = "İngilizce (B2+)",
        subtitle = "Teknik Dokümantasyon, Speaking & Dinleme",
        emoji = "🇬🇧",
        description = "Oxford 5000 kelime dağarcığı, AI sesli konuşma pratiği, teknik podcast'ler ve mesleki akıcılık.",
        tag = "B2+ Fluency"
    ),
    CategorySubItem(
        id = "sub_second_language",
        title = "2. Yabancı Dil: İspanyolca",
        subtitle = "Kelime Uygulaması & Keyifli Hobi",
        emoji = "🇪🇸",
        description = "Kişisel kelime uygulamasına eklenecek temel İspanyolca kelimeler ve stressiz hobi dili temelleri.",
        tag = "İspanyolca (Hobi)"
    )
)

private val portfolioSubItems = listOf(
    CategorySubItem(
        id = "sub_github",
        title = "GitHub",
        subtitle = "Açık Kaynak Depoları & Vitrin",
        emoji = "🐙",
        description = "Depoları doldurmak, açık kaynak projeler, mimari şemalı profesyonel README'ler ve düzenli yeşil katkı takvimi.",
        tag = "Open Source"
    ),
    CategorySubItem(
        id = "sub_play_store",
        title = "Play Store",
        subtitle = "Canlı Mobil Uygulamalar",
        emoji = "📱",
        description = "Google Play Console süreci, yayınlanan utility/hobi/araç native Android uygulamaları ve güncellemeler.",
        tag = "Production Apps"
    ),
    CategorySubItem(
        id = "sub_medium",
        title = "Medium",
        subtitle = "Öğrenilenleri Yazıya Dökme & Teknik Hafıza",
        emoji = "✍️",
        description = "Öğrenilen ufak tefek her şeyi, mimari analizleri, backend ipuçlarını ve hata çözümlerini teknik makaleye dönüştürmek.",
        tag = "Tech Blog"
    )
)

@Composable
fun CategoriesDashboardView(
    skills: List<SkillNode>,
    projects: List<EngineeringProject>,
    onAdvanceProjectStage: (String, ProjectWorkflowStage) -> Unit = { _, _ -> },
    onRegressProjectStage: (String) -> Unit = {},
    onCreateProject: (String, String, String, ProjectWorkflowStage, String, String, String, List<String>) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onDeleteProject: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    onCategorySelected: (CategoryMeta) -> Unit = {}
) {
    var activeCategory by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val roadmapPrefs = remember { context.getSharedPreferences(RoadmapProgressHelper.PREFS_ROADMAP, Context.MODE_PRIVATE) }
    var prefsUpdateTrigger by remember { mutableIntStateOf(0) }

    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefsUpdateTrigger++
        }
    }

    DisposableEffect(roadmapPrefs) {
        roadmapPrefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            roadmapPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    LaunchedEffect(activeCategory) {
        if (activeCategory == null) {
            prefsUpdateTrigger++
        }
    }

    when (activeCategory) {
        "cat_school" -> {
            SchoolHubView(
                onBackToCategories = { activeCategory = null },
                modifier = modifier
            )
            return
        }
        "cat_career" -> {
            CategorySubItemsHubView(
                categoryTitle = "KARİYER",
                categoryEmoji = "💼",
                categorySubtitle = "Sektör Odaklı Uzmanlık & Mimari",
                categoryDescription = ".NET Backend, Dağıtık Sistemler, DevOps, Android ve Kişisel Projeler.",
                accentColor = BranchDotNet,
                subItems = careerSubItems,
                projects = projects,
                onAdvanceProjectStage = onAdvanceProjectStage,
                onRegressProjectStage = onRegressProjectStage,
                onCreateProject = onCreateProject,
                onDeleteProject = onDeleteProject,
                onBack = { activeCategory = null },
                modifier = modifier
            )
            return
        }
        "cat_tech_culture" -> {
            CategorySubItemsHubView(
                categoryTitle = "TEKNİK GENEL KÜLTÜR",
                categoryEmoji = "🧠",
                categorySubtitle = "Mühendislik Temelleri & Sistem Mantığı",
                categoryDescription = "Mühendislik ilkeleri, algoritmalar, sistem tasarımı, ağ protokolleri, Git, Linux, siber güvenlik ve yapay zeka.",
                accentColor = BranchTools,
                subItems = techCultureSubItems,
                onBack = { activeCategory = null },
                modifier = modifier
            )
            return
        }
        "cat_personal_dev" -> {
            CategorySubItemsHubView(
                categoryTitle = "KİŞİSEL GELİŞİM",
                categoryEmoji = "🌱",
                categorySubtitle = "Winter Arc, Alışkanlıklar & Hobiler",
                categoryDescription = "Dopamin detoksu, uyku, beslenme, spor, tenis, kitaplar, kart illüzyonları, anime/manhwa ve İngilizce.",
                accentColor = AccentAmber,
                subItems = personalDevSubItems,
                onBack = { activeCategory = null },
                modifier = modifier
            )
            return
        }
        "cat_portfolio" -> {
            CategorySubItemsHubView(
                categoryTitle = "PORTFÖY",
                categoryEmoji = "🚀",
                categorySubtitle = "GitHub, Play Store & Medium Çıktıları",
                categoryDescription = "GitHub depolarını doldurmak, Play Store'a canlı uygulamalar çıkarmak ve öğrenilen her şeyi Medium'da teknik yazıya dökmek.",
                accentColor = BranchPortfolio,
                subItems = portfolioSubItems,
                onBack = { activeCategory = null },
                modifier = modifier
            )
            return
        }
    }

    val categories = listOf(
        CategoryMeta(
            id = "cat_school",
            title = "OKUL",
            emoji = "🎓",
            subtitle = "Bilgisayar Mühendisliği & Akademik",
            description = "Üniversite ders müfredatı, veri yapıları & algoritmalar, bitirme tezi ve CS başucu kitapları.",
            accentColor = BranchCS,
            tags = listOf("Okul Dersleri", "Bitirme Projesi", "CS Kitapları"),
            branchIds = listOf(BranchId.COMPUTER_SCIENCE, BranchId.GRADUATION_PROJECT, BranchId.CERTIFICATES)
        ),
        CategoryMeta(
            id = "cat_career",
            title = "KARİYER",
            emoji = "💼",
            subtitle = "Sektör Odaklı Uzmanlık & Mimari",
            description = ".NET Backend, Dağıtık Sistemler, DevOps, Native Android, Kişisel Projeler ve BTK Akademi.",
            accentColor = BranchDotNet,
            tags = listOf("Backend / .NET", "DevOps", "Android", "Projeler", "BTK Akademi"),
            branchIds = listOf(BranchId.BACKEND_DOTNET, BranchId.DISTRIBUTED_DEVOPS, BranchId.ANDROID_MOBILE, BranchId.CYBER_SECURITY)
        ),
        CategoryMeta(
            id = "cat_tech_culture",
            title = "TEKNİK GENEL KÜLTÜR",
            emoji = "🧠",
            subtitle = "Sistem Mantığı & Mühendislik",
            description = "Git/GitHub pratikleri, Linux terminal yetkinliği, siber güvenlik temelleri ve yazılım prensipleri.",
            accentColor = BranchTools,
            tags = listOf("Git / GitHub", "Linux / CLI", "Cyber Security", "SWE Fund."),
            branchIds = listOf(BranchId.ENGINEERING_TOOLS)
        ),
        CategoryMeta(
            id = "cat_personal_dev",
            title = "KİŞİSEL GELİŞİM",
            emoji = "🌱",
            subtitle = "Winter Arc, Alışkanlıklar & Hobiler",
            description = "Dopamin detoksu, uyku, spor/tenis, tarih ve gelişim kitapları, kart illüzyonları, anime/manhwa ve İngilizce.",
            accentColor = AccentAmber,
            tags = listOf("Winter Arc", "Kitaplar", "Kart Numaraları", "Anime/Manhwa", "İngilizce"),
            branchIds = listOf(BranchId.ENGLISH, BranchId.SECOND_LANGUAGE, BranchId.KNOWLEDGE_MANAGEMENT)
        ),
        CategoryMeta(
            id = "cat_portfolio",
            title = "PORTFÖY",
            emoji = "🚀",
            subtitle = "GitHub, Play Store & Medium Çıktıları",
            description = "GitHub depolarını doldurmak, Play Store'a canlı uygulamalar çıkarmak ve öğrenilen her şeyi Medium'da teknik yazıya dökmek.",
            accentColor = BranchPortfolio,
            tags = listOf("GitHub", "Play Store", "Medium"),
            branchIds = listOf(BranchId.PORTFOLIO_OUTPUT)
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                Text(
                    text = "KATEGORİLER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 1.2.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Gelişim alanlarını 5 ana sütun altında takip et.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }

        items(categories, key = { it.id }) { cat ->
            val subItemIds = when (cat.id) {
                "cat_career" -> careerSubItems.map { it.id }
                "cat_tech_culture" -> techCultureSubItems.map { it.id }
                "cat_personal_dev" -> personalDevSubItems.map { it.id }
                "cat_portfolio" -> portfolioSubItems.map { it.id }
                else -> emptyList()
            }

            val stats = remember(cat.id, prefsUpdateTrigger, projects) {
                if (cat.id == "cat_school") {
                    SubItemProgressStats(
                        totalCount = 4,
                        completedCount = 0,
                        practiceCount = 0,
                        theoryCount = 0,
                        earnedPoints = 0f,
                        progressFraction = 0f,
                        progressPercent = 0
                    )
                } else {
                    RoadmapProgressHelper.getCategoryStats(subItemIds, roadmapPrefs, projects)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .clickable {
                        activeCategory = cat.id
                        onCategorySelected(cat)
                    },
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
                                    .background(cat.accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, cat.accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = cat.emoji, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = cat.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = cat.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = cat.accentColor,
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
                        text = cat.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Metric Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (stats.progressPercent > 0) "%${stats.progressPercent} İlerleme" else "%0 Başlanmadı",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (stats.progressPercent > 0) cat.accentColor else TextDarkMuted,
                                fontSize = 11.5.sp
                            )
                        )

                        Text(
                            text = when (cat.id) {
                                "cat_school" -> "${stats.completedCount}/${stats.totalCount} Modül • 4 Kitap"
                                "cat_career" -> "${stats.completedCount}/${stats.totalCount} Konu & Proje"
                                "cat_portfolio" -> "${stats.completedCount}/${stats.totalCount} Çıktı & Proje"
                                else -> "${stats.completedCount}/${stats.totalCount} Konu Tamamlandı"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (stats.completedCount > 0) TextSecondary else TextDarkMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { stats.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = cat.accentColor,
                        trackColor = PanelNavy
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            cat.tags.take(3).forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PanelNavy,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    )
                                }
                            }
                        }

                        if (stats.practiceCount > 0 || stats.theoryCount > 0) {
                            Text(
                                text = "🛠️ ${stats.practiceCount} • 📘 ${stats.theoryCount}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextDarkMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
