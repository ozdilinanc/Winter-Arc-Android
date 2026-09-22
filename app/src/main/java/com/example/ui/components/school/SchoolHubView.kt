package com.example.ui.components.school

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
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
import com.example.data.repository.SchoolCourseRepository
import com.example.data.model.school.*
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
    val context = LocalContext.current
    var courses by remember {
        mutableStateOf(SchoolCourseRepository.getCourses(context))
    }

    var currentSubScreen by remember { mutableStateOf(SchoolSubScreen.OVERVIEW) }
    var initialCoursesTab by remember { mutableStateOf(0) }
    var activeAttendanceCourseId by remember { mutableStateOf<String?>(null) }

    // Whenever returning to OVERVIEW, refresh courses
    LaunchedEffect(currentSubScreen) {
        if (currentSubScreen == SchoolSubScreen.OVERVIEW) {
            courses = SchoolCourseRepository.getCourses(context)
        }
    }

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
            // Render Clean Harmonious iOS Menu
        }
    }

    val totalCredits = remember(courses) { courses.sumOf { it.credits } }
    val isAnyFailed = remember(courses) { courses.any { it.isFailedDueToAbsence } }
    val isAnyWarning = remember(courses) { courses.any { it.isLastAbsenceWarning } }

    val cyanColor = AccentCyan
    val emeraldColor = AccentEmerald
    val amberColor = AccentAmber

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
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Executive Header Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PanelNavyElevated,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onBackToCategories() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Geri",
                                    tint = cyanColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "OKUL",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "7. Dönem • Bilgisayar Mühendisliği",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Semester Summary Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PanelNavy,
                        border = BorderStroke(1.dp, cyanColor.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isAnyFailed) StatusFailed else if (isAnyWarning) AccentAmber else emeraldColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${courses.size} Ders • $totalCredits AKTS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Quick Status Pill Strip
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PanelNavyElevated,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = if (isAnyFailed) StatusFailed else if (isAnyWarning) AccentAmber else emeraldColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isAnyFailed -> "Kritik Devamsızlık Limiti Aşıldı"
                                    isAnyWarning -> "Son Devamsızlık Uyarısı Mevcut"
                                    else -> "14 Hafta Devamsızlık Durumu Güvenli"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isAnyFailed) StatusFailed else if (isAnyWarning) AccentAmber else TextSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Text(
                            text = "Vize & Final Muaf",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextDarkMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }

            // Clean Menu Section Title
            item {
                Text(
                    text = "AKADEMİK MODÜLLER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                )
            }

            // MENU ITEM 1: Okul Dersleri
            item {
                SchoolHubMenuItemCard(
                    title = "Okul Dersleri",
                    subtitle = "Müfredat, Not Girişi & 16 Hafta Yoklama",
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    accentColor = cyanColor,
                    primaryBadgeText = "${courses.size} Aktif Ders",
                    secondaryBadgeText = if (isAnyFailed) "Kaldı" else if (isAnyWarning) "Uyarı" else "Güvenli",
                    secondaryBadgeColor = if (isAnyFailed) StatusFailed else if (isAnyWarning) AccentAmber else emeraldColor,
                    onClick = {
                        initialCoursesTab = 0
                        activeAttendanceCourseId = null
                        currentSubScreen = SchoolSubScreen.COURSES
                    }
                )
            }

            // MENU ITEM 2: CS Başucu Kitapları
            item {
                SchoolHubMenuItemCard(
                    title = "CS Başucu Kitapları",
                    subtitle = "OSTEP, CS:APP, DDIA, Ağlar derin okuma takibi",
                    icon = Icons.Outlined.AutoStories,
                    accentColor = emeraldColor,
                    primaryBadgeText = "4 Temel Eser",
                    secondaryBadgeText = "Kitaplık Takibi",
                    secondaryBadgeColor = emeraldColor,
                    onClick = {
                        currentSubScreen = SchoolSubScreen.BOOKS
                    }
                )
            }

            // MENU ITEM 3: Bitirme Projesi
            item {
                SchoolHubMenuItemCard(
                    title = "Bitirme Projesi",
                    subtitle = "Mühendislik Tezi, Danışman & Kilometre Taşları",
                    icon = Icons.Outlined.Code,
                    accentColor = amberColor,
                    primaryBadgeText = "C-E Graph Sistemi",
                    secondaryBadgeText = "%70 Tamamlandı",
                    secondaryBadgeColor = amberColor,
                    onClick = {
                        currentSubScreen = SchoolSubScreen.GRADUATION_PROJECT
                    }
                )
            }
        }
    }
}

@Composable
private fun SchoolHubMenuItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    primaryBadgeText: String,
    secondaryBadgeText: String? = null,
    secondaryBadgeColor: Color = AccentEmerald,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Themed Icon Badge in Harmonious System Styling
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(accentColor.copy(alpha = 0.14f))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle: Title, Subtitle, Badges
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Aç",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PanelNavy,
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = primaryBadgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (secondaryBadgeText != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = secondaryBadgeColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, secondaryBadgeColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(secondaryBadgeColor)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = secondaryBadgeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = secondaryBadgeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
