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

    @Test fun manualExerciseCanBeCheckedAndUncheckedWithoutChangingOtherDays() {
        val today = LocalDate.now().toString()
        val previous = LocalDate.now().minusWeeks(1).toString()
        prefs.edit().putBoolean("health_movement_$today", true)
            .putBoolean("health_sleep_$today", true)
            .putBoolean("health_exercise_$previous", true).commit()
        compose.setContent { MyApplicationTheme { TodayHubView(onOpenThemePicker = {}) } }
        compose.onNodeWithContentDescription("Huawei bağlantısı").assertDoesNotExist()
        compose.onNodeWithText("Huawei Sağlık").assertDoesNotExist()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Düzenli uyku"))
        compose.onNodeWithText("Yürüyüş / koşu").assertIsOn()
        compose.onNodeWithText("Düzenli uyku").assertIsOn()
        compose.onNodeWithText("Egzersiz").assertIsOff().performClick().assertIsOn()
        compose.runOnIdle { assertTrue(Routine.isCompleted(prefs, today, "exercise")) }
        compose.onNodeWithText("Egzersiz").performClick().assertIsOff()
        compose.runOnIdle {
            assertFalse(Routine.isCompleted(prefs, today, "exercise"))
            assertTrue(Routine.isCompleted(prefs, previous, "exercise"))
            assertTrue(Routine.isCompleted(prefs, today, "movement"))
            assertTrue(Routine.isCompleted(prefs, today, "sleep"))
        }
    }

    @Test fun savedHobbiesRenderInLightTheme() {
        prefs.edit().putBoolean("hab_reading_${LocalDate.now()}", true).commit()
        compose.setContent { MyApplicationTheme(palette = AppThemePalette.LightPaper) { TodayHubView(onOpenThemePicker = {}) } }
        compose.onNodeWithText("Kitap okuma").assertIsOn()
        compose.onNodeWithText("İngilizce").assertIsOff()
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
        compose.onNodeWithText("Kendime zaman").assertIsDisplayed()
        compose.onRoot().captureRoboImage("build/previews/winterarc-daily-large-text.png")
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("250 ml ekle"))
        compose.onNodeWithText("250 ml ekle").assertIsDisplayed().performClick()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Düzenli uyku"))
        compose.onNodeWithText("Düzenli uyku").assertIsDisplayed().performClick().assertIsOn()
    }
}
