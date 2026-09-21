package com.example.ui.components.school

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Room
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SchoolCourseRepository
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun SchoolCoursesView(
    onBack: () -> Unit,
    initialTab: Int = 0,
    initialAttendanceCourseId: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var courses by remember {
        mutableStateOf(SchoolCourseRepository.getCourses(context))
    }

    var selectedViewTab by remember { mutableStateOf(initialTab) } // 0: Dersler & Notlar, 1: Haftalık Program
    var activeAttendanceCourseId by remember { mutableStateOf(initialAttendanceCourseId) }
    var activeGradeDialogCourseId by remember { mutableStateOf<String?>(null) }
    var activeWeightsDialogCourseId by remember { mutableStateOf<String?>(null) }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    // Optional semester filter (0 = all)
    var selectedSemesterFilter by remember { mutableStateOf(0) }

    // System Back handling
    BackHandler {
        if (activeAttendanceCourseId != null) {
            activeAttendanceCourseId = null
        } else if (selectedViewTab != 0) {
            selectedViewTab = 0
        } else {
            onBack()
        }
    }

    // 16-WEEK ATTENDANCE SCREEN
    if (activeAttendanceCourseId != null) {
        val course = courses.find { it.id == activeAttendanceCourseId }
        if (course != null) {
            CourseAttendanceDetailView(
                course = course,
                onUpdateCourse = { updated ->
                    courses = SchoolCourseRepository.updateCourse(context, updated)
                },
                onBack = { activeAttendanceCourseId = null },
                modifier = modifier
            )
            return
        }
    }



    val filteredCourses = if (selectedSemesterFilter == 0) {
        courses
    } else {
        courses.filter { it.semester == selectedSemesterFilter }
    }

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

        // Header & Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OKUL DERSLERİ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Derslerini, notlarını ve 16 haftalık yoklamanı yönet",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                Button(
                    onClick = { isAddDialogOpen = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ders Ekle",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }

        // View Mode Switcher: "Dersler & Notlar" vs "Haftalık Program"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PanelNavy)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedViewTab == 0) AccentCyan else Color.Transparent)
                        .clickable { selectedViewTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = if (selectedViewTab == 0) Color.White else TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dersler & Notlar",
                            fontSize = 12.sp,
                            fontWeight = if (selectedViewTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedViewTab == 0) Color.White else TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedViewTab == 1) AccentCyan else Color.Transparent)
                        .clickable { selectedViewTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (selectedViewTab == 1) Color.White else TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Yoklama & Devamsızlık",
                            fontSize = 12.sp,
                            fontWeight = if (selectedViewTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedViewTab == 1) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // Stats Summary Card
        if (courses.isNotEmpty()) {
            item {
                val totalCredits = courses.sumOf { it.credits }
                val totalAbsent = courses.sumOf { it.absentCount }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${courses.size} Ders",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 16.sp
                            )
                            Text(text = "Toplam Kayıtlı", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalCredits AKTS",
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontSize = 16.sp
                            )
                            Text(text = "Dönem Kredisi", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$totalAbsent Hafta",
                                fontWeight = FontWeight.Bold,
                                color = if (totalAbsent > 0) AccentAmber else AccentEmerald,
                                fontSize = 16.sp
                            )
                            Text(text = "Toplam Devamsızlık", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                        }
                    }
                }
            }

            // Semester Filter Chips (if multiple semesters exist)
            val availableSemesters = courses.map { it.semester }.distinct().sorted()
            if (availableSemesters.size > 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SemesterFilterChip(
                            label = "Tümü (${courses.size})",
                            isSelected = selectedSemesterFilter == 0,
                            onClick = { selectedSemesterFilter = 0 }
                        )
                        availableSemesters.forEach { sem ->
                            val count = courses.count { it.semester == sem }
                            SemesterFilterChip(
                                label = "$sem. Dönem ($count)",
                                isSelected = selectedSemesterFilter == sem,
                                onClick = { selectedSemesterFilter = sem }
                            )
                        }
                    }
                }
            }
        }

        // Empty State
        if (courses.isEmpty()) {
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
                            .padding(vertical = 40.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Henüz Ders Eklenmemiş",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Dönemlik derslerini, kredilerini ve ders saatlerini ekleyerek başla.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { isAddDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "İlk Dersi Ekle", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Courses List
        items(filteredCourses, key = { it.id }) { course ->
            if (selectedViewTab == 0) {
                CourseCardItem(
                    course = course,
                    onOpenAttendance = { activeAttendanceCourseId = course.id },
                    onOpenGrades = { activeGradeDialogCourseId = course.id },
                    onOpenWeights = { activeWeightsDialogCourseId = course.id },
                    onDelete = { courses = SchoolCourseRepository.deleteCourse(context, course.id) }
                )
            } else {
                CourseAttendanceOverviewCard(
                    course = course,
                    onOpenAttendance = { activeAttendanceCourseId = course.id }
                )
            }
        }
    }

    // GRADE INPUT DIALOG
    activeGradeDialogCourseId?.let { courseId ->
        val course = courses.find { it.id == courseId }
        if (course != null) {
            EditGradesDialog(
                course = course,
                onDismiss = { activeGradeDialogCourseId = null },
                onSave = { vize, secondEval, fin, letterGrade ->
                    val updatedCourse = course.copy(
                        midtermGrade = vize,
                        secondAssessmentGrade = secondEval,
                        finalGrade = fin,
                        letterGrade = letterGrade,
                        isCompleted = letterGrade != "Devam" && letterGrade != "FF"
                    )
                    courses = SchoolCourseRepository.updateCourse(context, updatedCourse)
                    activeGradeDialogCourseId = null
                }
            )
        }
    }

    // WEIGHTS DIALOG
    activeWeightsDialogCourseId?.let { courseId ->
        val course = courses.find { it.id == courseId }
        if (course != null) {
            EditWeightsDialog(
                course = course,
                onDismiss = { activeWeightsDialogCourseId = null },
                onSave = { vWeight, sWeight, fWeight ->
                    val updatedCourse = course.copy(
                        weights = GradeWeights(
                            midtermWeight = vWeight,
                            secondAssessmentWeight = sWeight,
                            finalWeight = fWeight
                        )
                    )
                    courses = SchoolCourseRepository.updateCourse(context, updatedCourse)
                    activeWeightsDialogCourseId = null
                }
            )
        }
    }

    // ADD COURSE DIALOG
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
}

