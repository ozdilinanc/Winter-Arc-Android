package com.example.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Provides subtle, tactile haptic feedback using the device's vibration engine.
 * Specifically triggers:
 * 1. Marking a skill node as 'Completed' (clean, subtle tactile click / double-pulse).
 * 2. Level-up achievements (celebratory multi-pulse waveform).
 */
class HapticEngine(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Throwable) {
            null
        }
    }

    /**
     * Emits subtle, tactile haptic feedback when a skill node is marked as completed.
     */
    fun vibrateSkillCompleted() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // API 29+: Predefined EFFECT_CLICK delivers standard subtle tactile feedback
                vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26+: Short 35ms one-shot pulse with standard amplitude
                vib.vibrate(VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(35L)
            }
        } catch (e: Throwable) {
            // Gracefully ignore on devices or test runners without vibration hardware
        }
    }

    /**
     * Emits celebratory rhythmic haptic feedback upon achieving a level-up or major milestone.
     */
    fun vibrateLevelUp() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Celebratory multi-pulse ascending pattern:
                // [delay 0ms, buzz 45ms, pause 60ms, buzz 75ms]
                val timings = longArrayOf(0L, 45L, 60L, 75L)
                if (vib.hasAmplitudeControl()) {
                    val amplitudes = intArrayOf(0, 180, 0, 255)
                    vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    vib.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0L, 45L, 60L, 75L), -1)
            }
        } catch (e: Throwable) {
            // Gracefully ignore on devices or test runners without vibration hardware
        }
    }

    /**
     * Emits subtle click vibration on selection or toggle.
     */
    fun vibrateSelection() {
        vibrateSkillCompleted()
    }

    /**
     * Emits haptic vibration on step completion.
     */
    fun vibrateStepCompleted() {
        vibrateSkillCompleted()
    }
}

@Composable
fun rememberHapticEngine(): HapticEngine {
    val context = LocalContext.current.applicationContext
    return remember(context) { HapticEngine(context) }
}
