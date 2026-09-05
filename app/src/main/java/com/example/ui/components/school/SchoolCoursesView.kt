package com.example.ui.components.school

import androidx.activity.compose.BackHandler
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

    // Optional semester filter (0 = all)
    var selectedSemesterFilter by remember { mutableStateOf(0) }

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
                            text = "Bu dönem alacağın dersleri ekle; vize, 2. değerlendirme ve final notlarını gir. 4 devamsızlık hakkını 16 haftalık çizelgede takip et.",
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
        items(filteredCourses, key = { it.id }) { course ->
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
            onAddCourse = { name, credits, semester ->
                val newCourse = SchoolCourse(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    credits = credits,
                    semester = semester
                )
                courses = courses + newCourse
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
private fun CourseCardItem(
    course: SchoolCourse,
    onOpenAttendance: () -> Unit,
    onOpenGrades: () -> Unit,
    onOpenWeights: () -> Unit,
    onDelete: () -> Unit
) {
    val isFailed = course.isFailedDueToAbsence
    val isWarning = course.isLastAbsenceWarning

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isFailed) Color(0xFFEF4444).copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = PanelNavyElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Course Name & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.5.sp
                        )
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

            Spacer(modifier = Modifier.height(4.dp))

            // Badges Row: Semester & Credits
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PanelNavy,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = course.semesterLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                    )
                }

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
                // Absence Badge with fixed 4-right rule
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Devamsızlık: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.5.sp)
                    )
                    Text(
                        text = when {
                            isFailed -> "🚨 KALDI (${course.absentCount} / 4 Hak)"
                            isWarning -> "⚠️ SON HAK (4 / 4)"
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
                                Text(text = "🚨", fontSize = 16.sp)
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
                                Text(text = "⚠️", fontSize = 16.sp)
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

        // Section 2: VİZE HAFTASI
        item {
            AttendanceSectionTitle("VİZE HAFTASI")
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

        // Section 3: 9 – 15. HAFTA
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

        // Section 4: FİNAL HAFTASI
        item {
            AttendanceSectionTitle("FİNAL HAFTASI")
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
                text = "${course.name} Notlarını Düzenle",
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
// ADD NEW COURSE DIALOG (8 Semesters selection, No Code, Fixed 4 absences)
// -------------------------------------------------------------
@Composable
private fun AddNewCourseDialog(
    onDismiss: () -> Unit,
    onAddCourse: (String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var creditsText by remember { mutableStateOf("3") }
    var selectedSemester by remember { mutableStateOf(7) } // Default: 7. Dönem (Senior Güz)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelNavyElevated,
        title = {
            Text("Yeni Ders Ekle", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

                OutlinedTextField(
                    value = creditsText,
                    onValueChange = { creditsText = it },
                    label = { Text("Kredi (Örn: 3, 4)", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

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

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PanelNavy,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ℹ️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Devamsızlık hakkı fix 4 hafta olarak takip edilir. 5. devamsızlıkta kalınır.",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.5.sp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val c = creditsText.toIntOrNull() ?: 3
                        onAddCourse(name.trim(), c, selectedSemester)
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
