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
import androidx.compose.runtime.Composable
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

@Composable
fun CategoriesDashboardView(
    skills: List<SkillNode>,
    projects: List<EngineeringProject>,
    modifier: Modifier = Modifier,
    onCategorySelected: (CategoryMeta) -> Unit = {}
) {
    val categories = listOf(
        CategoryMeta(
            id = "cat_school",
            title = "OKUL",
            emoji = "🎓",
            subtitle = "Bilgisayar Mühendisliği & Temel Bilimler",
            description = "Üniversite ders müfredatı, veri yapıları & algoritmalar, bitirme tezi ve akademik temeller.",
            accentColor = BranchCS,
            tags = listOf("Veri Yapıları", "Algoritmalar", "Bitirme Tezi", "Matematik"),
            branchIds = listOf(BranchId.COMPUTER_SCIENCE, BranchId.GRADUATION_PROJECT, BranchId.CERTIFICATES)
        ),
        CategoryMeta(
            id = "cat_career",
            title = "KARİYER",
            emoji = "💼",
            subtitle = "Sektör Odaklı Uzmanlık & Mimari",
            description = ".NET Backend, Mikroservisler, DevOps, Docker & K8s, Native Android ve Güvenlik pratikleri.",
            accentColor = BranchDotNet,
            tags = listOf(".NET / C#", "DevOps & K8s", "Android Compose", "Siber Güvenlik"),
            branchIds = listOf(BranchId.BACKEND_DOTNET, BranchId.DISTRIBUTED_DEVOPS, BranchId.ANDROID_MOBILE, BranchId.CYBER_SECURITY)
        ),
        CategoryMeta(
            id = "cat_tech_culture",
            title = "TEKNİK GENEL KÜLTÜR",
            emoji = "🧠",
            subtitle = "Sistem Mantığı & Başucu Kitapları",
            description = "İşletim sistemleri (OSTEP), Donanım & Bellek (CS:APP), Dağıtık Sistemler (DDIA) ve Ağ protokolleri.",
            accentColor = BranchTools,
            tags = listOf("OSTEP", "CS:APP", "DDIA", "Linux & CLI", "RFC & Protokoller"),
            branchIds = listOf(BranchId.ENGINEERING_TOOLS)
        ),
        CategoryMeta(
            id = "cat_personal_dev",
            title = "KİŞİSEL GELİŞİM",
            emoji = "🌱",
            subtitle = "Dil, Zihin Modelleri & Alışkanlıklar",
            description = "Akıcı B2+ teknik İngilizce, ikinci yabancı dil, Recursive Learning Loop ve sürekli öğrenme disiplini.",
            accentColor = AccentAmber,
            tags = listOf("B2+ İngilizce", "2. Dil", "Learning Loop", "Winter Arc"),
            branchIds = listOf(BranchId.ENGLISH, BranchId.SECOND_LANGUAGE, BranchId.KNOWLEDGE_MANAGEMENT)
        ),
        CategoryMeta(
            id = "cat_portfolio",
            title = "PORTFÖY",
            emoji = "🚀",
            subtitle = "Gerçek Dünya Projeleri & Çıktılar",
            description = "Üretime hazır backend servisleri, KV Cache optimizasyonu, Medium makaleleri ve GitHub depoları.",
            accentColor = BranchPortfolio,
            tags = listOf("Eczane Sistemi", "KV Cache C++", "Android SkillTree", "Medium"),
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
                    .clickable { onCategorySelected(cat) },
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
