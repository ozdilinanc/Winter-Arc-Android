package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TodayHubView(onOpenThemePicker: () -> Unit, modifier: Modifier = Modifier) {
    HealthRoutineView(onBack = {}, modifier = modifier, showBack = false, showHobbies = true,
        onOpenThemePicker = onOpenThemePicker)
}
