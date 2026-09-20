package com.example.ui.components.school

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun SchoolScheduleView(
    courses: List<SchoolCourse>,
    onBack: () -> Unit,
    onOpenAttendance: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val daysOfWeek = listOf("Tümü", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma")

    val todayName = remember {
        when (LocalDate.now().dayOfWeek) {
            DayOfWeek.MONDAY -> "Pazartesi"
            DayOfWeek.TUESDAY -> "Salı"
            DayOfWeek.WEDNESDAY -> "Çarşamba"
            DayOfWeek.THURSDAY -> "Perşembe"
            DayOfWeek.FRIDAY -> "Cuma"
            DayOfWeek.SATURDAY -> "Cumartesi"
            DayOfWeek.SUNDAY -> "Pazar"
            null -> ""
        }
    }

    // Default to today if it's a weekday, otherwise "Tümü"
    var selectedDay by remember {
        mutableStateOf(if (daysOfWeek.contains(todayName)) todayName else "Tümü")
    }

    val totalCredits = courses.sumOf { it.credits }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
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

        // Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentCyan.copy(alpha = 0.15f))
                                    .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🗓️", fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "HAFTALIK DERS PROGRAMI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "BANÜ Bilgisayar Mühendisliği (4. Sınıf)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "$totalCredits Kredi",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Day Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(daysOfWeek) { day ->
                            val isSelected = selectedDay == day
                            val isToday = day == todayName
                            val dayCoursesCount = if (day == "Tümü") courses.size else courses.count { it.dayOfWeek == day }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AccentCyan.copy(alpha = 0.22f) else PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) AccentCyan else if (isToday) AccentAmber.copy(alpha = 0.6f) else BorderSubtle
                                ),
                                modifier = Modifier.clickable { selectedDay = day }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                                ) {
                                    if (isToday) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(AccentAmber)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }

                                    Text(
                                        text = if (day == "Tümü") "Tümü ($dayCoursesCount)" else "$day ($dayCoursesCount)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) AccentCyan else if (isToday) AccentAmber else TextSecondary,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Schedule Content
        if (selectedDay == "Tümü") {
            val weekdays = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma")
            weekdays.forEach { day ->
                val dayCourses = courses.filter { it.dayOfWeek == day }
                val isToday = day == todayName

                item(key = "day_header_$day") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = day.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) AccentAmber else AccentCyan,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 12.sp
                                )
                            )
                            if (isToday) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentAmber.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "BUGÜN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AccentAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (dayCourses.isNotEmpty()) "${dayCourses.size} Ders" else "Ders Yok",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (dayCourses.isEmpty()) {
                    item(key = "empty_$day") {
                        FreeDayCard(day = day)
                    }
                } else {
                    items(dayCourses, key = { "full_${day}_${it.id}" }) { course ->
                        ScheduleCourseCard(
                            course = course,
                            onOpenAttendance = { onOpenAttendance(course.id) }
                        )
                    }
                }
            }
        } else {
            val dayCourses = courses.filter { it.dayOfWeek == selectedDay }
            if (dayCourses.isEmpty()) {
                item {
                    FreeDayCard(day = selectedDay)
                }
            } else {
                items(dayCourses, key = { it.id }) { course ->
                    ScheduleCourseCard(
                        course = course,
                        onOpenAttendance = { onOpenAttendance(course.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleCourseCard(
    course: SchoolCourse,
    onOpenAttendance: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Time Slot & Online/Classroom Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.timeSlot.ifBlank { "Saat belirtilmedi" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            fontSize = 13.5.sp
                        )
                    )
                }

                if (course.isOnline) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF6366F1).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🌐 U.Ö. Online",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFA5B4FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PanelNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = "${course.credits} Kredi",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Course Code & Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (course.code.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = course.code,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details: Instructor & Classroom
            if (course.instructor.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 1.5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.instructor,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            if (course.classroom.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 1.5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.classroom,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle.copy(alpha = 0.6f)))
            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Absence status & quick jump
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val isFailed = course.isFailedDueToAbsence
                val isWarning = course.isLastAbsenceWarning

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Devamsızlık: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.5.sp)
                    )
                    Text(
                        text = when {
                            isFailed -> "🚨 KALDI (${course.absentCount}/4)"
                            isWarning -> "⚠️ SON HAK (4/4)"
                            else -> "${course.absentCount} / 4 Hak"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isFailed -> Color(0xFFEF4444)
                                isWarning -> AccentAmber
                                else -> AccentEmerald
                            },
                            fontSize = 11.5.sp
                        )
                    )
                }

                FilledTonalButton(
                    onClick = onOpenAttendance,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PanelNavy,
                        contentColor = AccentCyan
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Yoklama Çizelgesi", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FreeDayCard(day: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavy)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentEmerald.copy(alpha = 0.15f))
                    .border(1.dp, AccentEmerald.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌲", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "$day - Ders Yok / Serbest Çalışma Günü",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.5.sp
                    )
                )
                Text(
                    text = "Winter Arc projelerine, bitirme araştırmasına veya portföy çalışmalarına odaklanabilirsin.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
