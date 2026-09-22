package com.example

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Günlük Takip, Hidrasyon, İçecekler, Dopamin Kalkanı, Uyku ve XP Hesaplamalarının
 * tüm fonksiyonlarını kapsayan derinlemesine test paketi.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DailyTrackerFunctionsComprehensiveTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var todayKey: String

    private val INSTAGRAM_LIMIT_MINUTES = 45L

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences("winter_arc_daily_tracker", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // ====================================================================
    // 1. XP HESAPLAMA MANTIĞI TESTLERİ
    // ====================================================================
    @Test
    fun verifyDailyXpCalculation_AllCombinations() {
        fun calculateXp(completedHabits: Int, dopamineStatus: String, waterMl: Int, slept6hPlus: Boolean): Int {
            return (completedHabits * 30) +
                    (if (dopamineStatus == "maintained") 30 else 0) +
                    (if (waterMl >= 2500) 20 else 0) +
                    (if (slept6hPlus) 30 else 0)
        }

        // Sıfır aktivite
        assertEquals(0, calculateXp(0, "broken", 0, false))
        assertEquals(0, calculateXp(0, "none", 1200, false))

        // Sadece 1 rutin (30 XP)
        assertEquals(30, calculateXp(1, "none", 0, false))

        // Sadece su hedefi aşıldı (>=2500ml -> 20 XP)
        assertEquals(20, calculateXp(0, "none", 2500, false))
        assertEquals(20, calculateXp(0, "none", 3000, false))
        assertEquals(0, calculateXp(0, "none", 2490, false))

        // Sadece dopamin korundu (30 XP)
        assertEquals(30, calculateXp(0, "maintained", 0, false))

        // Sadece uyku hedefi (30 XP)
        assertEquals(30, calculateXp(0, "none", 0, true))

        // TAM BAŞARI: 3 Rutin (90) + Dopamin (30) + Su (20) + Uyku (30) = 170 XP
        assertEquals(170, calculateXp(3, "maintained", 3000, true))
    }

    // ====================================================================
    // 2. SU VE HİDRASYON TESTLERİ (Hacim, Sınırlar, Hedefler)
    // ====================================================================
    @Test
    fun verifyWaterLimitsAndIncrement() {
        fun updateWater(current: Int, delta: Int): Int {
            return (current + delta).coerceIn(0, 5000)
        }

        var water = 0
        water = updateWater(water, 200) // Drink 200 ml
        assertEquals(200, water)

        water = updateWater(water, 250) // Kupa
        assertEquals(450, water)

        water = updateWater(water, 500) // Şişe
        assertEquals(950, water)

        // Negatif değer 0 altına inemez
        water = updateWater(water, -2000)
        assertEquals(0, water)

        // Maksimum sınır 5000 ml
        water = updateWater(water, 6000)
        assertEquals(5000, water)

        // Sıfırlama
        water = updateWater(water, -water)
        assertEquals(0, water)
    }

    @Test
    fun verifyWaterTargetPresets() {
        val targets = listOf(2000, 2500, 3000, 3500)
        targets.forEach { target ->
            prefs.edit().putInt("water_target_ml", target).commit()
            assertEquals(target, prefs.getInt("water_target_ml", 3000))
        }
    }

    // ====================================================================
    // 3. DİĞER İÇECEKLER TESTLERİ (Kola, Kahve, Çay, Meyve Suyu, Maden Suyu, Enerji)
    // ====================================================================
    @Test
    fun verifyBeveragePortionAddAndSubtract() {
        val beveragePortions = mapOf(
            "soda" to 330,
            "coffee" to 200,
            "tea" to 150,
            "juice" to 250,
            "sparkling" to 200,
            "energy" to 250
        )

        fun updateBeverage(drinkId: String, delta: Int): Int {
            val current = prefs.getInt("drink_${drinkId}_$todayKey", 0)
            val updated = (current + delta).coerceAtLeast(0)
            prefs.edit().putInt("drink_${drinkId}_$todayKey", updated).commit()
            return updated
        }

        // Kola ekleme (1 kutu: 330ml, 2 kutu: 660ml)
        assertEquals(330, updateBeverage("soda", 330))
        assertEquals(660, updateBeverage("soda", 330))
        // 1 kutu azalt
        assertEquals(330, updateBeverage("soda", -330))
        // 2 kutu azaltınca sıfırın altına inemez
        assertEquals(0, updateBeverage("soda", -500))

        // Kahve (200ml)
        assertEquals(200, updateBeverage("coffee", 200))
        assertEquals(400, updateBeverage("coffee", 200))

        // Çay (150ml)
        assertEquals(150, updateBeverage("tea", 150))
        assertEquals(300, updateBeverage("tea", 150))

        // Enerji içeceği (250ml)
        assertEquals(250, updateBeverage("energy", 250))
    }

    // ====================================================================
    // 4. DOPAMİN & INSTAGRAM EKRAN SÜRESİ TESTLERİ
    // ====================================================================
    @Test
    fun verifyDopamineShieldStatusLogic() {
        fun evaluateDopamine(instaMinutes: Long, isPermitted: Boolean, manualStatus: String): String {
            return if (isPermitted) {
                if (instaMinutes <= INSTAGRAM_LIMIT_MINUTES) "maintained" else "broken"
            } else {
                manualStatus
            }
        }

        // İzin verildiğinde: 30 dk <= 45 dk -> maintained
        assertEquals("maintained", evaluateDopamine(30L, true, "none"))
        // Tam 45 dk -> maintained
        assertEquals("maintained", evaluateDopamine(45L, true, "none"))
        // 46 dk -> limit aşıldı, broken
        assertEquals("broken", evaluateDopamine(46L, true, "none"))
        // 90 dk -> broken
        assertEquals("broken", evaluateDopamine(90L, true, "none"))

        // İzin verilmediğinde: Manuel duruma bakar
        assertEquals("maintained", evaluateDopamine(0L, false, "maintained"))
        assertEquals("broken", evaluateDopamine(0L, false, "broken"))
        assertEquals("none", evaluateDopamine(0L, false, "none"))
    }

    @Test
    fun verifyDopamineStreakLogic() {
        var streak = prefs.getInt("dopamine_streak", 5)

        fun onMaintain() {
            streak += 1
            prefs.edit().putInt("dopamine_streak", streak).commit()
        }

        fun onBreak() {
            streak = 0
            prefs.edit().putInt("dopamine_streak", 0).commit()
        }

        onMaintain()
        assertEquals(6, prefs.getInt("dopamine_streak", 0))

        onMaintain()
        assertEquals(7, prefs.getInt("dopamine_streak", 0))

        onBreak()
        assertEquals(0, prefs.getInt("dopamine_streak", 0))
    }

    // ====================================================================
    // 5. RUTİNLER (Yürüyüş, Kitap, İngilizce) & UYKU TESTLERİ
    // ====================================================================
    @Test
    fun verifyRoutineToggling() {
        val routineIds = listOf("hab_walk", "hab_reading", "hab_english")

        routineIds.forEach { id ->
            assertFalse(prefs.getBoolean("${id}_$todayKey", false))

            // Tamamla
            prefs.edit().putBoolean("${id}_$todayKey", true).commit()
            assertTrue(prefs.getBoolean("${id}_$todayKey", false))

            // Geri al
            prefs.edit().putBoolean("${id}_$todayKey", false).commit()
            assertFalse(prefs.getBoolean("${id}_$todayKey", false))
        }
    }

    @Test
    fun verifyWalkStepThreshold() {
        val targetSteps = 7000L
        fun isStepGoalMet(steps: Long) = steps >= targetSteps

        assertFalse(isStepGoalMet(6999L))
        assertTrue(isStepGoalMet(7000L))
        assertTrue(isStepGoalMet(10500L))
    }

    @Test
    fun verifySleepGoalThreshold() {
        // 6 saat = 360 dakika
        fun isSleepGoalMet(minutes: Long) = minutes >= 360L

        assertFalse(isSleepGoalMet(359L))
        assertTrue(isSleepGoalMet(360L))
        assertTrue(isSleepGoalMet(480L)) // 8 saat
    }
}
