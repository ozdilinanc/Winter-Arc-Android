package com.example

import android.content.Context
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.test.core.app.ApplicationProvider
import com.example.ui.util.HealthSyncManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HealthSyncManagerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("winter_arc_health_sync", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun testWalkStepTargetThresholds() {
        assertEquals(7000L, HealthSyncManager.WALK_STEP_TARGET)
        assertFalse("6999 steps should not meet the daily walking goal", HealthSyncManager.isWalkTargetAchieved(6999L))
        assertFalse("0 steps should not meet the goal", HealthSyncManager.isWalkTargetAchieved(0L))
        assertTrue("7000 steps should meet the daily walking goal", HealthSyncManager.isWalkTargetAchieved(7000L))
        assertTrue("10500 steps should meet the daily walking goal", HealthSyncManager.isWalkTargetAchieved(10500L))
    }

    @Test
    fun testSleepTargetThresholds() {
        assertEquals(6.0, HealthSyncManager.SLEEP_HOURS_TARGET, 0.001)
        assertFalse("5.9 hours should not meet 6h+ target", HealthSyncManager.isSleepTargetAchieved(5.9))
        assertFalse("0.0 hours should not meet target", HealthSyncManager.isSleepTargetAchieved(0.0))
        assertTrue("6.0 hours exactly should meet target", HealthSyncManager.isSleepTargetAchieved(6.0))
        assertTrue("7.5 hours should meet target", HealthSyncManager.isSleepTargetAchieved(7.5))
        assertTrue("9.0 hours should meet target", HealthSyncManager.isSleepTargetAchieved(9.0))
    }

    @Test
    fun testSleepQualityDetermination() {
        // >= 7.5 hours -> refreshed
        assertEquals("refreshed", HealthSyncManager.determineSleepQuality(7.5))
        assertEquals("refreshed", HealthSyncManager.determineSleepQuality(8.2))
        assertEquals("refreshed", HealthSyncManager.determineSleepQuality(9.0))

        // 6.0 <= hours < 7.5 -> normal
        assertEquals("normal", HealthSyncManager.determineSleepQuality(6.0))
        assertEquals("normal", HealthSyncManager.determineSleepQuality(6.8))
        assertEquals("normal", HealthSyncManager.determineSleepQuality(7.49))

        // < 6.0 hours -> tired
        assertEquals("tired", HealthSyncManager.determineSleepQuality(5.99))
        assertEquals("tired", HealthSyncManager.determineSleepQuality(4.5))
        assertEquals("tired", HealthSyncManager.determineSleepQuality(0.0))
    }

    @Test
    fun testRequiredHealthPermissionsContainsStepsAndSleep() {
        val permissions = HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS
        assertEquals(2, permissions.size)
        assertTrue(permissions.contains(HealthPermission.getReadPermission(StepsRecord::class)))
        assertTrue(permissions.contains(HealthPermission.getReadPermission(SleepSessionRecord::class)))
    }

    @Test
    fun testSaveAndGetLastSync() {
        val todayKey = "2026_09_13"
        assertNull("Should be null initially", HealthSyncManager.getLastSync(context, todayKey))

        val syncResult = HealthSyncManager.HealthSyncResult(
            stepsCount = 8450L,
            sleepHours = 7.25,
            sleepMinutesTotal = 435L,
            sleepQuality = "normal",
            isSleep6hPlus = true,
            isWalkGoalMet = true,
            source = "Health Connect (Huawei Sağlık ⌚)",
            syncedAtMillis = 1726225200000L,
            isSuccess = true
        )

        HealthSyncManager.saveLastSync(context, todayKey, syncResult)

        val retrieved = HealthSyncManager.getLastSync(context, todayKey)
        assertNotNull(retrieved)
        assertEquals(8450L, retrieved!!.stepsCount)
        assertEquals(7.25, retrieved.sleepHours, 0.01)
        assertEquals(435L, retrieved.sleepMinutesTotal)
        assertEquals("normal", retrieved.sleepQuality)
        assertTrue(retrieved.isSleep6hPlus)
        assertTrue(retrieved.isWalkGoalMet)
        assertEquals("Health Connect (Huawei Sağlık ⌚)", retrieved.source)
        assertEquals(1726225200000L, retrieved.syncedAtMillis)
    }
}
