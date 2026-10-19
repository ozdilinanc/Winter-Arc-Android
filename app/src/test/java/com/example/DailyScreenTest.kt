package com.example

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.HealthRoutineRepository as Routine
import com.example.ui.components.TodayHubView
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.HealthSyncManager
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w393dp-h852dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DailyScreenTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs get() = context.getSharedPreferences(Routine.PREFS_NAME, Context.MODE_PRIVATE)

    @Before fun reset() {
        prefs.edit().clear().commit()
        context.getSharedPreferences("winter_arc_huawei_sync_v2", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test fun waterAndHobbiesStayOnTheSelectedDate() {
        val today = LocalDate.now().toString()
        val previous = LocalDate.now().minusWeeks(1).toString()
        prefs.edit().putInt("water_ml_$today", 1000).putBoolean("hab_reading_$today", true).commit()
        compose.setContent { MyApplicationTheme { TodayHubView(onOpenThemePicker = {}) } }
        compose.onNodeWithContentDescription("Önceki hafta").performClick()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Kitap okuma"))
        compose.onNodeWithText("Kitap okuma").assertIsOff().performClick().assertIsOn()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("250 ml ekle"))
        compose.onNodeWithText("250 ml ekle").performClick()
        compose.runOnIdle {
            assertEquals(250, Routine.waterMl(prefs, previous))
            assertEquals(1000, Routine.waterMl(prefs, today))
            assertTrue(Routine.isHobbyCompleted(prefs, previous, "hab_reading"))
            assertTrue(Routine.isHobbyCompleted(prefs, today, "hab_reading"))
        }
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Bugün"))
        compose.onNodeWithText("Bugün").performClick()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Kitap okuma"))
        compose.onNodeWithText("Kitap okuma").assertIsOn()
    }

    @Test fun missingWatchDataIsNotDisplayedAsZeroAndConnectionCanBeOpened() {
        compose.setContent { MyApplicationTheme { TodayHubView(onOpenThemePicker = {}) } }
        compose.onAllNodesWithText("Kayıt yok").assertCountEquals(4)
        compose.onNodeWithContentDescription("Huawei bağlantısı").performClick()
        compose.onNodeWithText("Saat bağlantısı").assertIsDisplayed()
        compose.onNodeWithText("İzin ver ve kontrol et").assertExists()
    }

    @Test fun verifiedCacheIsLabelledAndRendersInLightTheme() {
        val day = LocalDate.now().toString()
        HealthSyncManager.saveLastSync(context, day, HealthSyncManager.HealthSyncResult(
            stepsCount = 8234, sleepMinutesTotal = 452, activeCaloriesKcal = 386.0,
            exerciseMinutesTotal = 42, exerciseCount = 1, hasStepsData = true, hasSleepData = true,
            hasCaloriesData = true, hasExerciseData = true, isSuccess = true, dateKey = day))
        compose.setContent { MyApplicationTheme(palette = AppThemePalette.LightPaper) { TodayHubView(onOpenThemePicker = {}) } }
        compose.onNodeWithText("Son kayıt gösteriliyor").assertIsDisplayed()
        compose.onNodeWithText("8.234").assertIsDisplayed()
        compose.onNodeWithText("7 sa 32 dk").assertIsDisplayed()
        compose.onRoot().captureRoboImage("build/previews/winterarc-daily-light.png")
    }

    @Test
    @Config(qualifiers = "w320dp-h640dp-xhdpi")
    fun compactScreenWithLargeTextKeepsDailyControlsReachable() {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.4f)) {
                MyApplicationTheme { TodayHubView(onOpenThemePicker = {}) }
            }
        }
        compose.onNodeWithContentDescription("Huawei bağlantısı").assertIsDisplayed()
        compose.onRoot().captureRoboImage("build/previews/winterarc-daily-large-text.png")
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("250 ml ekle"))
        compose.onNodeWithText("250 ml ekle").assertIsDisplayed().performClick()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Düzenli uyku"))
        compose.onNodeWithText("Düzenli uyku").assertIsDisplayed().performClick().assertIsOn()
    }
}
