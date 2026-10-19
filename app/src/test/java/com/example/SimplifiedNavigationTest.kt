package com.example

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.HealthRoutineRepository
import com.example.data.repository.PersonalTrackerRepository
import com.example.data.repository.TrackerKind
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w393dp-h852dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SimplifiedNavigationTest {
    @get:Rule val compose = createComposeRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before fun reset() {
        context.getSharedPreferences(PersonalTrackerRepository.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences(HealthRoutineRepository.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test fun mainAreasAndSoftwareArchiveAreReachable() {
        compose.setContent { MyApplicationTheme { CategoriesDashboardView() } }
        listOf("Okul", "Yazılım", "Ürettiklerim", "Hobiler").forEach { compose.onNodeWithText(it).assertIsDisplayed() }
        compose.onNodeWithText("TEKNİK GENEL KÜLTÜR").assertDoesNotExist()
        compose.onRoot().captureRoboImage("build/previews/winterarc-areas.png")
        compose.onNodeWithText("Yazılım").performClick()
        compose.onNodeWithText("Backend / .NET").assertIsDisplayed()
        compose.onNodeWithText("Android").assertIsDisplayed()
        compose.onNodeWithText("BTK kursları").assertIsDisplayed()
        compose.onNodeWithText("Kişisel Projeler").assertDoesNotExist()
        compose.onNodeWithText("Konu arşivi").performClick()
        compose.onNodeWithText("Konu ara").assertIsDisplayed()
        compose.onNodeWithText("Konu ara").performTextInput("DevOps")
        compose.onNodeWithText("DevOps & dağıtık sistemler").assertIsDisplayed()
    }

    @Test fun portfolioKeepsMediumAndOpensPublishedClawssary() {
        compose.setContent { MyApplicationTheme { CategoriesDashboardView() } }
        compose.onNodeWithText("Ürettiklerim").performClick()
        compose.onNodeWithText("GitHub").assertIsDisplayed()
        compose.onNodeWithText("Medium").assertIsDisplayed()
        compose.onNodeWithText("Uygulamalarım").performClick()
        compose.onNodeWithText("Clawssary").assertIsDisplayed()
        compose.onNodeWithText("Yayında").assertIsDisplayed()
        compose.onRoot().captureRoboImage("build/previews/winterarc-apps.png")
    }

    @Test fun newTrickCanBeSavedWithRequirementsAndReminder() {
        compose.setContent { MyApplicationTheme { PersonalTrackerView(TrackerKind.CARD_TRICKS, onBack = {}) } }
        compose.onNodeWithText("Numara ekle").performClick()
        compose.onNodeWithText("İsim").performTextInput("Double lift")
        compose.onNodeWithText("Gerekenler").performTextInput("Standart deste")
        compose.onNodeWithText("Nasıl yapılıyor?").performTextInput("İki kartı birlikte çevir.")
        compose.onNodeWithText("Kaydet").performClick()
        compose.onNodeWithText("Double lift").assertIsDisplayed()
        val prefs = context.getSharedPreferences(PersonalTrackerRepository.PREFS_NAME, Context.MODE_PRIVATE)
        assertEquals("Standart deste", PersonalTrackerRepository.getEntries(prefs, TrackerKind.CARD_TRICKS).single().requirements)
        compose.onRoot().captureRoboImage("build/previews/winterarc-tricks.png")
    }

    @Test fun dailyShowsSimpleHealthAndOptionalHobbies() {
        compose.setContent { MyApplicationTheme { TodayHubView(onOpenThemePicker = {}) } }
        compose.onNodeWithText("Huawei Sağlık").assertIsDisplayed()
        compose.onNodeWithText("Aktif kalori").assertIsDisplayed()
        compose.onNodeWithText("Egzersiz").assertIsDisplayed()
        compose.onRoot().captureRoboImage("build/previews/winterarc-daily.png")
        compose.onNodeWithText("Düzenle").performScrollTo().performClick()
        compose.onNodeWithText("Günlük hobilerin").assertIsDisplayed()
        compose.onNodeWithText("Kart numarası çalışması").performClick()
        compose.onNodeWithText("Tamam").performClick()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Kart numarası çalışması"))
        compose.onNodeWithText("Kart numarası çalışması").assertIsDisplayed()
        compose.onNodeWithTag("daily_routines").performScrollToNode(hasText("Düzenli uyku"))
        compose.onNodeWithText("Sağlıklı beslenme").assertIsDisplayed()
        compose.onNodeWithText("Yürüyüş / koşu").assertIsDisplayed()
        compose.onNodeWithText("Düzenli uyku").assertIsDisplayed()
        compose.onRoot().captureRoboImage("build/previews/winterarc-daily-lower.png")
    }
}
