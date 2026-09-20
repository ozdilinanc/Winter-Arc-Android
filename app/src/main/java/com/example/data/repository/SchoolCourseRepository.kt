package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.components.school.AttendanceStatus
import com.example.ui.components.school.CourseAttendanceWeek
import com.example.ui.components.school.SchoolCourse
import com.example.ui.components.school.defaultSemesterCourses
import org.json.JSONArray
import org.json.JSONObject

object SchoolCourseRepository {

    const val PREFS_SCHOOL = "winter_arc_school_data"
    const val KEY_COURSES = "school_courses_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_SCHOOL, Context.MODE_PRIVATE)
    }

    @Synchronized
    fun getCourses(context: Context): List<SchoolCourse> {
        val prefs = getPrefs(context)
        if (!prefs.contains(KEY_COURSES)) {
            val defaults = defaultSemesterCourses()
            saveCourses(context, defaults)
            return defaults
        }

        val jsonString = prefs.getString(KEY_COURSES, null) ?: return defaultSemesterCourses()
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<SchoolCourse>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i) ?: continue
                list.add(SchoolCourse.fromJson(obj))
            }
            if (list.isEmpty()) {
                val defaults = defaultSemesterCourses()
                saveCourses(context, defaults)
                defaults
            } else {
                list
            }
        } catch (e: Exception) {
            val defaults = defaultSemesterCourses()
            saveCourses(context, defaults)
            defaults
        }
    }

    @Synchronized
    fun saveCourses(context: Context, courses: List<SchoolCourse>) {
        val jsonArray = JSONArray()
        courses.forEach { course ->
            jsonArray.put(course.toJson())
        }
        getPrefs(context).edit()
            .putString(KEY_COURSES, jsonArray.toString())
            .apply()
    }

    @Synchronized
    fun updateCourse(context: Context, updatedCourse: SchoolCourse): List<SchoolCourse> {
        val current = getCourses(context)
        val updatedList = current.map { if (it.id == updatedCourse.id) updatedCourse else it }
        saveCourses(context, updatedList)
        return updatedList
    }

    @Synchronized
    fun addCourse(context: Context, course: SchoolCourse): List<SchoolCourse> {
        val current = getCourses(context)
        val updatedList = current + course
        saveCourses(context, updatedList)
        return updatedList
    }

    @Synchronized
    fun deleteCourse(context: Context, courseId: String): List<SchoolCourse> {
        val current = getCourses(context)
        val updatedList = current.filter { it.id != courseId }
        saveCourses(context, updatedList)
        return updatedList
    }

    @Synchronized
    fun updateAttendance(
        context: Context,
        courseId: String,
        weekNumber: Int,
        status: AttendanceStatus,
        note: String? = null
    ): List<SchoolCourse> {
        val current = getCourses(context)
        val updatedList = current.map { course ->
            if (course.id == courseId) {
                val updatedAttendance = course.attendance.map { week ->
                    if (week.weekNumber == weekNumber) {
                        week.copy(
                            status = status,
                            note = note ?: week.note
                        )
                    } else week
                }
                course.copy(attendance = updatedAttendance)
            } else course
        }
        saveCourses(context, updatedList)
        return updatedList
    }

    @Synchronized
    fun resetToDefaults(context: Context): List<SchoolCourse> {
        val defaults = defaultSemesterCourses()
        saveCourses(context, defaults)
        return defaults
    }
}
