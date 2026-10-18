package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.repository.HealthRoutineRepository as Routine
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HealthRoutineView(
    onBack: () -> Unit, modifier: Modifier = Modifier,
    showBack: Boolean = true, showHobbies: Boolean = false,
    onOpenHealthDetails: (() -> Unit)? = null,
    onOpenThemePicker: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(Routine.PREFS_NAME, Context.MODE_PRIVATE) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var today by remember { mutableStateOf(LocalDate.now()) }
    var showHobbyPicker by remember { mutableStateOf(false) }
    fun refreshDay() {
        val fresh = LocalDate.now()
        if (date == today) date = fresh
        today = fresh
    }
    var revision by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(prefs, lifecycleOwner) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> revision++ }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshDay()
                revision++
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            refreshDay()
            delay(30_000)
        }
    }
    BackHandler(enabled = showBack, onBack = onBack)
    val dateKey = date.toString()
    val water = remember(dateKey, revision) { Routine.waterMl(prefs, dateKey) }
    val target = remember(revision) { prefs.getInt("water_target_ml", 3000).coerceAtLeast(1) }
    val enabledHobbies = remember(revision) { Routine.enabledHobbies(prefs) }
    val labels = listOf("Sağlıklı beslenme", "Yürüyüş / koşu", "Düzenli uyku")

    LazyColumn(
        modifier = modifier.fillMaxSize().background(CanvasDark).padding(horizontal = 16.dp).testTag("daily_routines"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBack) IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = TextPrimary)
                }
                Text(if (showHobbies) "Günlük" else "Sağlık & Rutin", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.weight(1f))
                onOpenThemePicker?.let { ThemeToggleButton(onOpenThemePicker = it) }
            }
            Text(
                "${if (date == today) "Bugün · " else ""}${date.format(DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("tr")))}",
                color = TextSecondary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { date = date.minusDays(1) }) { Text("Önceki gün") }
                TextButton(onClick = { date = today }, enabled = date != today) { Text("Bugün") }
                TextButton(onClick = { date = date.plusDays(1) }, enabled = date < today) { Text("Sonraki gün") }
            }
            Spacer(Modifier.height(8.dp))
            Text("Tamamladıklarını işaretle. Her gün ayrı kaydedilir.", color = TextSecondary)
        }
        Routine.habits.forEachIndexed { index, habit ->
            item(key = habit) {
                val checked = remember(dateKey, revision) { Routine.isCompleted(prefs, dateKey, habit) }
                Surface(color = PanelNavy, shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .toggleable(checked, role = Role.Checkbox) {
                                Routine.setCompleted(prefs, dateKey, habit, it)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(labels[index], color = TextPrimary, modifier = Modifier.weight(1f))
                        Checkbox(checked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = AccentEmerald))
                    }
                }
            }
        }
        item {
            Surface(color = PanelNavy, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Günlük su", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("$water / $target ml", color = TextSecondary)
                    LinearProgressIndicator(
                        progress = { (water.toFloat() / target).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = AccentCyan
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { Routine.addWater(prefs, dateKey, -250) },
                            enabled = water > 0
                        ) { Text("−250 ml") }
                        Button(
                            onClick = { Routine.addWater(prefs, dateKey, 250) },
                            enabled = water < 5000
                        ) { Text("+250 ml") }
                    }
                    Text("Su hedefini sağlık ayrıntılarından değiştirebilirsin.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (showHobbies) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Hobiler", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showHobbyPicker = true }) { Text("Düzenle") }
                }
            }
            val enabled = enabledHobbies
            if (enabled.isEmpty()) item { Text("Takip etmek istediğin hobileri seçebilirsin.", color = TextSecondary) }
            Routine.hobbies.filterKeys { it in enabled }.forEach { (id, label) ->
                item(key = id) {
                    val checked = remember(dateKey, revision) { Routine.isHobbyCompleted(prefs, dateKey, id) }
                    Surface(color = PanelNavy, shape = RoundedCornerShape(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth().toggleable(checked, role = Role.Checkbox) {
                                Routine.setHobbyCompleted(prefs, dateKey, id, it)
                            }.padding(16.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = TextPrimary, modifier = Modifier.weight(1f))
                            Checkbox(checked, onCheckedChange = null)
                            if (id == "hab_english") TextButton(onClick = {
                                val launch = context.packageManager.getLaunchIntentForPackage("com.ozdilinanc.clawssary")
                                    ?: android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.ozdilinanc.clawssary"))
                                runCatching { context.startActivity(launch) }
                            }) { Text("Aç") }
                        }
                    }
                }
            }
        }
        onOpenHealthDetails?.let { open -> item { TextButton(onClick = open) { Text("Saat verileri ve sağlık ayrıntıları") } } }
    }
    if (showHobbyPicker) {
        AlertDialog(onDismissRequest = { showHobbyPicker = false }, title = { Text("Günlük hobilerin") }, text = {
            Column {
                Routine.hobbies.forEach { (id, label) ->
                    val enabled = enabledHobbies.contains(id)
                    Row(Modifier.fillMaxWidth().toggleable(enabled, role = Role.Checkbox) {
                        Routine.setHobbyEnabled(prefs, id, it)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(enabled, onCheckedChange = null)
                        Text(label)
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { showHobbyPicker = false }) { Text("Tamam") } })
    }

}