@Composable
private fun SemesterFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AccentCyan.copy(alpha = 0.2f) else PanelNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AccentCyan else BorderSubtle
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentCyan else TextMuted,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun CourseAttendanceOverviewCard(
    course: SchoolCourse,
    onOpenAttendance: () -> Unit
) {
    val totalAbsent = course.absentCount
    val isFailed = course.isFailedDueToAbsence
    val isWarning = course.isLastAbsenceWarning
    val remaining = course.remainingAbsenceRights

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isFailed) StatusFailed.copy(alpha = 0.5f)
                else if (isWarning) AccentAmber.copy(alpha = 0.5f)
                else BorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (course.instructor.isNotBlank()) course.instructor else "Öğretim Üyesi Belirtilmemiş",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isFailed -> StatusFailed.copy(alpha = 0.15f)
                        isWarning -> AccentAmber.copy(alpha = 0.15f)
                        else -> AccentEmerald.copy(alpha = 0.15f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            isFailed -> StatusFailed.copy(alpha = 0.4f)
                            isWarning -> AccentAmber.copy(alpha = 0.4f)
                            else -> AccentEmerald.copy(alpha = 0.4f)
                        }
                    )
                ) {
                    Text(
                        text = when {
                            isFailed -> "Kaldı ($totalAbsent Hafta)"
                            isWarning -> "Kritik ($totalAbsent Hafta)"
                            else -> "Güvenli (Kalan: $remaining)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isFailed -> StatusFailed
                                isWarning -> AccentAmber
                                else -> AccentEmerald
                            },
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 16-Week Dot Matrix Visualizer (Week 8: Vize, Week 16: Final)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                course.attendance.take(16).forEach { week ->
                    if (week.weekNumber == 8 || week.weekNumber == 16) {
                        // Sınav Rozetleri (8: Vize, 16: Final)
                        val examLetter = if (week.weekNumber == 8) "V" else "F"
                        Box(
                            modifier = Modifier
                                .size(17.dp)
                                .clip(CircleShape)
                                .background(AccentAmber.copy(alpha = 0.25f))
                                .border(1.dp, AccentAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = examLetter,
                                fontSize = 8.sp,
                                color = AccentAmber,
                                fontWeight = FontWeight.Black
                            )
                        }
                    } else {
                        val dotColor = when (week.status) {
                            AttendanceStatus.ATTENDED -> AccentEmerald
                            AttendanceStatus.ABSENT -> StatusFailed
                            AttendanceStatus.NOT_HELD -> AccentAmber
                            AttendanceStatus.PENDING -> TextDarkMuted.copy(alpha = 0.35f)
                        }
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(dotColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = week.weekNumber.toString(),
                                fontSize = 7.5.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footnote & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "V: Vize • F: Final",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDarkMuted, fontSize = 10.sp)
                )
                Text(
                    text = "${course.attendedCount}/${course.totalAttendanceWeeks} Hafta Katılım",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccentEmerald,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onOpenAttendance,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "16 Haftalık Yoklamayı Düzenle",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun CourseCardItem(
    course: SchoolCourse,
    onOpenAttendance: () -> Unit,
    onOpenGrades: () -> Unit,
    onOpenWeights: () -> Unit,
    onDelete: () -> Unit
) {
    val isFailed = course.isFailedDueToAbsence
    val isWarning = course.isLastAbsenceWarning
    val attendancePercent = (course.attendanceRatio * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isFailed) StatusFailed.copy(alpha = 0.5f)
                else if (isWarning) AccentAmber.copy(alpha = 0.4f)
                else BorderSubtle,
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Course Code & Name (Left) + Grade Badge & Delete (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (course.code.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentCyan.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = course.code,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "${course.credits} Kredi",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (course.instructor.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AccentCyan.copy(alpha = 0.7f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = course.instructor,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Letter Grade Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (course.letterGrade) {
                            "AA", "BA" -> AccentEmerald.copy(alpha = 0.16f)
                            "BB", "CB", "CC" -> AccentCyan.copy(alpha = 0.16f)
                            "DC", "DD" -> AccentAmber.copy(alpha = 0.16f)
                            "FD", "FF" -> StatusFailed.copy(alpha = 0.16f)
                            else -> PanelNavy
                        },
                        border = BorderStroke(
                            1.dp,
                            when (course.letterGrade) {
                                "AA", "BA" -> AccentEmerald.copy(alpha = 0.4f)
                                "BB", "CB", "CC" -> AccentCyan.copy(alpha = 0.4f)
                                "DC", "DD" -> AccentAmber.copy(alpha = 0.4f)
                                "FD", "FF" -> StatusFailed.copy(alpha = 0.4f)
                                else -> BorderSubtle
                            }
                        )
                    ) {
                        Text(
                            text = if (course.letterGrade.isNotBlank()) course.letterGrade else "Devam",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (course.letterGrade) {
                                    "AA", "BA" -> AccentEmerald
                                    "BB", "CB", "CC" -> AccentCyan
                                    "DC", "DD" -> AccentAmber
                                    "FD", "FF" -> StatusFailed
                                    else -> TextSecondary
                                },
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Dersi Sil",
                            tint = TextDarkMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Schedule & Location Meta Row (Clean Classroom, e.g. "Derslik 9")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (course.dayOfWeek.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AccentCyan.copy(alpha = 0.85f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.dayOfWeek} ${course.timeSlot}".trim(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDarkMuted, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Icon(
                    imageVector = if (course.isOnline) Icons.Outlined.Language else Icons.Outlined.Room,
                    contentDescription = null,
                    tint = if (course.isOnline) Color(0xFFA5B4FC) else AccentEmerald,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = course.cleanClassroom,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (course.isOnline) Color(0xFFA5B4FC) else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar Row (Directly matching User's Reference Image)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress percentage (Left)
                Text(
                    text = "$attendancePercent%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isFailed -> StatusFailed
                            isWarning -> AccentAmber
                            else -> AccentCyan
                        },
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.width(38.dp)
                )

                // Progress Bar (Center)
                val progressFill = course.attendanceRatio.coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(PanelNavy)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = if (progressFill > 0f) progressFill else 0.02f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    isFailed -> StatusFailed
                                    isWarning -> AccentAmber
                                    else -> AccentCyan
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Task count / Weeks count (Right)
                Text(
                    text = "${course.attendedCount}/${course.totalAttendanceWeeks} Hafta",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clear, Distinct Action Buttons (Zero ambiguity)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Notlar & Sınavlar Butonu
                OutlinedButton(
                    onClick = onOpenGrades,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    border = BorderStroke(1.dp, BorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = PanelNavy,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Notlar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 2. Yoklama Butonu
                OutlinedButton(
                    onClick = onOpenAttendance,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    border = BorderStroke(
                        1.dp,
                        when {
                            isFailed -> StatusFailed.copy(alpha = 0.5f)
                            isWarning -> AccentAmber.copy(alpha = 0.5f)
                            else -> AccentEmerald.copy(alpha = 0.4f)
                        }
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = PanelNavy,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = when {
                            isFailed -> StatusFailed
                            isWarning -> AccentAmber
                            else -> AccentEmerald
                        },
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isFailed -> "Kaldı (${course.absentCount}/4)"
                            isWarning -> "Kritik (${course.absentCount}/4)"
                            else -> "Yoklama (${course.remainingAbsenceRights} Hak)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = when {
                                isFailed -> StatusFailed
                                isWarning -> AccentAmber
                                else -> TextPrimary
                            }
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick Ağırlık Ayarı Butonu
                IconButton(
                    onClick = onOpenWeights,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PanelNavy)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Ağırlık Ayarları",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GradePillItem(label: String, grade: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = grade?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "-",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (grade != null) TextPrimary else TextDarkMuted,
                fontSize = 13.sp
            )
        )
    }
}

// -------------------------------------------------------------
// 16-WEEK ATTENDANCE DETAIL SCREEN
// -------------------------------------------------------------
@Composable
private fun CourseAttendanceDetailView(
    course: SchoolCourse,
    onUpdateCourse: (SchoolCourse) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFailed = course.isFailedDueToAbsence
    val isWarning = course.isLastAbsenceWarning

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp),
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
                    text = "DERS LİSTESİNE DÖN",
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
                    .border(1.dp, if (isFailed) Color(0xFFEF4444).copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PanelNavy,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = course.semesterLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AccentCyan,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "16 Haftalık Yoklama",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = course.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Summary Pills (Fixed 4 rights)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttendanceStatPill("Katıldı", "${course.attendedCount} Hafta", AccentEmerald)
                        AttendanceStatPill(
                            "Devamsızlık",
                            "${course.absentCount} / 4 Hak",
                            when {
                                isFailed -> Color(0xFFEF4444)
                                isWarning -> AccentAmber
                                else -> AccentEmerald
                            }
                        )
                        AttendanceStatPill(
                            "Kalan Hak",
                            "${course.remainingAbsenceRights} Hafta",
                            if (isFailed) Color(0xFFEF4444) else TextSecondary
                        )
                    }

                    // Warning / Failure Banner
                    if (isFailed) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "5. devamsızlık yapıldı! 4 hak aşıldığı için dersten devamsızlıkla kalındı.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    } else if (isWarning) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentAmber.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "4 devamsızlık hakkının tamamı doldu! 1 hafta daha gitmezsen dersten kalırsın.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 1: 1 – 7. HAFTA
        item {
            AttendanceSectionTitle("1 – 7. HAFTA")
        }
        items(course.attendance.filter { it.weekNumber in 1..7 }, key = { it.weekNumber }) { week ->
            AttendanceWeekRow(
                week = week,
                onStatusChange = { newStatus ->
                    val updatedList = course.attendance.map {
                        if (it.weekNumber == week.weekNumber) it.copy(status = newStatus) else it
                    }
                    onUpdateCourse(course.copy(attendance = updatedList))
                }
            )
        }

        // VİZE BÖLÜMÜ (Minimal Çizgi ve Rozet)
        item {
            ExamDivider(label = "VİZE")
        }

        // Section 2: 9 – 15. HAFTA
        item {
            AttendanceSectionTitle("9 – 15. HAFTA")
        }
        items(course.attendance.filter { it.weekNumber in 9..15 }, key = { it.weekNumber }) { week ->
            AttendanceWeekRow(
                week = week,
                onStatusChange = { newStatus ->
                    val updatedList = course.attendance.map {
                        if (it.weekNumber == week.weekNumber) it.copy(status = newStatus) else it
                    }
                    onUpdateCourse(course.copy(attendance = updatedList))
                }
            )
        }

        // FİNAL BÖLÜMÜ (Minimal Çizgi ve Rozet)
        item {
            ExamDivider(label = "FİNAL")
        }
    }
}

@Composable
private fun ExamDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BorderSubtle.copy(alpha = 0.5f),
            thickness = 1.dp
        )
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = PanelNavyElevated,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = AccentAmber,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BorderSubtle.copy(alpha = 0.5f),
            thickness = 1.dp
        )
    }
}

@Composable
private fun AttendanceSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp,
            fontSize = 11.sp
        ),
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun AttendanceStatPill(title: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = PanelNavy,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp))
        }
    }
}

