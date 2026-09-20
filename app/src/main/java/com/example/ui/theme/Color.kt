package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppPalette = staticCompositionLocalOf { AppThemePalette.ForestPine }

// Surfaces
val CanvasDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.canvasDark

val PanelNavy: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.panelNavy

val PanelNavyElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.panelNavyElevated

val PanelNavyHighlight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.panelNavyHighlight

val BorderSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.borderSubtle

val BorderActive: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.borderActive

// Accents
val AccentCyan: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentCyan

val AccentCyanGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentCyanGlow

val AccentIndigo: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentIndigo

val AccentPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentPurple

val AccentViolet: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentViolet

val AccentAmber: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentAmber

val AccentGold: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentGold

val AccentEmerald: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentEmerald

// Status Colors
val StatusNotStarted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.textMuted

val StatusLearning: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentAmber

val StatusPracticed: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentEmerald

val StatusCompleted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentEmerald

val StatusStrong: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.accentCyan

// Branch Accent Palettes
val BranchDotNet: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchDotNet

val BranchDevOps: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchDevOps

val BranchAndroid: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchAndroid

val BranchCS: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchCS

val BranchGraduation: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchGraduation

val BranchTools: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchTools

val BranchSecurity: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchSecurity

val BranchEnglish: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchEnglish

val BranchPortfolio: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchPortfolio

val BranchKnowledge: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchKnowledge

val BranchCerts: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchCerts

val BranchLanguage: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.branchLanguage

// Typography & Content Colors
val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.textMuted

val TextDarkMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.textDarkMuted

val TextAccent: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppPalette.current.textAccent
