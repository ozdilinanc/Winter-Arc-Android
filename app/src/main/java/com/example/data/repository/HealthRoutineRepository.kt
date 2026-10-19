package com.example.data.repository

import android.content.SharedPreferences

/** Uses the daily tracker's existing water storage so both screens share one record. */
object HealthRoutineRepository {
    const val PREFS_NAME = "winter_arc_daily_tracker"
    val habits = listOf("nutrition", "movement", "exercise", "sleep")

    val hobbies = linkedMapOf(
        "hab_reading" to "Kitap okuma",
        "hab_english" to "Clawssary / İngilizce",
        "hab_cards" to "Kart numarası çalışması",
        "hab_spanish" to "İspanyolca"
    )

    fun enabledHobbies(prefs: SharedPreferences): Set<String> =
        prefs.getStringSet("enabled_hobbies", setOf("hab_reading", "hab_english"))!!.intersect(hobbies.keys)

    fun setHobbyEnabled(prefs: SharedPreferences, id: String, enabled: Boolean) {
        require(id in hobbies)
        val selected = enabledHobbies(prefs).toMutableSet()
        if (enabled) selected.add(id) else selected.remove(id)
        prefs.edit().putStringSet("enabled_hobbies", selected).apply()
    }

    fun isHobbyCompleted(prefs: SharedPreferences, date: String, id: String): Boolean =
        prefs.getBoolean("${id}_$date", false)

    fun setHobbyCompleted(prefs: SharedPreferences, date: String, id: String, completed: Boolean) {
        require(id in hobbies)
        prefs.edit().putBoolean("${id}_$date", completed).apply()
    }

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
