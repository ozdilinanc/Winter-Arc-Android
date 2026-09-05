package com.example.ui.components.school

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun SchoolCoursesView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Start with empty course list as requested
    var courses by remember { mutableStateOf(listOf<SchoolCourse>()) }

    var activeAttendanceCourseId by remember { mutableStateOf<String?>(null) }
    var activeGradeDialogCourseId by remember { mutableStateOf<String?>(null) }
    var activeWeightsDialogCourseId by remember { mutableStateOf<String?>(null) }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    // System Back handling
    BackHandler {
        if (activeAttendanceCourseId != null) {
            activeAttendanceCourseId = null
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
                    courses = courses.map { if (it.id == updated.id) updated else it }
                },
                onBack = { activeAttendanceCourseId = null },
                modifier = modifier
            )
            return
        }
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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📚", fontSize = 22.sp)
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
                        text = "Vize, 2. Değerlendirme, Final & 16 Haftalık Yoklama Takibi",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.5.sp)
                    )
                }

                Button(
                    onClick = { isAddDialogOpen = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Ders Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${courses.size}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                            Text(text = "Toplam Ders", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                        }
                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(BorderSubtle))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$totalCredits", fontWeight = FontWeight.Bold, color = AccentAmber, fontSize = 16.sp)
                            Text(text = "Toplam Kredi", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp))
                        }
                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(BorderSubtle))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            Text(text = "📚", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Henüz Ders Eklenmedi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Bu dönem alacağın dersleri ekle; vize, 2. değerlendirme, final notlarını gir ve 16 haftalık yoklamanı tek tıkla takip et.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { isAddDialogOpen = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "İlk Dersi Ekle", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Courses List
        items(courses, key = { it.id }) { course ->
            CourseCardItem(
                course = course,
                onOpenAttendance = { activeAttendanceCourseId = course.id },
                onOpenGrades = { activeGradeDialogCourseId = course.id },
                onOpenWeights = { activeWeightsDialogCourseId = course.id },
                onDelete = { courses = courses.filter { it.id != course.id } }
            )
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
                    courses = courses.map {
                        if (it.id == courseId) {
                            it.copy(
                                midtermGrade = vize,
                                secondAssessmentGrade = secondEval,
                                finalGrade = fin,
                                letterGrade = letterGrade,
                                isCompleted = letterGrade != "Devam" && letterGrade != "FF"
                            )
                        } else it
                    }
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
                    courses = courses.map {
                        if (it.id == courseId) {
                            it.copy(
                                weights = GradeWeights(
                                    midtermWeight = vWeight,
                                    secondAssessmentWeight = sWeight,
                                    finalWeight = fWeight
                                )
                            )
                        } else it
                    }
                    activeWeightsDialogCourseId = null
                }
            )
        }
    }

    // ADD COURSE DIALOG
    if (isAddDialogOpen) {
        AddNewCourseDialog(
            onDismiss = { isAddDialogOpen = false },
            onAddCourse = { code, name, credits, semester, maxAbsence ->
                val newCourse = SchoolCourse(
                    id = UUID.randomUUID().toString(),
                    code = code,
                    name = name,
                    credits = credits,
                    semester = semester,
                    maxAbsenceWeeks = maxAbsence
                )
                courses = courses + newCourse
                isAddDialogOpen = false
            }
        )
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
    val isOverAbsenceLimit = course.absentCount >= course.maxAbsenceWeeks
    val isNearAbsenceLimit = course.absentCount == course.maxAbsenceWeeks - 1 && course.maxAbsenceWeeks > 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isOverAbsenceLimit) Color(0xFFEF4444).copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Code, Credits, Semester & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PanelNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = course.code,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${course.credits} Kredi • ${course.semester}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Course Name
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grades Row (Vize, 2. Değerlendirme, Final)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PanelNavy,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GradePillItem("Vize (%${course.weights.midtermWeight})", course.midtermGrade)
                    Box(modifier = Modifier.height(20.dp).width(1.dp).background(BorderSubtle))
                    GradePillItem("2. Değ (%${course.weights.secondAssessmentWeight})", course.secondAssessmentGrade)
                    Box(modifier = Modifier.height(20.dp).width(1.dp).background(BorderSubtle))
                    GradePillItem("Final (%${course.weights.finalWeight})", course.finalGrade)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Average & Letter Grade Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ortalama: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
                    )
                    Text(
                        text = course.calculatedAverage?.let { String.format("%.1f", it) } ?: "-",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (course.calculatedAverage != null) AccentCyan else TextMuted,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Harf:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (course.letterGrade) {
                            "AA", "BA" -> AccentEmerald.copy(alpha = 0.15f)
                            "BB", "CB", "CC" -> AccentCyan.copy(alpha = 0.15f)
                            "DC", "DD" -> AccentAmber.copy(alpha = 0.15f)
                            "FD", "FF" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                            else -> PanelNavy
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (course.letterGrade) {
                                "AA", "BA" -> AccentEmerald
                                "BB", "CB", "CC" -> AccentCyan
                                "DC", "DD" -> AccentAmber
                                "FD", "FF" -> Color(0xFFEF4444)
                                else -> BorderSubtle
                            }
                        )
                    ) {
                        Text(
                            text = course.letterGrade,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (course.letterGrade) {
                                    "AA", "BA" -> AccentEmerald
                                    "BB", "CB", "CC" -> AccentCyan
                                    "DC", "DD" -> AccentAmber
                                    "FD", "FF" -> Color(0xFFEF4444)
                                    else -> TextSecondary
                                },
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Notları Gir Butonu
                    FilledTonalButton(
                        onClick = onOpenGrades,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PanelNavy,
                            contentColor = AccentCyan
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Notlar", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Ağırlıklar Butonu
                    IconButton(
                        onClick = onOpenWeights,
                        modifier = Modifier.size(30.dp)
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

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle.copy(alpha = 0.6f)))
            Spacer(modifier = Modifier.height(10.dp))

            // Attendance Row & Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Absence Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Devamsızlık: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.5.sp)
                    )
                    Text(
                        text = "${course.absentCount} / ${course.maxAbsenceWeeks} hafta",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isOverAbsenceLimit -> Color(0xFFEF4444)
                                isNearAbsenceLimit -> AccentAmber
                                else -> AccentEmerald
                            },
                            fontSize = 11.5.sp
                        )
                    )
                    if (isOverAbsenceLimit) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "🚨", fontSize = 11.sp)
                    }
                }

                // Open 16 Weeks Attendance
                OutlinedButton(
                    onClick = onOpenAttendance,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "16 Haftalık Yoklama", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    val isOverLimit = course.absentCount >= course.maxAbsenceWeeks

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
                    .border(1.dp, if (isOverLimit) Color(0xFFEF4444).copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(16.dp)),
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
                                        text = course.code,
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

                    // Status Summary Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AttendanceStatPill("Katıldı", "${course.attendedCount} Hafta", AccentEmerald)
                        AttendanceStatPill(
                            "Devamsızlık",
                            "${course.absentCount} / ${course.maxAbsenceWeeks} Hafta",
                            if (isOverLimit) Color(0xFFEF4444) else AccentAmber
                        )
                        AttendanceStatPill(
                            "Kalan Hak",
                            "${maxOf(0, course.maxAbsenceWeeks - course.absentCount)} Hafta",
                            if (isOverLimit) Color(0xFFEF4444) else TextSecondary
                        )
                    }

                    if (isOverLimit) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🚨", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Devamsızlık sınırını aştın! Ders kalma riski var.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 1: 📝 VİZE ÖNCESİ (Hafta 1 - 7)
        item {
            AttendanceSectionTitle("📝 VİZE ÖNCESİ (HAFTA 1 - 7)")
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

        // Section 2: 🎯 VİZE HAFTASI (Hafta 8)
        item {
            AttendanceSectionTitle("🎯 VİZE HAFTASI (HAFTA 8)")
        }
        items(course.attendance.filter { it.weekNumber == 8 }, key = { it.weekNumber }) { week ->
            AttendanceWeekRow(
                week = week,
                isHighlight = true,
                onStatusChange = { newStatus ->
                    val updatedList = course.attendance.map {
                        if (it.weekNumber == week.weekNumber) it.copy(status = newStatus) else it
                    }
                    onUpdateCourse(course.copy(attendance = updatedList))
                }
            )
        }

        // Section 3: 📝 VİZE SONRASI (Hafta 9 - 15)
        item {
            AttendanceSectionTitle("📝 VİZE SONRASI (HAFTA 9 - 15)")
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

        // Section 4: 🏁 FİNAL HAFTASI (Hafta 16)
        item {
            AttendanceSectionTitle("🏁 FİNAL HAFTASI (HAFTA 16)")
        }
        items(course.attendance.filter { it.weekNumber == 16 }, key = { it.weekNumber }) { week ->
            AttendanceWeekRow(
                week = week,
                isHighlight = true,
                onStatusChange = { newStatus ->
                    val updatedList = course.attendance.map {
                        if (it.weekNumber == week.weekNumber) it.copy(status = newStatus) else it
                    }
                    onUpdateCourse(course.copy(attendance = updatedList))
                }
            )
        }
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
                        fontSize = 12.5.sp
                    )
                )
            }

            // Quick Status Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AttendanceStatusButton(
                    label = "Katıldım",
                    isSelected = week.status == AttendanceStatus.ATTENDED,
                    selectedColor = AccentEmerald,
                    onClick = {
                        onStatusChange(
                            if (week.status == AttendanceStatus.ATTENDED) AttendanceStatus.PENDING else AttendanceStatus.ATTENDED
                        )
                    }
                )
                AttendanceStatusButton(
                    label = "Gitmedim",
                    isSelected = week.status == AttendanceStatus.ABSENT,
                    selectedColor = Color(0xFFEF4444),
                    onClick = {
                        onStatusChange(
                            if (week.status == AttendanceStatus.ABSENT) AttendanceStatus.PENDING else AttendanceStatus.ABSENT
                        )
                    }
                )
                AttendanceStatusButton(
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
private fun AttendanceStatusButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.2f) else PanelNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) selectedColor else BorderSubtle
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) selectedColor else TextMuted,
                fontSize = 10.5.sp
            )
        )
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
    var vizeText by remember { mutableStateOf(course.midtermGrade?.toString() ?: "") }
    var secondEvalText by remember { mutableStateOf(course.secondAssessmentGrade?.toString() ?: "") }
    var finalText by remember { mutableStateOf(course.finalGrade?.toString() ?: "") }
    var selectedLetterGrade by remember { mutableStateOf(course.letterGrade) }

    val letterGrades = listOf("AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF", "Devam")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Text(
                text = "${course.code} Notlarını Düzenle",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = vizeText,
                    onValueChange = { vizeText = it },
                    label = { Text("Vize Notu (Ağırlık: %${course.weights.midtermWeight})", fontSize = 12.sp) },
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
                    onValueChange = { secondEvalText = it },
                    label = { Text("2. Değerlendirme / Quiz / Proje (%${course.weights.secondAssessmentWeight})", fontSize = 12.sp) },
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
                    onValueChange = { finalText = it },
                    label = { Text("Final Notu (Ağırlık: %${course.weights.finalWeight})", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

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
                    val v = vizeText.toDoubleOrNull()
                    val s = secondEvalText.toDoubleOrNull()
                    val f = finalText.toDoubleOrNull()
                    onSave(v, s, f, selectedLetterGrade)
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
                text = "${course.code} Not Ağırlıkları (%)",
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
private fun AddNewCourseDialog(
    onDismiss: () -> Unit,
    onAddCourse: (String, String, Int, String, Int) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var creditsText by remember { mutableStateOf("3") }
    var semesterText by remember { mutableStateOf("Güz Dönemi") }
    var maxAbsenceText by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Text("Yeni Ders Ekle", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Ders Kodu (Örn: CENG 401)", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ders Adı (Örn: Yazılım Mimarisi)", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = creditsText,
                        onValueChange = { creditsText = it },
                        label = { Text("Kredi", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
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
                        value = maxAbsenceText,
                        onValueChange = { maxAbsenceText = it },
                        label = { Text("Devamsızlık Hak (Hafta)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                OutlinedTextField(
                    value = semesterText,
                    onValueChange = { semesterText = it },
                    label = { Text("Dönem (Örn: 7. Dönem / Güz)", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        val c = creditsText.toIntOrNull() ?: 3
                        val ma = maxAbsenceText.toIntOrNull() ?: 4
                        onAddCourse(code.trim(), name.trim(), c, semesterText.trim(), ma)
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