@Composable
private fun AttendanceWeekRow(
    week: CourseAttendanceWeek,
    isHighlight: Boolean = false,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isHighlight) AccentCyan.copy(alpha = 0.4f) else BorderSubtle,
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isHighlight) AccentCyan.copy(alpha = 0.15f) else PanelNavy)
                        .border(1.dp, if (isHighlight) AccentCyan else BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${week.weekNumber}",
                        fontWeight = FontWeight.Bold,
                        color = if (isHighlight) AccentCyan else TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = week.label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                        color = if (isHighlight) TextPrimary else TextSecondary,
                        fontSize = 13.sp
                    )
                )
            }

            // Quick Status Buttons: Tik (✓), Çarpı (✕), Tatil
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tik (Katıldım)
                AttendanceIconButton(
                    icon = Icons.Default.Check,
                    contentDescription = "Katıldım",
                    isSelected = week.status == AttendanceStatus.ATTENDED,
                    selectedColor = AccentEmerald,
                    onClick = {
                        onStatusChange(
                            if (week.status == AttendanceStatus.ATTENDED) AttendanceStatus.PENDING else AttendanceStatus.ATTENDED
                        )
                    }
                )
                // Çarpı (Gitmedim)
                AttendanceIconButton(
                    icon = Icons.Default.Close,
                    contentDescription = "Gitmedim",
                    isSelected = week.status == AttendanceStatus.ABSENT,
                    selectedColor = Color(0xFFEF4444),
                    onClick = {
                        onStatusChange(
                            if (week.status == AttendanceStatus.ABSENT) AttendanceStatus.PENDING else AttendanceStatus.ABSENT
                        )
                    }
                )
                // Tatil
                AttendanceTextButton(
                    label = "Tatil",
                    isSelected = week.status == AttendanceStatus.NOT_HELD,
                    selectedColor = AccentAmber,
                    onClick = {
                        onStatusChange(
                            if (week.status == AttendanceStatus.NOT_HELD) AttendanceStatus.PENDING else AttendanceStatus.NOT_HELD
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AttendanceIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.22f) else PanelNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) selectedColor else BorderSubtle
        ),
        modifier = Modifier
            .size(width = 34.dp, height = 30.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isSelected) selectedColor else TextMuted.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AttendanceTextButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.22f) else PanelNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) selectedColor else BorderSubtle
        ),
        modifier = Modifier
            .height(30.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) selectedColor else TextMuted.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )
        }
    }
}

