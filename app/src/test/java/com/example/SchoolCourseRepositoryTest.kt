package com.example

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SchoolCourseRepository
import com.example.ui.components.school.AttendanceStatus
import com.example.ui.components.school.GradeWeights
import com.example.ui.components.school.SchoolCourse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SchoolCourseRepositoryTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences(SchoolCourseRepository.PREFS_SCHOOL, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun verifyDefaultCoursesLoadedCorrectly() {
        val courses = SchoolCourseRepository.getCourses(context)
        assertEquals("Tam olarak 5 ders yüklenmeli", 5, courses.size)

        val totalCredits = courses.sumOf { it.credits }
        assertEquals("Toplam kredi tam olarak 22 AKTS olmalı", 22, totalCredits)

        val codes = courses.map { it.code }
        assertTrue("BMI4146 Data Mining bulunmalı", codes.contains("BMI4146"))
        assertTrue("BMI4143 Artificial Intelligence bulunmalı", codes.contains("BMI4143"))
        assertTrue("BMI3125 İş Sağlığı ve Güvenliği bulunmalı", codes.contains("BMI3125"))
        assertTrue("BMI4150 Social Network Analysis bulunmalı", codes.contains("BMI4150"))
        assertTrue("BMI4141 Mobile Programming bulunmalı", codes.contains("BMI4141"))

        // Kontrol: İş Sağlığı ve Güvenliği 2 kredi ve U.Ö. (online)
        val isgCourse = courses.find { it.code == "BMI3125" }
        assertNotNull(isgCourse)
        assertEquals(2, isgCourse!!.credits)
        assertTrue("BMI3125 online (U.Ö.) olmalı", isgCourse.isOnline)
        assertEquals("Salı", isgCourse.dayOfWeek)
        assertEquals("Dr. Öğr. Üyesi Mehmet Sevi", isgCourse.instructor)

        // Kontrol: Data Mining 5 kredi, Pazartesi
        val dataMining = courses.find { it.code == "BMI4146" }
        assertNotNull(dataMining)
        assertEquals(5, dataMining!!.credits)
        assertEquals("Pazartesi", dataMining.dayOfWeek)
        assertEquals("ED-K1-04 (Derslik 9)", dataMining.classroom)
        assertEquals("Dr. Öğr. Üyesi B. Milani", dataMining.instructor)

        // Kontrol: Yapay Zeka 5 kredi, Pazartesi
        val ai = courses.find { it.code == "BMI4143" }
        assertNotNull(ai)
        assertEquals(5, ai!!.credits)
        assertEquals("Pazartesi", ai.dayOfWeek)
        assertEquals("ED-Z-21 (Derslik 3)", ai.classroom)

        // Kontrol: Sosyal Ağlar 5 kredi, Çarşamba
        val sna = courses.find { it.code == "BMI4150" }
        assertNotNull(sna)
        assertEquals(5, sna!!.credits)
        assertEquals("Çarşamba", sna.dayOfWeek)
        assertEquals("ED-Z-26 (Derslik 6)", sna.classroom)

        // Kontrol: Mobile Programming 5 kredi, Çarşamba
        val mob = courses.find { it.code == "BMI4141" }
        assertNotNull(mob)
        assertEquals(5, mob!!.credits)
        assertEquals("Çarşamba", mob.dayOfWeek)
        assertEquals("ED-K1-11 (Derslik 14)", mob.classroom)
    }

    @Test
    fun verifyAttendanceUpdatePersistedToStorage() {
        val initialCourses = SchoolCourseRepository.getCourses(context)
        val targetCourse = initialCourses.first { it.code == "BMI4146" }

        // 1. Hafta: Katıldı
        SchoolCourseRepository.updateAttendance(
            context = context,
            courseId = targetCourse.id,
            weekNumber = 1,
            status = AttendanceStatus.ATTENDED,
            note = "Ders çok verimli geçti"
        )

        // 2. Hafta: Devamsız
        SchoolCourseRepository.updateAttendance(
            context = context,
            courseId = targetCourse.id,
            weekNumber = 2,
            status = AttendanceStatus.ABSENT,
            note = "Hastanede randevu"
        )

        // Uygulama yeniden başlatılmış gibi repository'den tekrar oku
        val reloadedCourses = SchoolCourseRepository.getCourses(context)
        val reloadedCourse = reloadedCourses.first { it.id == targetCourse.id }

        val week1 = reloadedCourse.attendance.find { it.weekNumber == 1 }
        assertNotNull(week1)
        assertEquals(AttendanceStatus.ATTENDED, week1!!.status)
        assertEquals("Ders çok verimli geçti", week1.note)

        val week2 = reloadedCourse.attendance.find { it.weekNumber == 2 }
        assertNotNull(week2)
        assertEquals(AttendanceStatus.ABSENT, week2!!.status)
        assertEquals("Hastanede randevu", week2.note)

        assertEquals("Devamsız sayısı 1 olmalı", 1, reloadedCourse.absentCount)
        assertEquals("Kalan hak 3 olmalı", 3, reloadedCourse.remainingAbsenceRights)
    }

    @Test
    fun verifyGradesAndWeightsPersisted() {
        val initialCourses = SchoolCourseRepository.getCourses(context)
        val mobileCourse = initialCourses.first { it.code == "BMI4141" }

        val updatedWithGrades = mobileCourse.copy(
            midtermGrade = 88.0,
            secondAssessmentGrade = 92.0,
            finalGrade = 95.0,
            letterGrade = "AA",
            weights = GradeWeights(midtermWeight = 40, secondAssessmentWeight = 10, finalWeight = 50),
            isCompleted = true
        )
        SchoolCourseRepository.updateCourse(context, updatedWithGrades)

        // Tekrar diskten oku
        val reloadedCourses = SchoolCourseRepository.getCourses(context)
        val reloaded = reloadedCourses.first { it.id == mobileCourse.id }

        assertEquals(88.0, reloaded.midtermGrade!!, 0.01)
        assertEquals(92.0, reloaded.secondAssessmentGrade!!, 0.01)
        assertEquals(95.0, reloaded.finalGrade!!, 0.01)
        assertEquals("AA", reloaded.letterGrade)
        assertTrue(reloaded.isCompleted)
        assertEquals(40, reloaded.weights.midtermWeight)
        assertEquals(10, reloaded.weights.secondAssessmentWeight)
        assertEquals(50, reloaded.weights.finalWeight)

        // Ağırlıklı ortalama: (88*40 + 92*10 + 95*50) / 100 = (3520 + 920 + 4750) / 100 = 91.9
        assertNotNull(reloaded.calculatedAverage)
        assertEquals(91.9, reloaded.calculatedAverage!!, 0.05)
    }

    @Test
    fun verifyAddAndDeleteCoursePersisted() {
        val initialCourses = SchoolCourseRepository.getCourses(context)
        assertEquals(5, initialCourses.size)

        val newCourse = SchoolCourse(
            id = UUID.randomUUID().toString(),
            code = "BMI4145",
            name = "Compiler Design",
            credits = 5,
            semester = 7,
            instructor = "Dr. Öğr. Üyesi M. Milani",
            classroom = "ED-Z-21 (Derslik 3)",
            dayOfWeek = "Perşembe",
            timeSlot = "12:50 - 15:15"
        )
        SchoolCourseRepository.addCourse(context, newCourse)

        val afterAdd = SchoolCourseRepository.getCourses(context)
        assertEquals(6, afterAdd.size)
        assertEquals(27, afterAdd.sumOf { it.credits })
        assertTrue(afterAdd.any { it.code == "BMI4145" })

        // Sil
        SchoolCourseRepository.deleteCourse(context, newCourse.id)
        val afterDelete = SchoolCourseRepository.getCourses(context)
        assertEquals(5, afterDelete.size)
        assertEquals(22, afterDelete.sumOf { it.credits })
        assertFalse(afterDelete.any { it.code == "BMI4145" })
    }

    @Test
    fun verifyJsonRoundTripIntegrity() {
        val course = SchoolCourse(
            id = "test_course_id",
            code = "TEST101",
            name = "Test Course",
            credits = 4,
            semester = 8,
            instructor = "Prof. Dr. Test",
            classroom = "A-101",
            dayOfWeek = "Cuma",
            timeSlot = "10:00 - 12:00",
            isOnline = false,
            midtermGrade = 75.5,
            finalGrade = 82.0,
            letterGrade = "BA",
            isCompleted = true
        )

        val json = course.toJson()
        val restored = SchoolCourse.fromJson(json)

        assertEquals(course.id, restored.id)
        assertEquals(course.code, restored.code)
        assertEquals(course.name, restored.name)
        assertEquals(course.credits, restored.credits)
        assertEquals(course.semester, restored.semester)
        assertEquals(course.instructor, restored.instructor)
        assertEquals(course.classroom, restored.classroom)
        assertEquals(course.dayOfWeek, restored.dayOfWeek)
        assertEquals(course.timeSlot, restored.timeSlot)
        assertEquals(course.isOnline, restored.isOnline)
        assertEquals(course.midtermGrade, restored.midtermGrade)
        assertEquals(course.finalGrade, restored.finalGrade)
        assertEquals(course.letterGrade, restored.letterGrade)
        assertEquals(course.isCompleted, restored.isCompleted)
        assertEquals(16, restored.attendance.size)
    }
}
