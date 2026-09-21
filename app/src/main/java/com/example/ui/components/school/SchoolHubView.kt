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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        SchoolSubScreen.SCHEDULE -> {
            SchoolScheduleHubView(
                onBack = { currentSubScreen = SchoolSubScreen.OVERVIEW },
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
            // Render Command Dashboard Menu
        }
    }

    val totalCredits = remember(courses) { courses.sumOf { it.credits } }
    val isAnyFailed = remember(courses) { courses.any { it.isFailedDueToAbsence } }
    val isAnyWarning = remember(courses) { courses.any { it.isLastAbsenceWarning } }

    val cyanColor = AccentCyan
    val emeraldColor = AccentEmerald
    val amberColor = AccentAmber
    val toolsColor = BranchTools

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

                    // Semester Credits & Courses Badge
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
                                    .background(emeraldColor)
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

            // PRIMARY HERO BENTO CARD: Okul Dersleri & Notlar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, cyanColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .drawBehind {
                            // Subtle radial glow flare in the top right
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(cyanColor.copy(alpha = 0.12f), Color.Transparent),
                                    center = Offset(size.width * 0.9f, size.height * 0.15f),
                                    radius = size.width * 0.65f
                                )
                            )
                        }
                        .clickable {
                            initialCoursesTab = 0
                            activeAttendanceCourseId = null
                            currentSubScreen = SchoolSubScreen.COURSES
                        },
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AccentCyan.copy(alpha = 0.15f))
                                        .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "Okul Dersleri",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${courses.size} Ders • Notlar & 14 Hafta Yoklama",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AccentCyan.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Yönet",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AccentCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Course Chips Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            courses.take(5).forEach { c ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PanelNavy,
                                    border = BorderStroke(1.dp, BorderSubtle),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = c.code.ifBlank { c.name.take(4) },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status Info Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val statusDotColor = when {
                                    isAnyFailed -> StatusFailed
                                    isAnyWarning -> AccentAmber
                                    else -> AccentEmerald
                                }
                                val statusText = when {
                                    isAnyFailed -> "Kritik Devamsızlık Sınırı Aşıldı"
                                    isAnyWarning -> "Son Devamsızlık Uyarısı"
                                    else -> "Yoklama Durumu Güvenli"
                                }
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(statusDotColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = statusDotColor,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
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
            }

            // 2-COLUMN ASYMMETRIC BENTO GRID: Haftalık Program & Bitirme Tezi
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Col 1: Haftalık Ders Programı
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, emeraldColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(emeraldColor.copy(alpha = 0.08f), Color.Transparent),
                                        center = Offset(size.width * 0.8f, size.height * 0.2f),
                                        radius = size.width * 0.8f
                                    )
                                )
                            }
                            .clickable { currentSubScreen = SchoolSubScreen.SCHEDULE },
                        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(emeraldColor.copy(alpha = 0.15f))
                                    .border(1.dp, emeraldColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = emeraldColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Ders Programı",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Günlük & Matris",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = emeraldColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, emeraldColor.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "Pzt – Çar Akışı",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = emeraldColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Col 2: Bitirme Tezi
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, amberColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(amberColor.copy(alpha = 0.08f), Color.Transparent),
                                        center = Offset(size.width * 0.8f, size.height * 0.2f),
                                        radius = size.width * 0.8f
                                    )
                                )
                            }
                            .clickable { currentSubScreen = SchoolSubScreen.GRADUATION_PROJECT },
                        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(amberColor.copy(alpha = 0.15f))
                                    .border(1.dp, amberColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.School,
                                    contentDescription = null,
                                    tint = amberColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Bitirme Tezi",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "C-E Graph Sistemi",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = amberColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, amberColor.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "%70 Tamamlandı",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = amberColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM BENTO CARD: CS Kitaplığı
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, toolsColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(toolsColor.copy(alpha = 0.08f), Color.Transparent),
                                    center = Offset(size.width * 0.9f, size.height * 0.3f),
                                    radius = size.width * 0.6f
                                )
                            )
                        }
                        .clickable { currentSubScreen = SchoolSubScreen.BOOKS },
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
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BranchTools.copy(alpha = 0.15f))
                                        .border(1.dp, BranchTools.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoStories,
                                        contentDescription = null,
                                        tint = BranchTools,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "CS Başucu Kitaplığı",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "4 Temel Eser • Sistemler, Mimari & Ağlar",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Book Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("OSTEP", "CS:APP", "DDIA", "Networks").forEach { book ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PanelNavy,
                                    border = BorderStroke(1.dp, BorderSubtle),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = book,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = BranchTools,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
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
    }
}
