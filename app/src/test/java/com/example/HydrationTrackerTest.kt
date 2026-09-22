package com.example

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.DayHydrationStat
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HydrationTrackerTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var todayKey: String

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences("winter_arc_daily_tracker", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    @Test
    fun verifyDefaultTargetWaterAndPersistence() {
        val defaultTarget = prefs.getInt("water_target_ml", 3000)
        assertEquals(3000, defaultTarget)

        // Change target to 2500ml
        prefs.edit().putInt("water_target_ml", 2500).commit()
        val updatedTarget = prefs.getInt("water_target_ml", 3000)
        assertEquals(2500, updatedTarget)

        // Change target to 3500ml
        prefs.edit().putInt("water_target_ml", 3500).commit()
        assertEquals(3500, prefs.getInt("water_target_ml", 3000))
    }

    @Test
    fun verifyWaterIntakeLoggingAndReset() {
        var water = prefs.getInt("water_ml_$todayKey", 0)
        assertEquals(0, water)

        // Add 200ml (Quick Drink)
        water = (water + 200).coerceIn(0, 5000)
        prefs.edit().putInt("water_ml_$todayKey", water).commit()
        assertEquals(200, prefs.getInt("water_ml_$todayKey", 0))

        // Add 500ml
        water = (water + 500).coerceIn(0, 5000)
        prefs.edit().putInt("water_ml_$todayKey", water).commit()
        assertEquals(700, prefs.getInt("water_ml_$todayKey", 0))

        // Reset
        water = 0
        prefs.edit().putInt("water_ml_$todayKey", water).commit()
        assertEquals(0, prefs.getInt("water_ml_$todayKey", 0))
    }

    @Test
    fun verifyMultipleBeverageTracking() {
        val drinks = listOf("soda", "coffee", "tea", "juice", "sparkling", "energy")

        // Initial values are 0
        drinks.forEach { drinkId ->
            val consumed = prefs.getInt("drink_${drinkId}_$todayKey", 0)
            assertEquals(0, consumed)
        }

        // Log 330ml soda (1 can)
        prefs.edit().putInt("drink_soda_$todayKey", 330).commit()
        assertEquals(330, prefs.getInt("drink_soda_$todayKey", 0))

        // Log 200ml coffee (1 cup)
        prefs.edit().putInt("drink_coffee_$todayKey", 200).commit()
        assertEquals(200, prefs.getInt("drink_coffee_$todayKey", 0))

        // Log 150ml tea
        prefs.edit().putInt("drink_tea_$todayKey", 150).commit()
        assertEquals(150, prefs.getInt("drink_tea_$todayKey", 0))

        // Add another soda: 330 + 330 = 660ml
        val currentSoda = prefs.getInt("drink_soda_$todayKey", 0)
        prefs.edit().putInt("drink_soda_$todayKey", currentSoda + 330).commit()
        assertEquals(660, prefs.getInt("drink_soda_$todayKey", 0))
    }

    @Test
    fun verifyWeeklyStatsCalculation() {
        val statNormal = DayHydrationStat(
            dayLabel = "Mon",
            dayShort = "Mon",
            dateKey = "2026-09-22",
            consumedMl = 1500,
            targetMl = 3000,
            isToday = true
        )
        assertEquals(50f, statNormal.percentage, 0.01f)

        val statExceeded = DayHydrationStat(
            dayLabel = "Tue",
            dayShort = "Tue",
            dateKey = "2026-09-23",
            consumedMl = 3500,
            targetMl = 3000,
            isToday = false
        )
        // Capped at 100%
        assertEquals(100f, statExceeded.percentage, 0.01f)

        val statZero = DayHydrationStat(
            dayLabel = "Wed",
            dayShort = "Web",
            dateKey = "2026-09-24",
            consumedMl = 0,
            targetMl = 3000,
            isToday = false
        )
        assertEquals(0f, statZero.percentage, 0.01f)
    }
}
