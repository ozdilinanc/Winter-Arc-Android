package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.util.HapticEngine
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowVibrator
import org.robolectric.Shadows.shadowOf
import android.os.Vibrator

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HapticEngineTest {

    @Test
    fun testHapticEngineInitialization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = HapticEngine(context)
        assertNotNull("HapticEngine should initialize cleanly", engine)
    }

    @Test
    fun testVibrateSkillCompletedDoesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = HapticEngine(context)
        
        // Execute skill completed vibration
        engine.vibrateSkillCompleted()
        
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        assertNotNull(vibrator)
    }

    @Test
    fun testVibrateLevelUpDoesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = HapticEngine(context)
        
        // Execute level up celebratory vibration
        engine.vibrateLevelUp()
        
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        assertNotNull(vibrator)
    }
}
