package com.example.ui.components.school

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SchoolCourseRepository
import com.example.data.model.school.*
import com.example.ui.components.ThemeToggleButton
import com.example.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

enum class ScheduleViewMode {
    DAILY_FLOW,   // 📋 Günlük Akış (Görsel 3 Tarzı Gün Hapları & Kartlar)
    WEEKLY_GRID   // 📊 Haftalık Matris (Görsel 2 Tarzı Zaman Tablosu)
}

data class DayPillInfo(
    val dayName: String,
    val dayNumber: String,
    val fullDayName: String,
    val isToday: Boolean
)

@Composable
fun SchoolScheduleHubView(
    onBack: (() -> Unit)? = null,
    onOpenThemePicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var courses by remember { mutableStateOf(SchoolCourseRepository.getCourses(context)) }
    var viewMode by remember { mutableStateOf(ScheduleViewMode.DAILY_FLOW) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var selectedCourseForDetail by remember { mutableStateOf<SchoolCourse?>(null) }

    // Day calculation
    val today = remember { LocalDate.now() }
    val todayDayOfWeek = today.dayOfWeek

    val todayNameTr = remember {
        when (todayDayOfWeek) {
            DayOfWeek.MONDAY -> "Pazartesi"
            DayOfWeek.TUESDAY -> "Salı"
            DayOfWeek.WEDNESDAY -> "Çarşamba"
            DayOfWeek.THURSDAY -> "Perşembe"
            DayOfWeek.FRIDAY -> "Cuma"
            DayOfWeek.SATURDAY -> "Cumartesi"
            DayOfWeek.SUNDAY -> "Pazar"
            null -> "Pazartesi"
        }
    }

    // Prepare 5 weekdays for selector
    val weekDays = remember {
        val startOfWeek = today.minusDays((todayDayOfWeek.value - 1).toLong())
        listOf(
            DayPillInfo("Pzt", startOfWeek.dayOfMonth.toString(), "Pazartesi", todayDayOfWeek == DayOfWeek.MONDAY),
            DayPillInfo("Sal", startOfWeek.plusDays(1).dayOfMonth.toString(), "Salı", todayDayOfWeek == DayOfWeek.TUESDAY),
            DayPillInfo("Çar", startOfWeek.plusDays(2).dayOfMonth.toString(), "Çarşamba", todayDayOfWeek == DayOfWeek.WEDNESDAY),
            DayPillInfo("Per", startOfWeek.plusDays(3).dayOfMonth.toString(), "Perşembe", todayDayOfWeek == DayOfWeek.THURSDAY),
            DayPillInfo("Cum", startOfWeek.plusDays(4).dayOfMonth.toString(), "Cuma", todayDayOfWeek == DayOfWeek.FRIDAY)
        )
    }

    var selectedDayName by remember {
        mutableStateOf(if (weekDays.any { it.fullDayName == todayNameTr }) todayNameTr else "Pazartesi")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back Button Bar if navigated from SchoolHub
        if (onBack != null) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
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
                        text = "Okul Paneline Dön",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = AccentCyan
                        )
                    )
                }
            }
        }

        // Top Header Row: Title & Theme Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DERS PROGRAMI & TAKVİM",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Haftalık ders saatleri ve akademik zaman tablosu.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
            }
        }

        // View Mode Switcher (Günlük Akış vs Haftalık Matris)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PanelNavy)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                // Mode 0: Günlük Akış
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (viewMode == ScheduleViewMode.DAILY_FLOW) AccentCyan else Color.Transparent)
                        .clickable { viewMode = ScheduleViewMode.DAILY_FLOW }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ViewAgenda,
                            contentDescription = null,
                            tint = if (viewMode == ScheduleViewMode.DAILY_FLOW) Color.Black else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Günlük Akış",
                            fontSize = 12.sp,
                            fontWeight = if (viewMode == ScheduleViewMode.DAILY_FLOW) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == ScheduleViewMode.DAILY_FLOW) Color.Black else TextSecondary
                        )
                    }
                }

                // Mode 1: Haftalık Matris
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (viewMode == ScheduleViewMode.WEEKLY_GRID) AccentCyan else Color.Transparent)
                        .clickable { viewMode = ScheduleViewMode.WEEKLY_GRID }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarViewWeek,
                            contentDescription = null,
                            tint = if (viewMode == ScheduleViewMode.WEEKLY_GRID) Color.Black else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Haftalık Matris",
                            fontSize = 12.sp,
                            fontWeight = if (viewMode == ScheduleViewMode.WEEKLY_GRID) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == ScheduleViewMode.WEEKLY_GRID) Color.Black else TextSecondary
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // VIEW MODE 1: GÜNLÜK AKIŞ (AJANDA)
        // -------------------------------------------------------------
        if (viewMode == ScheduleViewMode.DAILY_FLOW) {
            // Horizontal Date / Day Selector Pills (Image 3 style: media_1789948414867.png)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    weekDays.forEach { dayInfo ->
                        val isSelected = selectedDayName == dayInfo.fullDayName

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) AccentCyan else PanelNavyElevated,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentCyan.copy(alpha = 0.8f) else BorderSubtle
                            ),
                            shadowElevation = if (isSelected) 6.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clickable { selectedDayName = dayInfo.fullDayName }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayInfo.dayNumber,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dayInfo.dayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.8f) else TextDarkMuted
                                )
                                if (dayInfo.isToday) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.Black else AccentCyan)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Courses List for Selected Day
            val dayCourses = courses.filter { it.dayOfWeek.equals(selectedDayName, ignoreCase = true) }

            if (dayCourses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(AccentEmerald.copy(alpha = 0.12f))
                                    .border(1.dp, AccentEmerald.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "$selectedDayName Günü Dersin Yok",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dinlenme, proje geliştirme veya kişisel odak için harika bir gün! ☕",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(dayCourses, key = { it.id }) { course ->
                    DailyScheduleCourseCard(
                        course = course,
                        onClick = { selectedCourseForDetail = course }
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // VIEW MODE 2: HAFTALIK MATRİS (ZAMAN TABLOSU - GÖRSEL 2)
        // -------------------------------------------------------------
        if (viewMode == ScheduleViewMode.WEEKLY_GRID) {
            item {
                WeeklyTimetableMatrix(
                    courses = courses,
                    onCourseClick = { selectedCourseForDetail = it }
                )
            }

            // Image 2 style: "+ Add New Schedule" Dashed/Glowing Button
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AccentCyan.copy(alpha = 0.12f),
                    border = BorderStroke(1.2.dp, AccentCyan.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAddDialogOpen = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Yeni Ders / Program Ekle",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            }
        }
    }

    // Add Course Dialog
    if (isAddDialogOpen) {
        AddNewCourseDialog(
            onDismiss = { isAddDialogOpen = false },
            onAddCourse = { name, code, credits, semester, instructor, classroom, dayOfWeek, timeSlot, isOnline ->
                val newCourse = SchoolCourse(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    code = code,
                    credits = credits,
                    semester = semester,
                    instructor = instructor,
                    classroom = classroom,
                    dayOfWeek = dayOfWeek,
                    timeSlot = timeSlot,
                    isOnline = isOnline
                )
                courses = SchoolCourseRepository.addCourse(context, newCourse)
                isAddDialogOpen = false
            }
        )
    }

    // Course Detail Sheet
    selectedCourseForDetail?.let { course ->
        CourseQuickDetailSheet(
            course = course,
            onDismiss = { selectedCourseForDetail = null }
        )
    }
}

// -------------------------------------------------------------
// DAILY SCHEDULE CARD (GÜNLÜK DERS KARTI)
// -------------------------------------------------------------
@Composable
private fun DailyScheduleCourseCard(
    course: SchoolCourse,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Time Slot Pill Box
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentCyan.copy(alpha = 0.12f))
                        .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val parts = course.timeSlot.split("-").map { it.trim() }
                        Text(
                            text = parts.getOrNull(0) ?: course.timeSlot,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        if (parts.size > 1) {
                            Text(
                                text = parts[1],
                                fontSize = 10.sp,
                                color = AccentCyan.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.displayTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (course.isOnline) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentAmber.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Language, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "U.Ö. (Online)", fontSize = 9.5.sp, color = AccentAmber, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        } else if (course.classroom.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PanelNavy
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Room, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = course.cleanClassroom, fontSize = 9.5.sp, color = AccentEmerald, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        if (course.instructor.isNotBlank()) {
                            Text(
                                text = course.instructor,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkMuted,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextDarkMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// WEEKLY TIMETABLE MATRIX (HAFTALIK MATRİS - GÖRSEL 2 STİLİ)
// -------------------------------------------------------------
@Composable
private fun WeeklyTimetableMatrix(
    courses: List<SchoolCourse>,
    onCourseClick: (SchoolCourse) -> Unit
) {
    val weekDays = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma")
    val dayLabels = listOf("Pzt", "Sal", "Çar", "Per", "Cum")
    val timeSlots = listOf(
        "08:00", "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "HAFTALIK DERS TABLOSU",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Table Header: Time label + 5 Days
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saat",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDarkMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.Center
                )

                dayLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.height(6.dp))

            // Time Slots Rows
            timeSlots.forEach { slotHour ->
                val hourInt = slotHour.substringBefore(":").toIntOrNull() ?: 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour label
                    Text(
                        text = slotHour,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextDarkMuted,
                            fontSize = 9.5.sp
                        ),
                        modifier = Modifier.width(42.dp),
                        textAlign = TextAlign.Center
                    )

                    // 5 Day Columns
                    weekDays.forEach { dayName ->
                        val matchedCourse = courses.find { c ->
                            c.dayOfWeek.equals(dayName, ignoreCase = true) &&
                                    isCourseInHourSlot(c.timeSlot, hourInt)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(1.dp)
                                .border(0.5.dp, BorderSubtle.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (matchedCourse != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (matchedCourse.isOnline) AccentAmber.copy(alpha = 0.25f) else AccentCyan.copy(alpha = 0.22f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (matchedCourse.isOnline) AccentAmber.copy(alpha = 0.6f) else AccentCyan.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { onCourseClick(matchedCourse) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = if (matchedCourse.code.isNotBlank()) matchedCourse.code else matchedCourse.name.take(6),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (matchedCourse.isOnline) AccentAmber else AccentCyan
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
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

// Helper to determine if a course timeSlot covers a given hour
private fun isCourseInHourSlot(timeSlot: String, slotHour: Int): Boolean {
    if (timeSlot.isBlank()) return false
    val parts = timeSlot.split("-").map { it.trim() }
    val startHour = parts.getOrNull(0)?.substringBefore(":")?.toIntOrNull() ?: return false
    val endHour = parts.getOrNull(1)?.substringBefore(":")?.toIntOrNull() ?: (startHour + 2)
    return slotHour in startHour..endHour
}

// -------------------------------------------------------------
// COURSE QUICK DETAIL BOTTOM SHEET
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseQuickDetailSheet(
    course: SchoolCourse,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                color = BorderSubtle,
                shape = CircleShape
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.displayTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${course.semester}. Dönem • ${course.credits} AKTS",
                        style = MaterialTheme.typography.labelSmall.copy(color = AccentCyan, fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PanelNavy,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow(label = "Gün & Saat", value = "${course.dayOfWeek} • ${course.timeSlot}")
                    DetailRow(label = "Derslik", value = if (course.isOnline) "Uzaktan Eğitim (Online)" else course.cleanClassroom)
                    DetailRow(label = "Öğretim Üyesi", value = if (course.instructor.isNotBlank()) course.instructor else "Belirtilmemiş")
                    DetailRow(label = "Devamsızlık Durumu", value = "${course.absentCount} / 4 Hafta (Kalan Hak: ${course.remainingAbsenceRights})")
                    DetailRow(label = "Harf Notu / Durum", value = course.letterGrade)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextDarkMuted, fontSize = 11.5.sp))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 11.5.sp))
    }
}
