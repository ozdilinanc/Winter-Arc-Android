package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeId

object ThemePreferencesRepository {

    const val PREFS_THEME = "winter_arc_theme_prefs"
    private const val KEY_SELECTED_THEME = "selected_theme_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_THEME, Context.MODE_PRIVATE)
    }

    fun getSelectedThemeId(context: Context): AppThemeId {
        val key = getPrefs(context).getString(KEY_SELECTED_THEME, AppThemeId.FOREST_PINE.key)
        return AppThemeId.entries.find { it.key == key } ?: AppThemeId.FOREST_PINE
    }

    fun setSelectedThemeId(context: Context, themeId: AppThemeId) {
        getPrefs(context).edit()
            .putString(KEY_SELECTED_THEME, themeId.key)
            .apply()
    }
}
