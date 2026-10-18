package com.example.data.repository

import android.content.SharedPreferences

/** Uses the daily tracker's existing water storage so both screens share one record. */
object HealthRoutineRepository {
    const val PREFS_NAME = "winter_arc_daily_tracker"
    val habits = listOf("nutrition", "movement", "sleep")

    fun isCompleted(prefs: SharedPreferences, date: String, habit: String): Boolean =
        prefs.getBoolean("health_${habit}_$date", false)

    fun setCompleted(prefs: SharedPreferences, date: String, habit: String, completed: Boolean) {
        require(habit in habits)
        prefs.edit().putBoolean("health_${habit}_$date", completed).apply()
    }

    fun waterMl(prefs: SharedPreferences, date: String): Int = prefs.getInt("water_ml_$date", 0)

    fun addWater(prefs: SharedPreferences, date: String, delta: Int) {
        val amount = (waterMl(prefs, date).toLong() + delta).coerceIn(0L, 5000L).toInt()
        prefs.edit().putInt("water_ml_$date", amount).apply()
    }
}
