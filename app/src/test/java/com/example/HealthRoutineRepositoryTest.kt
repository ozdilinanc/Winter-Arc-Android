package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RoadmapDataStore
import com.example.data.repository.HealthRoutineRepository as Routine
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HealthRoutineRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs get() = context.getSharedPreferences(Routine.PREFS_NAME, Context.MODE_PRIVATE)

    @Test
    fun habitsAreSavedPerDayAndCanBeUnchecked() {
        prefs.edit().clear().commit()
        Routine.setCompleted(prefs, "2026-10-19", "nutrition", true)
        val reloaded = context.getSharedPreferences(Routine.PREFS_NAME, Context.MODE_PRIVATE)
        assertTrue(Routine.isCompleted(reloaded, "2026-10-19", "nutrition"))
        assertFalse(Routine.isCompleted(reloaded, "2026-10-20", "nutrition"))
        assertFalse(Routine.isCompleted(reloaded, "2026-10-19", "sleep"))
        Routine.setCompleted(reloaded, "2026-10-19", "nutrition", false)
        assertFalse(Routine.isCompleted(prefs, "2026-10-19", "nutrition"))
    }

    @Test
    fun waterUsesExistingDailyTrackerRecordAndRespectsBounds() {
        prefs.edit().clear().putInt("water_ml_2026-10-19", 1000).putInt("water_target_ml", 2500).commit()
        Routine.addWater(prefs, "2026-10-19", 250)
        assertEquals(1250, prefs.getInt("water_ml_2026-10-19", 0))
        assertEquals(0, Routine.waterMl(prefs, "2026-10-20"))
        Routine.addWater(prefs, "2026-10-19", -2000)
        assertEquals(0, Routine.waterMl(prefs, "2026-10-19"))
        Routine.addWater(prefs, "2026-10-19", 6000)
        assertEquals(5000, Routine.waterMl(prefs, "2026-10-19"))
        assertEquals(2500, prefs.getInt("water_target_ml", 0))
    }

    @Test
    fun healthRoutineHasNoLegacyLearningChecklist() {
        val roadmap = RoadmapDataStore.allRoadmaps.getValue("sub_winter_arc_discipline")
        assertEquals("Sağlık & Rutin", roadmap.title)
        assertTrue(roadmap.sections.isEmpty())
    }
}
