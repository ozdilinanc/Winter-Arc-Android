package com.example

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.ThemePreferencesRepository
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.AppThemePalette
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThemePreferencesRepositoryTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences(ThemePreferencesRepository.PREFS_THEME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun verifyDefaultThemeIsForestPine() {
        val defaultTheme = ThemePreferencesRepository.getSelectedThemeId(context)
        assertEquals(AppThemeId.FOREST_PINE, defaultTheme)
    }

    @Test
    fun verifyThemeSelectionPersists() {
        // Change to Nordic Frost
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.NORDIC_FROST)
        assertEquals(AppThemeId.NORDIC_FROST, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to Warm Espresso
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.WARM_ESPRESSO)
        assertEquals(AppThemeId.WARM_ESPRESSO, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to Light Paper
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.LIGHT_PAPER)
        assertEquals(AppThemeId.LIGHT_PAPER, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to Cyber Neon
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.CYBER_NEON)
        assertEquals(AppThemeId.CYBER_NEON, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to Matrix Terminal
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.MATRIX_TERMINAL)
        assertEquals(AppThemeId.MATRIX_TERMINAL, ThemePreferencesRepository.getSelectedThemeId(context))
    }

    @Test
    fun verifyAllPalettesExistAndAreComplete() {
        val palettes = AppThemePalette.allPalettes
        assertEquals("13 farklı tema bulunmalı", 13, palettes.size)

        val ids = palettes.map { it.id }
        assertTrue(ids.contains(AppThemeId.FOREST_PINE))
        assertTrue(ids.contains(AppThemeId.NORDIC_FROST))
        assertTrue(ids.contains(AppThemeId.WARM_ESPRESSO))
        assertTrue(ids.contains(AppThemeId.GITHUB_DIMMED))
        assertTrue(ids.contains(AppThemeId.DRACULA_VELVET))
        assertTrue(ids.contains(AppThemeId.OLED_PITCH))
        assertTrue(ids.contains(AppThemeId.OBSIDIAN_INDIGO))
        assertTrue(ids.contains(AppThemeId.LIGHT_PAPER))
        assertTrue(ids.contains(AppThemeId.CREAM_PARCHMENT))
        assertTrue(ids.contains(AppThemeId.CYBER_NEON))
        assertTrue(ids.contains(AppThemeId.MATRIX_TERMINAL))
        assertTrue(ids.contains(AppThemeId.SOLARIZED_DARK))
        assertTrue(ids.contains(AppThemeId.SAKURA_NIGHT))

        palettes.forEach { palette ->
            assertTrue("${palette.name} başlığı boş olmamalı", palette.name.isNotBlank())
            assertTrue("${palette.name} emojisi boş olmamalı", palette.emoji.isNotBlank())
            assertTrue("${palette.name} açıklaması boş olmamalı", palette.description.isNotBlank())
            assertEquals("${palette.name} 4 renk önizleme swatch'ına sahip olmalı", 4, palette.previewSwatches.size)

            val colorScheme = palette.toDarkColorScheme()
            assertNotNull(colorScheme)
            assertEquals(palette.canvasDark, colorScheme.background)
            assertEquals(palette.accentCyan, colorScheme.primary)
        }
    }

    @Test
    fun verifyFromKeyResolvesCorrectly() {
        assertEquals(AppThemePalette.ForestPine, AppThemePalette.fromKey("forest_pine"))
        assertEquals(AppThemePalette.NordicFrost, AppThemePalette.fromKey("nordic_frost"))
        assertEquals(AppThemePalette.WarmEspresso, AppThemePalette.fromKey("warm_espresso"))
        assertEquals(AppThemePalette.LightPaper, AppThemePalette.fromKey("light_paper"))
        assertEquals(AppThemePalette.CreamParchment, AppThemePalette.fromKey("cream_parchment"))
        assertEquals(AppThemePalette.CyberNeon, AppThemePalette.fromKey("cyber_neon"))
        assertEquals(AppThemePalette.MatrixTerminal, AppThemePalette.fromKey("matrix_terminal"))
        assertEquals(AppThemePalette.SolarizedDark, AppThemePalette.fromKey("solarized_dark"))
        assertEquals(AppThemePalette.SakuraNight, AppThemePalette.fromKey("sakura_night"))
        // Geçersiz key varsayılan olarak ForestPine dönmeli
        assertEquals(AppThemePalette.ForestPine, AppThemePalette.fromKey("invalid_unknown_key"))
    }
}
