package com.example

import android.content.Context
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.records.metadata.Metadata
import androidx.test.core.app.ApplicationProvider
import com.example.ui.util.HealthSyncManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HealthSyncManagerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        for (name in listOf("winter_arc_health_sync", "winter_arc_huawei_sync_v2")) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit()
        }
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
        assertEquals(4, permissions.size)
        assertTrue(permissions.contains(HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)))
        assertTrue(permissions.contains(HealthPermission.getReadPermission(ExerciseSessionRecord::class)))
        assertTrue(permissions.contains(HealthPermission.getReadPermission(StepsRecord::class)))
        assertTrue(permissions.contains(HealthPermission.getReadPermission(SleepSessionRecord::class)))
    }

    @Test
    fun testSaveAndGetLastSync() {
        val todayKey = "2026-09-13"
        assertNull("Should be null initially", HealthSyncManager.getLastSync(context, todayKey))

        val syncResult = HealthSyncManager.HealthSyncResult(
            stepsCount = 8450L,
            sleepHours = 7.25,
            sleepMinutesTotal = 435L,
            sleepQuality = "normal",
            isSleep6hPlus = true,
            isWalkGoalMet = true,
            source = HealthSyncManager.HUAWEI_SOURCE,
            syncedAtMillis = 1726225200000L,
            isSuccess = true,
            hasStepsData = true,
            hasSleepData = true,
            dateKey = todayKey
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
        assertEquals(HealthSyncManager.HUAWEI_SOURCE, retrieved.source)
        assertTrue(retrieved.hasStepsData)
        assertTrue(retrieved.hasSleepData)
        assertEquals(1726225200000L, retrieved.syncedAtMillis)
    }

    private val zone = ZoneId.of("Europe/Istanbul")
    private val date = LocalDate.of(2026, 10, 14)
    private val now = Instant.parse("2026-10-14T12:00:00Z")

    private fun sleep(
        start: String,
        end: String,
        origin: String = HealthSyncManager.HUAWEI_HEALTH_PACKAGE,
        stages: List<SleepSessionRecord.Stage> = emptyList()
    ) = SleepSessionRecord(
        startTime = Instant.parse(start), startZoneOffset = null,
        endTime = Instant.parse(end), endZoneOffset = null,
        metadata = Metadata(dataOrigin = DataOrigin(origin)), stages = stages
    )

    private fun minutes(vararg records: SleepSessionRecord) =
        HealthSyncManager.calculateHuaweiSleepMinutes(records.toList(), date, zone, now)

    @Test
    fun sleepIncludesWholeNightCrossingMidnight() {
        assertEquals(480L, minutes(sleep("2026-10-13T20:00:00Z", "2026-10-14T04:00:00Z")))
    }

    @Test
    fun sleepRejectsOtherWritersIncludingHuaweiBridge() {
        val records = listOf("com.google.android.apps.fitness", "com.samsung.android.shealth",
            "nl.appyhapps.healthsync").map {
            sleep("2026-10-13T20:00:00Z", "2026-10-14T04:00:00Z", origin = it)
        }
        assertNull(minutes(*records.toTypedArray()))
    }

    @Test
    fun sleepDoesNotReusePreviousNightOrFutureSession() {
        assertNull(minutes(
            sleep("2026-10-12T20:00:00Z", "2026-10-13T04:00:00Z"),
            sleep("2026-10-14T11:00:00Z", "2026-10-14T13:00:00Z")
        ))
    }

    @Test
    fun wakeUpDateUsesLocalTimezoneRatherThanUtcDate() {
        // 22:00 UTC is 01:00 the next day in Istanbul.
        assertEquals(180L, minutes(sleep("2026-10-13T19:00:00Z", "2026-10-13T22:00:00Z")))
    }

    @Test
    fun duplicateAndOverlappingSleepSessionsAreCountedOnce() {
        val night = sleep("2026-10-13T20:00:00Z", "2026-10-14T04:00:00Z")
        val overlap = sleep("2026-10-14T03:00:00Z", "2026-10-14T05:00:00Z")
        assertEquals(540L, minutes(night, night, overlap))
    }

    @Test
    fun splitSleepAndNapAreAddedWithoutCountingGaps() {
        assertEquals(420L, minutes(
            sleep("2026-10-13T21:00:00Z", "2026-10-14T01:00:00Z"),
            sleep("2026-10-14T02:00:00Z", "2026-10-14T04:00:00Z"),
            sleep("2026-10-14T10:00:00Z", "2026-10-14T11:00:00Z")
        ))
    }

    @Test
    fun sleepStagesExcludeAwakeAndOutOfBedTime() {
        fun stage(start: String, end: String, type: Int) =
            SleepSessionRecord.Stage(Instant.parse(start), Instant.parse(end), type)
        val night = sleep("2026-10-13T21:00:00Z", "2026-10-14T05:00:00Z", stages = listOf(
            stage("2026-10-13T21:00:00Z", "2026-10-14T01:00:00Z", SleepSessionRecord.STAGE_TYPE_DEEP),
            stage("2026-10-14T01:00:00Z", "2026-10-14T02:00:00Z", SleepSessionRecord.STAGE_TYPE_AWAKE),
            stage("2026-10-14T02:00:00Z", "2026-10-14T04:00:00Z", SleepSessionRecord.STAGE_TYPE_REM),
            stage("2026-10-14T04:00:00Z", "2026-10-14T05:00:00Z", SleepSessionRecord.STAGE_TYPE_OUT_OF_BED)
        ))
        assertEquals(360L, minutes(night, night))
        val unstagedSummary = sleep("2026-10-13T21:00:00Z", "2026-10-14T05:00:00Z")
        assertEquals(360L, minutes(night, unstagedSummary))
    }

    @Test
    fun roundsAfterCombiningIntervalsRatherThanLosingSecondsPerSegment() {
        assertEquals(1L, minutes(
            sleep("2026-10-14T03:00:00Z", "2026-10-14T03:00:40Z"),
            sleep("2026-10-14T04:00:00Z", "2026-10-14T04:00:40Z")
        ))
    }

    @Test
    fun oldMixedCacheIsNeverDisplayedAsHuaweiData() {
        context.getSharedPreferences("winter_arc_health_sync", Context.MODE_PRIVATE).edit()
            .putLong("sync_time_$date", now.toEpochMilli())
            .putLong("sync_steps_$date", 25000L)
            .putString("sync_source_$date", "Huawei & Google Fit").commit()
        assertNull(HealthSyncManager.getLastSync(context, date.toString()))
    }

    @Test
    fun failuresAndForeignSourcesCannotOverwriteVerifiedCache() {
        val key = date.toString()
        val verified = HealthSyncManager.HealthSyncResult(
            stepsCount = 7100L, hasStepsData = true, isSuccess = true, dateKey = key)
        HealthSyncManager.saveLastSync(context, key, verified)
        HealthSyncManager.saveLastSync(context, key, verified.copy(stepsCount = 0L, isSuccess = false))
        HealthSyncManager.saveLastSync(context, key, verified.copy(stepsCount = 99999L, source = "Google Fit"))
        assertEquals(7100L, HealthSyncManager.getLastSync(context, key)!!.stepsCount)
    }

    @Test
    fun zeroStepsRemainDifferentFromMissingData() {
        val key = date.toString()
        HealthSyncManager.saveLastSync(context, key, HealthSyncManager.HealthSyncResult(
            stepsCount = 0L, hasStepsData = true, isSuccess = true, dateKey = key))
        val result = HealthSyncManager.getLastSync(context, key)!!
        assertTrue(result.hasStepsData)
        assertFalse(result.hasSleepData)
        assertEquals(0L, result.stepsCount)
        assertFalse(result.isWalkGoalMet)
    }

    @Test
    fun midnightResultCannotBeCachedUnderAnotherDay() {
        HealthSyncManager.saveLastSync(context, date.plusDays(1).toString(),
            HealthSyncManager.HealthSyncResult(stepsCount = 7100L, hasStepsData = true,
                isSuccess = true, dateKey = date.toString()))
        assertNull(HealthSyncManager.getLastSync(context, date.plusDays(1).toString()))
    }

    private fun exercise(start: String, end: String, origin: String = HealthSyncManager.HUAWEI_HEALTH_PACKAGE) =
        ExerciseSessionRecord(startTime = Instant.parse(start), startZoneOffset = null,
            endTime = Instant.parse(end), endZoneOffset = null,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
            metadata = Metadata(dataOrigin = DataOrigin(origin)))

    @Test fun exerciseClipsToLocalDayAndDeduplicatesOverlappingSessions() {
        val crossing = exercise("2026-10-13T20:45:00Z", "2026-10-13T21:15:00Z")
        val overlap = exercise("2026-10-13T21:10:00Z", "2026-10-13T21:30:00Z")
        val summary = HealthSyncManager.calculateHuaweiExercise(listOf(crossing, crossing, overlap), date, zone, now)
        assertEquals(30L, summary!!.first)
        assertEquals(2, summary.second)
    }

    @Test fun exerciseRejectsOtherSourcesAndUnfinishedSessions() {
        val summary = HealthSyncManager.calculateHuaweiExercise(listOf(
            exercise("2026-10-14T07:00:00Z", "2026-10-14T08:00:00Z", "nl.appyhapps.healthsync"),
            exercise("2026-10-14T11:00:00Z", "2026-10-14T13:00:00Z"),
            exercise("2026-10-12T07:00:00Z", "2026-10-12T08:00:00Z")
        ), date, zone, now)
        assertNull(summary)
    }

    @Test fun partialReadPreservesEarlierMetricsAndMarksThemStale() {
        val key = date.toString()
        HealthSyncManager.saveLastSync(context, key, HealthSyncManager.HealthSyncResult(
            stepsCount = 4000, hasStepsData = true, isSuccess = true, dateKey = key,
            activeCaloriesKcal = 201.5, hasCaloriesData = true))
        HealthSyncManager.saveLastSync(context, key, HealthSyncManager.HealthSyncResult(
            sleepMinutesTotal = 420, hasSleepData = true, isSuccess = true, dateKey = key,
            exerciseMinutesTotal = 32, exerciseCount = 1, hasExerciseData = true))
        val saved = HealthSyncManager.getLastSync(context, key)!!
        assertEquals(4000L, saved.stepsCount)
        assertEquals(201.5, saved.activeCaloriesKcal, 0.001)
        assertEquals(32L, saved.exerciseMinutesTotal)
        assertEquals(1, saved.exerciseCount)
        assertEquals(setOf("steps", "calories"), saved.staleMetrics)
        assertTrue(saved.hasExerciseData)
        assertTrue(saved.hasCaloriesData)
    }

    @Test fun caloriesOnlyResultCanBeCachedWithoutInventingStepsOrExercise() {
        val key = date.toString()
        HealthSyncManager.saveLastSync(context, key, HealthSyncManager.HealthSyncResult(
            activeCaloriesKcal = 0.0, hasCaloriesData = true, isSuccess = true, dateKey = key))
        val saved = HealthSyncManager.getLastSync(context, key)!!
        assertTrue(saved.hasCaloriesData)
        assertFalse(saved.hasStepsData)
        assertFalse(saved.hasExerciseData)
        assertEquals(0.0, saved.activeCaloriesKcal, 0.0)
    }
}