// -------------------------------------------------------------
// EDIT GRADES DIALOG
// -------------------------------------------------------------
@Composable
private fun EditGradesDialog(
    course: SchoolCourse,
    onDismiss: () -> Unit,
    onSave: (Double?, Double?, Double?, String) -> Unit
) {
    var vizeText by remember {
        mutableStateOf(course.midtermGrade?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var secondEvalText by remember {
        mutableStateOf(course.secondAssessmentGrade?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var finalText by remember {
        mutableStateOf(course.finalGrade?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }
    var selectedLetterGrade by remember { mutableStateOf(course.letterGrade) }

    fun sanitizeGrade(raw: String): String {
        val clean = raw.filter { it.isDigit() || it == '.' }
        val num = clean.toDoubleOrNull()
        return when {
            num == null -> if (clean.isEmpty()) "" else clean.take(1)
            num > 100.0 -> "100"
            num < 0.0 -> "0"
            else -> clean
        }
    }

    val vVal = vizeText.toDoubleOrNull()?.coerceIn(0.0, 100.0)
    val sVal = secondEvalText.toDoubleOrNull()?.coerceIn(0.0, 100.0)
    val fVal = finalText.toDoubleOrNull()?.coerceIn(0.0, 100.0)

    var totalWeight = 0
    var weightedSum = 0.0
    if (vVal != null) { weightedSum += vVal * course.weights.midtermWeight; totalWeight += course.weights.midtermWeight }
    if (sVal != null) { weightedSum += sVal * course.weights.secondAssessmentWeight; totalWeight += course.weights.secondAssessmentWeight }
    if (fVal != null) { weightedSum += fVal * course.weights.finalWeight; totalWeight += course.weights.finalWeight }
    val calculatedLiveAvg = if (totalWeight > 0) weightedSum / totalWeight else null

    val letterGrades = listOf("AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF", "Devam")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Text(
                text = "${course.name} Notları (0 - 100)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Live Weighted Average Display inside Dialog
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PanelNavy,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Ağırlıklı Ortalama",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                            )
                            Text(
                                text = "Vize %${course.weights.midtermWeight} • 2. Değ %${course.weights.secondAssessmentWeight} • Final %${course.weights.finalWeight}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkMuted, fontSize = 9.5.sp)
                            )
                        }
                        Text(
                            text = if (calculatedLiveAvg != null) String.format("%.1f", calculatedLiveAvg) else "—",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontSize = 18.sp
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = vizeText,
                    onValueChange = { vizeText = sanitizeGrade(it) },
                    label = { Text("Vize Notu (0 - 100, Ağırlık: %${course.weights.midtermWeight})", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = secondEvalText,
                    onValueChange = { secondEvalText = sanitizeGrade(it) },
                    label = { Text("2. Değerlendirme / Quiz (0 - 100, Ağırlık: %${course.weights.secondAssessmentWeight})", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = finalText,
                    onValueChange = { finalText = sanitizeGrade(it) },
                    label = { Text("Final Notu (0 - 100, Ağırlık: %${course.weights.finalWeight})", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Harf Notunu Seç (veya elle belirle):",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextMuted)
                )

                // Letter Grade Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    letterGrades.take(5).forEach { lg ->
                        LetterChip(
                            letter = lg,
                            isSelected = selectedLetterGrade == lg,
                            onSelect = { selectedLetterGrade = lg }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    letterGrades.drop(5).forEach { lg ->
                        LetterChip(
                            letter = lg,
                            isSelected = selectedLetterGrade == lg,
                            onSelect = { selectedLetterGrade = lg }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(vVal, sVal, fVal, selectedLetterGrade)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextMuted)
            }
        }
    )
}

@Composable
private fun LetterChip(letter: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) AccentCyan else PanelNavy,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentCyan else BorderSubtle),
        modifier = Modifier.clickable { onSelect() }
    ) {
        Text(
            text = letter,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 10.5.sp
            )
        )
    }
}

// -------------------------------------------------------------
// EDIT WEIGHTS DIALOG
// -------------------------------------------------------------
@Composable
private fun EditWeightsDialog(
    course: SchoolCourse,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var vizeWeightText by remember { mutableStateOf(course.weights.midtermWeight.toString()) }
    var secondEvalWeightText by remember { mutableStateOf(course.weights.secondAssessmentWeight.toString()) }
    var finalWeightText by remember { mutableStateOf(course.weights.finalWeight.toString()) }

    val v = vizeWeightText.toIntOrNull() ?: 0
    val s = secondEvalWeightText.toIntOrNull() ?: 0
    val f = finalWeightText.toIntOrNull() ?: 0
    val total = v + s + f

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Text(
                text = "${course.name} Not Ağırlıkları (%)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Vize, 2. Değerlendirme ve Finalin dönem sonu notuna etki yüzdelerini gir.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.5.sp)
                )

                OutlinedTextField(
                    value = vizeWeightText,
                    onValueChange = { vizeWeightText = it },
                    label = { Text("Vize Ağırlığı (%)", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = secondEvalWeightText,
                    onValueChange = { secondEvalWeightText = it },
                    label = { Text("2. Değerlendirme Ağırlığı (%)", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = finalWeightText,
                    onValueChange = { finalWeightText = it },
                    label = { Text("Final Ağırlığı (%)", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Toplam Ağırlık:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
                    )
                    Text(
                        text = "%$total",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (total == 100) AccentEmerald else AccentAmber
                        )
                    )
                }

                if (total != 100) {
                    Text(
                        text = "İpucu: Yüzdelerin toplamının %100 olması tavsiye edilir.",
                        style = MaterialTheme.typography.labelSmall.copy(color = AccentAmber, fontSize = 10.5.sp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(v, s, f) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text("Uygula", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextMuted)
            }
        }
    )
}

// -------------------------------------------------------------
// ADD NEW COURSE DIALOG
// -------------------------------------------------------------
@Composable
internal fun AddNewCourseDialog(
    onDismiss: () -> Unit,
    onAddCourse: (
        name: String,
        code: String,
        credits: Int,
        semester: Int,
        instructor: String,
        classroom: String,
        dayOfWeek: String,
        timeSlot: String,
        isOnline: Boolean
    ) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var creditsText by remember { mutableStateOf("5") }
    var selectedSemester by remember { mutableStateOf(7) } // Default: 7. Dönem (Senior Güz)
    var instructor by remember { mutableStateOf("") }
    var classroom by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableStateOf("Pazartesi") }
    var timeSlot by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }

    val days = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Text("Yeni Ders Ekle", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        label = { Text("Kod (Örn: BMI4146)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(0.45f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = creditsText,
                        onValueChange = { creditsText = it },
                        label = { Text("Kredi / AKTS", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.55f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ders Adı (Örn: Data Mining)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = instructor,
                        onValueChange = { instructor = it },
                        label = { Text("Öğr. Görevlisi", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = classroom,
                        onValueChange = { classroom = it },
                        label = { Text("Derslik (Örn: ED-K1-04)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("Saat (Örn: 09:35 - 12:00)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // Day selection chips
                Column {
                    Text(
                        text = "Ders Günü:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        days.forEach { day ->
                            val isSel = dayOfWeek == day
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) AccentCyan else PanelNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentCyan else BorderSubtle),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { dayOfWeek = day }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 5.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = day.take(3),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Online Checkbox Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isOnline = !isOnline },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isOnline,
                        onCheckedChange = { isOnline = it },
                        colors = CheckboxDefaults.colors(checkedColor = AccentCyan)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Uzaktan Öğretim (U.Ö. / Online)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                    )
                }

                Column {
                    Text(
                        text = "Dönem Seçimi (Toplam 8 Dönem):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // 1..4 Dönem Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..4).forEach { sem ->
                            SemesterSelectChip(
                                semester = sem,
                                isSelected = selectedSemester == sem,
                                onSelect = { selectedSemester = sem },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 5..8 Dönem Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (5..8).forEach { sem ->
                            SemesterSelectChip(
                                semester = sem,
                                isSelected = selectedSemester == sem,
                                onSelect = { selectedSemester = sem },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val c = creditsText.toIntOrNull() ?: 5
                        onAddCourse(
                            name.trim(),
                            code.trim(),
                            c,
                            selectedSemester,
                            instructor.trim(),
                            classroom.trim(),
                            dayOfWeek,
                            timeSlot.trim(),
                            isOnline
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text("Ekle", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextMuted)
            }
        }
    )
}

@Composable
private fun SemesterSelectChip(
    semester: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AccentCyan else PanelNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AccentCyan else BorderSubtle
        ),
        modifier = modifier.clickable { onSelect() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$semester. D.",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
