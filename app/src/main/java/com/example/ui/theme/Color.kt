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

// Status Colors (Matching elegant badges & completion tags)
val StatusNotStarted = Color(0xFF64748B)   // Slate-500
val StatusLearning = Color(0xFFF59E0B)     // Amber
val StatusPracticed = Color(0xFF10B981)    // Emerald-500
val StatusCompleted = Color(0xFF10B981)    // Emerald
val StatusStrong = Color(0xFF34D399)       // Mint green

// Branch Accent Palettes (Static constants for Enum compatibility)
val BranchDotNet = Color(0xFF818CF8)       // Sharp Indigo (.NET Core Primary)
val BranchDevOps = Color(0xFFA78BFA)       // Soft Violet (DevOps / K8s)
val BranchAndroid = Color(0xFF10B981)      // Emerald (Android / Mobile)
val BranchCS = Color(0xFFC084FC)          // Lavender (CS Foundations)
val BranchGraduation = Color(0xFFF43F5E)   // Rose / Crimson (Graduation / KV Cache)
val BranchTools = Color(0xFF38BDF8)        // Sky Blue (Git / Linux / LazyVim)
val BranchSecurity = Color(0xFFF59E0B)     // Amber (Security)
val BranchEnglish = Color(0xFF38BDF8)      // Sky Blue (B2+ English)
val BranchPortfolio = Color(0xFF10B981)    // Emerald (Portfolio Pipeline)
val BranchKnowledge = Color(0xFF818CF8)    // Indigo (Knowledge Loop)
val BranchCerts = Color(0xFF94A3B8)        // Slate (Certifications)
val BranchLanguage = Color(0xFFE2E8F0)     // Cool Slate (Second Language)

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
