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

        // Change to GitHub Dimmed
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.GITHUB_DIMMED)
        assertEquals(AppThemeId.GITHUB_DIMMED, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to Dracula Velvet
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.DRACULA_VELVET)
        assertEquals(AppThemeId.DRACULA_VELVET, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to OLED Pitch
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.OLED_PITCH)
        assertEquals(AppThemeId.OLED_PITCH, ThemePreferencesRepository.getSelectedThemeId(context))

        // Change to Obsidian Indigo
        ThemePreferencesRepository.setSelectedThemeId(context, AppThemeId.OBSIDIAN_INDIGO)
        assertEquals(AppThemeId.OBSIDIAN_INDIGO, ThemePreferencesRepository.getSelectedThemeId(context))
    }

    @Test
    fun verifyAllSevenPalettesExistAndAreComplete() {
        val palettes = AppThemePalette.allPalettes
        assertEquals("Tam olarak 7 koyu tema bulunmalı", 7, palettes.size)

        val ids = palettes.map { it.id }
        assertTrue(ids.contains(AppThemeId.FOREST_PINE))
        assertTrue(ids.contains(AppThemeId.NORDIC_FROST))
        assertTrue(ids.contains(AppThemeId.WARM_ESPRESSO))
        assertTrue(ids.contains(AppThemeId.GITHUB_DIMMED))
        assertTrue(ids.contains(AppThemeId.DRACULA_VELVET))
        assertTrue(ids.contains(AppThemeId.OLED_PITCH))
        assertTrue(ids.contains(AppThemeId.OBSIDIAN_INDIGO))

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
        assertEquals(AppThemePalette.GitHubDimmed, AppThemePalette.fromKey("github_dimmed"))
        assertEquals(AppThemePalette.DraculaVelvet, AppThemePalette.fromKey("dracula_velvet"))
        assertEquals(AppThemePalette.OledPitch, AppThemePalette.fromKey("oled_pitch"))
        assertEquals(AppThemePalette.ObsidianIndigo, AppThemePalette.fromKey("obsidian_indigo"))
        // Geçersiz key varsayılan olarak ForestPine dönmeli
        assertEquals(AppThemePalette.ForestPine, AppThemePalette.fromKey("invalid_unknown_key"))
    }
}
