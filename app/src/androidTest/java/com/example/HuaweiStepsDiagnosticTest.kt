package com.example

import android.os.Bundle
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ui.util.HealthSyncManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.ZoneId

/** Read-only device diagnostics; no records are inserted, changed or deleted. */
@RunWith(AndroidJUnit4::class)
class HuaweiStepsDiagnosticTest {
    @Test
    fun inspectDailyHuaweiMetrics() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val result = HealthSyncManager.fetchTodayHealthData(context)
        val permissions = if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE)
            HealthConnectClient.getOrCreate(context).permissionController.getGrantedPermissions() else emptySet()
        instrumentation.sendStatus(0, Bundle().apply {
            putString("stream", "Huawei daily read: date=${result.dateKey}; steps=${result.hasStepsData}; " +
                "sleep=${result.hasSleepData}; activeCalories=${result.hasCaloriesData}; exercise=${result.hasExerciseData}; " +
                "granted=${permissions.size}/${HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS.size}. ${result.message}\n")
        })
        assertEquals(HealthSyncManager.HUAWEI_SOURCE, result.source)
    }

    @Test
    fun inspectTodayHuaweiSteps() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        assertEquals(HealthConnectClient.SDK_AVAILABLE, HealthConnectClient.getSdkStatus(context))
        val client = HealthConnectClient.getOrCreate(context)
        assertTrue("Winter Arc adım okuma izni gerekli.",
            HealthPermission.getReadPermission(StepsRecord::class) in
                client.permissionController.getGrantedPermissions())
        val now = Instant.now()
        val zone = ZoneId.systemDefault()
        val date = now.atZone(zone).toLocalDate()
        val start = date.atStartOfDay(zone).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()
        val origin = setOf(DataOrigin(HealthSyncManager.HUAWEI_HEALTH_PACKAGE))
        val aggregate = client.aggregate(AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(start, now),
            dataOriginFilter = origin
        ))[StepsRecord.COUNT_TOTAL]
        val records = mutableListOf<StepsRecord>()
        var token: String? = null
        do {
            val page = client.readRecords(ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, endOfDay),
                dataOriginFilter = origin, pageToken = token
            ))
            records += page.records
            token = page.pageToken
        } while (!token.isNullOrEmpty())
        val summary = buildString {
            appendLine("Huawei adım tanısı: date=$date zone=$zone now=$now")
            appendLine("Gün içi aggregate=${aggregate ?: "KAYIT_YOK"}; " +
                "tam gün Huawei ham kayıt sayısı=${records.size}")
            for (record in records.take(10)) {
                appendLine("count=${record.count} start=${record.startTime} end=${record.endTime} " +
                    "origin=${record.metadata.dataOrigin.packageName}")
            }
        }
        instrumentation.sendStatus(0, Bundle().apply { putString("stream", summary) })
        assertTrue("Kaynak filtresi dışında kayıt geldi.", records.all {
            it.metadata.dataOrigin.packageName == HealthSyncManager.HUAWEI_HEALTH_PACKAGE
        })
    }
}
