package com.example.ui.components.school

enum class AttendanceStatus {
    ATTENDED,   // Katıldı ✅
    ABSENT,     // Gitmedi (Devamsız) ❌
    NOT_HELD,   // Tatil / Ders Yapılmadı ⏸️
    PENDING     // Beklemede ⚪
}

data class CourseAttendanceWeek(
    val weekNumber: Int,
    val label: String,
    val status: AttendanceStatus = AttendanceStatus.PENDING,
    val note: String = ""
)

data class GradeWeights(
    val midtermWeight: Int = 30,          // %30 varsayılan
    val secondAssessmentWeight: Int = 20, // %20 varsayılan
    val finalWeight: Int = 50             // %50 varsayılan
)

data class SchoolCourse(
    val id: String,
    val name: String,
    val credits: Int,
    val semester: Int = 7,                // 1..8 (Toplam 8 dönem)
    val midtermGrade: Double? = null,
    val secondAssessmentGrade: Double? = null,
    val finalGrade: Double? = null,
    val weights: GradeWeights = GradeWeights(),
    val letterGrade: String = "Devam",
    val attendance: List<CourseAttendanceWeek> = defaultAttendanceWeeks(),
    val isCompleted: Boolean = false
) {
    val semesterLabel: String
        get() = "$semester. Dönem"

    val calculatedAverage: Double?
        get() {
            var totalWeight = 0
            var weightedSum = 0.0
            midtermGrade?.let {
                weightedSum += it * weights.midtermWeight
                totalWeight += weights.midtermWeight
            }
            secondAssessmentGrade?.let {
                weightedSum += it * weights.secondAssessmentWeight
                totalWeight += weights.secondAssessmentWeight
            }
            finalGrade?.let {
                weightedSum += it * weights.finalWeight
                totalWeight += weights.finalWeight
            }
            return if (totalWeight > 0) weightedSum / totalWeight else null
        }

    val absentCount: Int
        get() = attendance.count { it.status == AttendanceStatus.ABSENT }

    val attendedCount: Int
        get() = attendance.count { it.status == AttendanceStatus.ATTENDED }

    // 4 hak var, 5. olanda kalır
    val isFailedDueToAbsence: Boolean
        get() = absentCount >= 5

    val isLastAbsenceWarning: Boolean
        get() = absentCount == 4

    val remainingAbsenceRights: Int
        get() = maxOf(0, 4 - absentCount)
}

fun defaultAttendanceWeeks(): List<CourseAttendanceWeek> {
    return (1..16).map { week ->
        val label = when (week) {
            in 1..7 -> "$week. Hafta"
            8 -> "Vize Haftası"
            in 9..15 -> "$week. Hafta"
            16 -> "Final Haftası"
            else -> "$week. Hafta"
        }
        CourseAttendanceWeek(weekNumber = week, label = label)
    }
}

enum class BookReadingStatus(val label: String, val emoji: String) {
    NOT_STARTED("Başlanmadı", "⚪"),
    READING("Okunuyor", "📖"),
    COMPLETED("Tamamlandı", "✅")
}

data class BookChapterItem(
    val id: String,
    val chapterNumber: String,
    val title: String,
    val description: String = "",
    val pageRange: String = "",
    val isCompleted: Boolean = false
)

data class BookSection(
    val id: String,
    val title: String,
    val emoji: String,
    val chapters: List<BookChapterItem>
)

data class TrackedBook(
    val id: String,
    val title: String,
    val shortTitle: String,
    val authors: String = "",
    val authorOrDomain: String,
    val whyItMatters: String,
    val totalPages: Int,
    val currentPage: Int,
    val coverEmoji: String,
    val keyTopics: List<String> = emptyList(),
    val status: BookReadingStatus = BookReadingStatus.READING,
    val personalNotes: String = "",
    val sections: List<BookSection> = emptyList()
) {
    val totalChaptersCount: Int
        get() = sections.sumOf { it.chapters.size }

    val completedChaptersCount: Int
        get() = sections.sumOf { it.chapters.count { ch -> ch.isCompleted } }

    val chapterProgressPercent: Float
        get() = if (totalChaptersCount > 0) (completedChaptersCount.toFloat() / totalChaptersCount.toFloat()).coerceIn(0f, 1f) else 0f

    val pageProgressPercent: Float
        get() = if (totalPages > 0) (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f) else 0f

    val progressPercent: Float
        get() = if (totalChaptersCount > 0) chapterProgressPercent else pageProgressPercent
}

data class ProjectMilestone(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
