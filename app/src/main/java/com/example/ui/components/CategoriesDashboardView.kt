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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BranchId
import com.example.data.model.EngineeringProject
import com.example.data.model.SkillNode
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
        title = "BTK Akademi",
        subtitle = "Sertifika & Eğitim Programları",
        emoji = "📜",
        description = "BTK Akademi üzerindeki uzmanlık programları, veri tabanı, yazılım mimarisi ve sertifikasyon süreçleri.",
        tag = "Eğitim"
    )
)

private val techCultureSubItems = listOf(
    CategorySubItem(
        id = "sub_git_github",
        title = "Git / GitHub",
        subtitle = "Versiyon Kontrol & İş Akışları",
        emoji = "🐙",
        description = "Git branching stratejileri (GitFlow, Trunk-based), rebase, conflict çözümü, PR inceleme kültürü ve commit hijyeni.",
        tag = "VCS"
    ),
    CategorySubItem(
        id = "sub_linux_terminal",
        title = "Linux / Terminal",
        subtitle = "Bash, CLI Araçları & Çekirdek Mantığı",
        emoji = "🐧",
        description = "Linux dosya hiyerarşisi, process yönetimi, Bash betikleme, SSH anahtarları ve terminal verimlilik araçları (tmux, zsh, vim).",
        tag = "Linux & CLI"
    ),
    CategorySubItem(
        id = "sub_cyber_security",
        title = "Cyber Security",
        subtitle = "Ağ Güvenliği, OWASP Top 10 & Savunma",
        emoji = "🛡️",
        description = "Web ve API güvenliği, yetkilendirme (OAuth2/JWT) açıkları, şifreleme algoritmaları ve güvenli kodlama standartları.",
        tag = "AppSec"
    ),
    CategorySubItem(
        id = "sub_swe_fundamentals",
        title = "Software Engineering Fundamentals",
        subtitle = "SOLID, Design Patterns & Clean Code",
        emoji = "📐",
        description = "Nesne yönelimli tasarım ilkeleri (SOLID), GoF tasarım desenleri, refactoring, DRY, KISS ve kod okunabilirliği.",
        tag = "Software Design"
    )
)

private val personalDevSubItems = listOf(
    CategorySubItem(
        id = "sub_english",
        title = "İngilizce",
        subtitle = "Teknik Dokümantasyon, B2+ & Konuşma",
        emoji = "🇬🇧",
        description = "Teknik RFC & kaynakları anlama, mühendislik makaleleri yazma, akıcı konuşma ve mesleki kelime dağarcığı.",
        tag = "B2+ Fluency"
    ),
    CategorySubItem(
        id = "sub_second_language",
        title = "2. Yabancı Dil: İspanyolca",
        subtitle = "Hobi & Kültür Dili (🔒 Kilitli - B2+ Sonrası)",
        emoji = "🇪🇸",
        description = "Hedeflenen ikinci yabancı dil olarak İspanyolca. İngilizce B2+ seviyesine oturduktan sonra stressiz bir hobi olarak açılacak.",
        tag = "İspanyolca (🔒)"
    )
)

private val portfolioSubItems = listOf(
    CategorySubItem(
        id = "sub_github",
        title = "GitHub",
        subtitle = "Açık Kaynak Depoları & Katkılar",
        emoji = "🐙",
        description = "Yıldız alan açık kaynak projeler, düzenli yeşil katkı takvimi ve profesyonel README vitrinleri.",
        tag = "Open Source"
    ),
    CategorySubItem(
        id = "sub_play_store",
        title = "Play Store",
        subtitle = "Canlı Mobil Uygulamalar",
        emoji = "📱",
        description = "Google Play Store'da yayınlanmış, kullanıcıya ulaşan native Android uygulamaları ve güncellemeleri.",
        tag = "Production Apps"
    ),
    CategorySubItem(
        id = "sub_medium",
        title = "Medium",
        subtitle = "Teknik Makaleler & İncelemeler",
        emoji = "✍️",
        description = "Derinlemesine mimari analizler, yazılım tasarım desenleri ve mühendislik deneyimlerini paylaşan blog yazıları.",
        tag = "Tech Blog"
    ),
    CategorySubItem(
        id = "sub_linkedin",
        title = "LinkedIn",
        subtitle = "Profesyonel Ağ & Görünürlük",
        emoji = "💼",
        description = "Kariyer başarıları, sertifikalar, proje paylaşımları ve sektör bağlantıları.",
        tag = "Networking"
    ),
    CategorySubItem(
        id = "sub_projects",
        title = "Projeler",
        subtitle = "Öne Çıkan Mühendislik Eserleri",
        emoji = "🏆",
        description = "Özgeçmişte ve portföyde en öne çıkan anahtar projeler, canlı demo linkleri ve mimari dokümantasyonlar.",
        tag = "Showcase"
    )
)

@Composable
fun CategoriesDashboardView(
    skills: List<SkillNode>,
    projects: List<EngineeringProject>,
    modifier: Modifier = Modifier,
    onCategorySelected: (CategoryMeta) -> Unit = {}
) {
    var activeCategory by remember { mutableStateOf<String?>(null) }

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
                categoryDescription = "Versiyon kontrolü, Linux ortamı, siber güvenlik ve yazılım mühendisliği ilkeleri.",
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
                categorySubtitle = "Dil Yetenekleri & Sürekli Gelişim",
                categoryDescription = "Akıcı teknik İngilizce ve ikinci yabancı dil öğrenme süreçleri.",
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
                categorySubtitle = "Açık Kaynak, Yayınlar & Görünürlük",
                categoryDescription = "GitHub depoları, Play Store uygulamaları, Medium yazıları ve öne çıkan projeler.",
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
            subtitle = "Dil Yetenekleri & Alışkanlıklar",
            description = "Akıcı B2+ teknik İngilizce, ikinci yabancı dil, sürekli öğrenme disiplini ve zihin modelleri.",
            accentColor = AccentAmber,
            tags = listOf("İngilizce", "2. Yabancı Dil", "Winter Arc"),
            branchIds = listOf(BranchId.ENGLISH, BranchId.SECOND_LANGUAGE, BranchId.KNOWLEDGE_MANAGEMENT)
        ),
        CategoryMeta(
            id = "cat_portfolio",
            title = "PORTFÖY",
            emoji = "🚀",
            subtitle = "Gerçek Dünya Projeleri & Çıktılar",
            description = "GitHub açık kaynak projeleri, Play Store uygulamaları, Medium yazıları, LinkedIn ve öne çıkan projeler.",
            accentColor = BranchPortfolio,
            tags = listOf("GitHub", "Play Store", "Medium", "LinkedIn", "Projeler"),
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
            val relevantSkills = skills.filter { it.branchId in cat.branchIds }
            val completedSkills = relevantSkills.count { it.status.isCompletedOrMastered }
            val totalSkills = if (cat.id == "cat_portfolio") projects.size else relevantSkills.size

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

                    // Tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
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

                        if (totalSkills > 0) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = if (cat.id == "cat_portfolio") "$totalSkills Proje" else "$completedSkills/$totalSkills Yetenek",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (completedSkills > 0) cat.accentColor else TextDarkMuted,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}
