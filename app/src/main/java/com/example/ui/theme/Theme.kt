package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0x336366F1),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = AccentIndigo,
    onSecondary = Color.White,
    secondaryContainer = PanelNavyElevated,
    onSecondaryContainer = Color(0xFFC7D2FE),
    tertiary = AccentPurple,
    onTertiary = Color.White,
    background = CanvasDark,
    onBackground = TextPrimary,
    surface = PanelNavy,
    onSurface = TextPrimary,
    surfaceVariant = PanelNavyElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderActive
)

@Composable
fun SkillTreeTheme(
    darkTheme: Boolean = true, // Default to dark mode for technical developer feel
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    SkillTreeTheme(content = content)
}
