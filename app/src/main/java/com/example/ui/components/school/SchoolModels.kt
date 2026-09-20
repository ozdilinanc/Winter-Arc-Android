package com.example.ui.components.school

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

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
    val code: String = "",
    val credits: Int,
    val semester: Int = 7,                // 1..8 (4. Sınıf Güz = 7. Dönem)
    val instructor: String = "",
    val classroom: String = "",
    val dayOfWeek: String = "",           // Pazartesi, Salı, Çarşamba, Perşembe, Cuma
    val timeSlot: String = "",            // Örn: "09:35 - 12:00"
    val isOnline: Boolean = false,
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

    val displayTitle: String
        get() = if (code.isNotBlank()) "$code - $name" else name

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

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("code", code)
        json.put("credits", credits)
        json.put("semester", semester)
        json.put("instructor", instructor)
        json.put("classroom", classroom)
        json.put("dayOfWeek", dayOfWeek)
        json.put("timeSlot", timeSlot)
        json.put("isOnline", isOnline)
        midtermGrade?.let { json.put("midtermGrade", it) }
        secondAssessmentGrade?.let { json.put("secondAssessmentGrade", it) }
        finalGrade?.let { json.put("finalGrade", it) }
        json.put("letterGrade", letterGrade)
        json.put("isCompleted", isCompleted)

        val weightsObj = JSONObject()
        weightsObj.put("midtermWeight", weights.midtermWeight)
        weightsObj.put("secondAssessmentWeight", weights.secondAssessmentWeight)
        weightsObj.put("finalWeight", weights.finalWeight)
        json.put("weights", weightsObj)

        val attArray = JSONArray()
        attendance.forEach { week ->
            val weekObj = JSONObject()
            weekObj.put("weekNumber", week.weekNumber)
            weekObj.put("label", week.label)
            weekObj.put("status", week.status.name)
            weekObj.put("note", week.note)
            attArray.put(weekObj)
        }
        json.put("attendance", attArray)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): SchoolCourse {
            val id = json.optString("id", UUID.randomUUID().toString())
            val name = json.optString("name", "")
            val code = json.optString("code", "")
            val credits = json.optInt("credits", 5)
            val semester = json.optInt("semester", 7)
            val instructor = json.optString("instructor", "")
            val classroom = json.optString("classroom", "")
            val dayOfWeek = json.optString("dayOfWeek", "")
            val timeSlot = json.optString("timeSlot", "")
            val isOnline = json.optBoolean("isOnline", false)
            val midtermGrade = if (json.has("midtermGrade") && !json.isNull("midtermGrade")) json.optDouble("midtermGrade") else null
            val secondAssessmentGrade = if (json.has("secondAssessmentGrade") && !json.isNull("secondAssessmentGrade")) json.optDouble("secondAssessmentGrade") else null
            val finalGrade = if (json.has("finalGrade") && !json.isNull("finalGrade")) json.optDouble("finalGrade") else null
            val letterGrade = json.optString("letterGrade", "Devam")
            val isCompleted = json.optBoolean("isCompleted", false)

            val weightsObj = json.optJSONObject("weights")
            val weights = if (weightsObj != null) {
                GradeWeights(
                    midtermWeight = weightsObj.optInt("midtermWeight", 30),
                    secondAssessmentWeight = weightsObj.optInt("secondAssessmentWeight", 20),
                    finalWeight = weightsObj.optInt("finalWeight", 50)
                )
            } else GradeWeights()

            val attList = mutableListOf<CourseAttendanceWeek>()
            val attArray = json.optJSONArray("attendance")
            if (attArray != null) {
                for (i in 0 until attArray.length()) {
                    val item = attArray.optJSONObject(i) ?: continue
                    val weekNumber = item.optInt("weekNumber", i + 1)
                    val label = item.optString("label", "$weekNumber. Hafta")
                    val statusName = item.optString("status", AttendanceStatus.PENDING.name)
                    val status = try { AttendanceStatus.valueOf(statusName) } catch (_: Exception) { AttendanceStatus.PENDING }
                    val note = item.optString("note", "")
                    attList.add(CourseAttendanceWeek(weekNumber, label, status, note))
                }
            }
            val finalAttendance = if (attList.isNotEmpty()) attList else defaultAttendanceWeeks()

            return SchoolCourse(
                id = id,
                name = name,
                code = code,
                credits = credits,
                semester = semester,
                instructor = instructor,
                classroom = classroom,
                dayOfWeek = dayOfWeek,
                timeSlot = timeSlot,
                isOnline = isOnline,
                midtermGrade = midtermGrade,
                secondAssessmentGrade = secondAssessmentGrade,
                finalGrade = finalGrade,
                weights = weights,
                letterGrade = letterGrade,
                attendance = finalAttendance,
                isCompleted = isCompleted
            )
        }
    }
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

fun defaultSemesterCourses(): List<SchoolCourse> = listOf(
    SchoolCourse(
        id = "course_bmi4146",
        code = "BMI4146",
        name = "Data Mining",
        credits = 5,
        semester = 7,
        instructor = "Dr. Öğr. Üyesi B. Milani",
        classroom = "ED-K1-04 (Derslik 9)",
        dayOfWeek = "Pazartesi",
        timeSlot = "09:35 - 12:00",
        isOnline = false
    ),
    SchoolCourse(
        id = "course_bmi4143",
        code = "BMI4143",
        name = "Artificial Intelligence",
        credits = 5,
        semester = 7,
        instructor = "Doç. Dr. M. A. Çifçi",
        classroom = "ED-Z-21 (Derslik 3)",
        dayOfWeek = "Pazartesi",
        timeSlot = "12:50 - 15:15",
        isOnline = false
    ),
    SchoolCourse(
        id = "course_bmi3125",
        code = "BMI3125",
        name = "Occupational Health and Safety I",
        credits = 2,
        semester = 7,
        instructor = "Dr. Öğr. Üyesi Mehmet Sevi",
        classroom = "U.Ö. (Online)",
        dayOfWeek = "Salı",
        timeSlot = "10:25 - 12:00",
        isOnline = true
    ),
    SchoolCourse(
        id = "course_bmi4150",
        code = "BMI4150",
        name = "Introduction to Social Network Analysis",
        credits = 5,
        semester = 7,
        instructor = "Dr. Öğr. Üyesi A. Karataş",
        classroom = "ED-Z-26 (Derslik 6)",
        dayOfWeek = "Çarşamba",
        timeSlot = "09:35 - 12:00",
        isOnline = false
    ),
    SchoolCourse(
        id = "course_bmi4141",
        code = "BMI4141",
        name = "Mobile Programming",
        credits = 5,
        semester = 7,
        instructor = "Dr. Öğr. Üyesi E. Arıcan",
        classroom = "ED-K1-11 (Derslik 14)",
        dayOfWeek = "Çarşamba",
        timeSlot = "12:50 - 15:15",
        isOnline = false
    )
)

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
