package com.example.ui.components.school

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SchoolCourseRepository
import com.example.ui.theme.*

enum class SchoolSubScreen {
    OVERVIEW,
    COURSES,
    SCHEDULE,
    GRADUATION_PROJECT,
    BOOKS
}

@Composable
fun SchoolHubView(
    onBackToCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSubScreen by remember { mutableStateOf(SchoolSubScreen.OVERVIEW) }
    var initialCoursesTab by remember { mutableStateOf(0) }
    var activeAttendanceCourseId by remember { mutableStateOf<String?>(null) }

    BackHandler {
        if (currentSubScreen != SchoolSubScreen.OVERVIEW) {
            currentSubScreen = SchoolSubScreen.OVERVIEW
        } else {
            onBackToCategories()
        }
    }

    when (currentSubScreen) {
        SchoolSubScreen.COURSES -> {
            SchoolCoursesView(
                onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW },
                modifier = modifier,
                initialTab = initialCoursesTab,
                initialAttendanceCourseId = activeAttendanceCourseId
            )
            return
        }
        SchoolSubScreen.SCHEDULE -> {
            val context = LocalContext.current
            val courses = remember(context) { SchoolCourseRepository.getCourses(context) }
            SchoolScheduleView(
                courses = courses,
                onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW },
                onOpenAttendance = { courseId ->
                    activeAttendanceCourseId = courseId
                    initialCoursesTab = 0
                    currentSubScreen = SchoolSubScreen.COURSES
                },
                modifier = modifier
            )
            return
        }
        SchoolSubScreen.GRADUATION_PROJECT -> {
            GraduationProjectDetailView(
                onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW },
                modifier = modifier
            )
            return
        }
        SchoolSubScreen.BOOKS -> {
            SchoolBooksView(
                onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW },
                modifier = modifier
            )
            return
        }
        SchoolSubScreen.OVERVIEW -> {
            // Render Hub Overview Menu
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CanvasDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasDark)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Back Button Bar
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
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            // Hero School Category Card
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
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                tint = BranchCS,
                                modifier = Modifier.size(28.dp)
                            )
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
                    icon = Icons.Outlined.MenuBook,
                    title = "Okul Dersleri",
                    subtitle = "Müfredat, Krediler & Not Takibi",
                    description = "Dönemlik aldığın dersler, krediler, harf notları ve 16 haftalık devamsızlık durumu.",
                    tag = "Ders Ekle / Yönet",
                    accentColor = AccentCyan,
                    onClick = {
                        initialCoursesTab = 0
                        activeAttendanceCourseId = null
                        currentSubScreen = SchoolSubScreen.COURSES
                    }
                )
            }

            // Option 2: Haftalık Ders Programı
            item {
                SchoolOptionCard(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Haftalık Ders Programı",
                    subtitle = "Derslikler, Saatler & U.Ö.",
                    description = "Pazartesi, Salı ve Çarşamba ders saatleri, derslik kodları ve online ders bilgisi.",
                    tag = "Programı Gör",
                    accentColor = AccentEmerald,
                    onClick = { currentSubScreen = SchoolSubScreen.SCHEDULE }
                )
            }

            // Option 3: Bitirme Projesi
            item {
                SchoolOptionCard(
                    icon = Icons.Outlined.School,
                    title = "Bitirme Projesi",
                    subtitle = "Mezuniyet & Araştırma Projesi",
                    description = "Proje konusu, danışman bilgisi, sistem mimarisi ve tez adımları daha sonra belirlenecektir.",
                    tag = "Sonradan Eklenecek",
                    accentColor = AccentAmber,
                    onClick = { currentSubScreen = SchoolSubScreen.GRADUATION_PROJECT }
                )
            }

            // Option 4: CS Kitapları
            item {
                SchoolOptionCard(
                    icon = Icons.Outlined.AutoStories,
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

@Composable
private fun SchoolOptionCard(
    title: String,
    subtitle: String,
    description: String,
    tag: String,
    accentColor: Color,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    emoji: String = ""
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
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text(text = emoji, fontSize = 22.sp)
                        }
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
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
