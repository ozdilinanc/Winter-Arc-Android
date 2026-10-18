package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun TodayHubView(onOpenThemePicker: () -> Unit, modifier: Modifier = Modifier) {
    var showHealthDetails by rememberSaveable { mutableStateOf(false) }
    if (showHealthDetails) {
        BackHandler { showHealthDetails = false }
        Column(modifier.fillMaxSize()) {
            TextButton(onClick = { showHealthDetails = false }) { Text("Günlüğe dön") }
            DailyTrackerView(onOpenThemePicker = onOpenThemePicker, modifier = Modifier.weight(1f), healthDetailsOnly = true)
        }
    } else {
        HealthRoutineView(
            onBack = {}, modifier = modifier, showBack = false, showHobbies = true,
            onOpenHealthDetails = { showHealthDetails = true }, onOpenThemePicker = onOpenThemePicker
        )
    }
}
