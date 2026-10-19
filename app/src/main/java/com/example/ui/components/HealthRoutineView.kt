package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.repository.HealthRoutineRepository as Routine
import com.example.ui.theme.*
import com.example.ui.util.WinterArcNotificationHelper
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val dailyLocale: Locale = Locale.forLanguageTag("tr-TR")

@Composable
fun HealthRoutineView(
    onBack: () -> Unit, modifier: Modifier = Modifier,
    showBack: Boolean = true, showHobbies: Boolean = false,
    onOpenThemePicker: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(Routine.PREFS_NAME, Context.MODE_PRIVATE) }
    var dateKey by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    val date = LocalDate.parse(dateKey)
    var today by remember { mutableStateOf(LocalDate.now()) }
    var showHobbyPicker by rememberSaveable { mutableStateOf(false) }
    var showWaterSettings by rememberSaveable { mutableStateOf(false) }
    var showConnection by rememberSaveable { mutableStateOf(false) }
    val (health, connect) = rememberDailyHealthState(date)
    fun refreshDay() {
        val fresh = LocalDate.now()
        if (dateKey == today.toString()) dateKey = fresh.toString()
        today = fresh
    }
    var revision by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(prefs, lifecycleOwner) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> revision++ }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) { refreshDay(); revision++ }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(Unit) {
        while (true) { refreshDay(); delay(30_000) }
    }
    BackHandler(enabled = showBack, onBack = onBack)
    val water = remember(dateKey, revision) { Routine.waterMl(prefs, dateKey) }
    val target = remember(revision) { prefs.getInt("water_target_ml", 3000).coerceIn(250, 5000) }
    val enabledHobbies = remember(revision) { Routine.enabledHobbies(prefs) }
    val checkedHobbies = remember(dateKey, revision) {
        enabledHobbies.filter { Routine.isHobbyCompleted(prefs, dateKey, it) }.toSet()
    }
    val checkedHabits = remember(dateKey, revision) {
        Routine.habits.filter { Routine.isCompleted(prefs, dateKey, it) }.toSet()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(CanvasDark).testTag("daily_routines"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(key = "date") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBack) IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Geri", tint = TextPrimary)
                }
                Column(Modifier.weight(1f)) {
                    Text("Günlük", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    Text(date.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", dailyLocale)),
                        color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
                onOpenThemePicker?.let { ThemeToggleButton(onOpenThemePicker = it) }
            }
            Spacer(Modifier.height(16.dp))
            DailyDateStrip(date, today, onSelect = { dateKey = it.toString() })
        }
        item(key = "watch") {
            DailyWatchCard(health.result, health.isRefreshing, health.isCached,
                onOpenConnection = { showConnection = true })
        }
        if (showHobbies) item(key = "hobbies") {
            DailySectionHeading("Kendime zaman", "Düzenle") { showHobbyPicker = true }
            if (enabledHobbies.isEmpty()) {
                TextButton(onClick = { showHobbyPicker = true }) { Text("Takip edeceğin hobileri seç") }
            } else DailySurface {
                Routine.hobbies.filterKeys { it in enabledHobbies }.entries.forEachIndexed { index, (id, label) ->
                    if (index > 0) DailyDivider()
                    DailyRoutineRow(
                        label = if (id == "hab_english") "İngilizce" else label,
                        description = when (id) {
                            "hab_reading" -> "Birkaç sayfa da olsa"
                            "hab_english" -> "Clawssary ile pratik"
                            "hab_cards" -> "Deste başında biraz pratik"
                            else -> "Dil pratiği"
                        },
                        icon = when (id) {
                            "hab_reading" -> Icons.AutoMirrored.Outlined.MenuBook
                            "hab_cards" -> Icons.Outlined.Style
                            else -> Icons.Outlined.Translate
                        },
                        checked = id in checkedHobbies,
                        onChecked = { Routine.setHobbyCompleted(prefs, dateKey, id, it) },
                        trailing = if (id == "hab_english") ({
                            IconButton(onClick = {
                                val launch = context.packageManager.getLaunchIntentForPackage("com.ozdilinanc.clawssary")
                                    ?: android.content.Intent(android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.ozdilinanc.clawssary"))
                                runCatching { context.startActivity(launch) }
                            }) { Icon(Icons.AutoMirrored.Outlined.OpenInNew, "Clawssary’yi aç", tint = TextSecondary, modifier = Modifier.size(18.dp)) }
                        }) else null
                    )
                }
            }
        }
        item(key = "water") {
            DailyWaterCard(water, target, onAdd = { Routine.addWater(prefs, dateKey, 250) },
                onRemove = { Routine.addWater(prefs, dateKey, -250) }, onSettings = { showWaterSettings = true })
        }
        item(key = "routines") {
            DailySectionHeading("Günün rutini")
            DailySurface {
                DailyRoutineRow("Sağlıklı beslenme", "Günün genelini düşün", Icons.Outlined.Restaurant,
                    "nutrition" in checkedHabits, { Routine.setCompleted(prefs, dateKey, "nutrition", it) })
                DailyDivider()
                DailyRoutineRow("Yürüyüş / koşu", "Hareket için zaman ayırdım", Icons.AutoMirrored.Outlined.DirectionsWalk,
                    "movement" in checkedHabits, { Routine.setCompleted(prefs, dateKey, "movement", it) })
                DailyDivider()
                DailyRoutineRow("Düzenli uyku", "Uyku düzenime dikkat ettim", Icons.Outlined.Bedtime,
                    "sleep" in checkedHabits, { Routine.setCompleted(prefs, dateKey, "sleep", it) })
            }
        }
    }
    if (showConnection) DailyHealthConnectionSheet(health, connect, onDismiss = { showConnection = false })
    if (showWaterSettings) WaterSettingsDialog(prefs, target, onDismiss = { showWaterSettings = false })
    if (showHobbyPicker) {
        AlertDialog(onDismissRequest = { showHobbyPicker = false }, title = { Text("Günlük hobilerin") }, text = {
            Column {
                Routine.hobbies.forEach { (id, label) ->
                    val enabled = enabledHobbies.contains(id)
                    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).toggleable(enabled, role = Role.Checkbox) {
                        Routine.setHobbyEnabled(prefs, id, it)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(enabled, onCheckedChange = null)
                        Text(label, Modifier.padding(start = 8.dp))
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { showHobbyPicker = false }) { Text("Tamam") } })
    }
}

@Composable
private fun DailyDateStrip(date: LocalDate, today: LocalDate, onSelect: (LocalDate) -> Unit) {
    // Seven recent days stay stable while selecting a day. Arrows reach older weeks.
    val weeksBack = java.time.temporal.ChronoUnit.DAYS.between(date, today) / 7
    val end = today.minusWeeks(weeksBack)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onSelect(date.minusWeeks(1)) }, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Outlined.ChevronLeft, "Önceki hafta", tint = TextSecondary)
        }
        Text(if (weeksBack == 0L) "Son 7 gün" else "${end.minusDays(6).format(DateTimeFormatter.ofPattern("d MMM", dailyLocale))} – ${end.format(DateTimeFormatter.ofPattern("d MMM", dailyLocale))}",
            Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        if (date != today) TextButton(onClick = { onSelect(today) }) { Text("Bugün") }
        IconButton(onClick = { onSelect(minOf(today, date.plusWeeks(1))) }, enabled = weeksBack > 0,
            modifier = Modifier.size(48.dp)) {
            Icon(Icons.Outlined.ChevronRight, "Sonraki hafta", tint = if (weeksBack > 0) TextSecondary else TextMuted)
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (6 downTo 0).forEach { daysAgo ->
            val day = end.minusDays(daysAgo.toLong())
            val selected = day == date
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                    .background(if (selected) AccentCyan.copy(alpha = .16f) else PanelNavy)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelect(day) })
                    .semantics { contentDescription = day.format(DateTimeFormatter.ofPattern("d MMMM EEEE", dailyLocale)) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(day.format(DateTimeFormatter.ofPattern("EE", dailyLocale)),
                    fontSize = 11.sp, color = if (selected) AccentCyan else TextSecondary)
                Text(day.dayOfMonth.toString(), fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    color = if (selected) AccentCyan else TextPrimary)
                Box(Modifier.size(4.dp).background(if (day == today) AccentCyan else androidx.compose.ui.graphics.Color.Transparent, CircleShape))
            }
        }
    }
}

@Composable
internal fun DailySurface(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = PanelNavy, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(content = content)
    }
}

@Composable
private fun DailySectionHeading(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().heightIn(min = 40.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), color = TextPrimary, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium)
        if (action != null) TextButton(onClick = onAction) { Text(action, color = TextSecondary) }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun DailyDivider() { HorizontalDivider(Modifier.padding(start = 56.dp, end = 16.dp), color = BorderSubtle.copy(alpha = .55f)) }

@Composable
private fun DailyRoutineRow(
    label: String, description: String, icon: ImageVector, checked: Boolean,
    onChecked: (Boolean) -> Unit, trailing: (@Composable () -> Unit)? = null
) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .toggleable(checked, role = Role.Checkbox, onValueChange = onChecked)
        .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(22.dp), tint = if (checked) AccentCyan else TextSecondary)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(label, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
            Text(if (checked) "Tamamlandı" else description, color = if (checked) AccentCyan else TextSecondary,
                style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 3.dp))
        }
        trailing?.invoke()
        Checkbox(checked, onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = AccentCyan, checkmarkColor = CanvasDark))
    }
}

@Composable
private fun DailyWaterCard(water: Int, target: Int, onAdd: () -> Unit, onRemove: () -> Unit, onSettings: () -> Unit) {
    DailySurface {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WaterDrop, null, tint = AccentCyan, modifier = Modifier.size(22.dp))
                Text("Günlük su", Modifier.weight(1f).padding(start = 10.dp), color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onSettings, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Outlined.Tune, "Su hedefi ve hatırlatma", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(String.format(dailyLocale, "%.2f", water / 1000.0).trimEnd('0').trimEnd(','),
                    color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                Text(" / ${String.format(dailyLocale, "%.2f", target / 1000.0).trimEnd('0').trimEnd(',')} L",
                    color = TextSecondary, modifier = Modifier.padding(bottom = 5.dp), style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.weight(1f))
                Text("${(water * 100 / target).coerceAtMost(100)}%", color = TextSecondary, modifier = Modifier.padding(bottom = 5.dp))
            }
            LinearProgressIndicator(progress = { (water.toFloat() / target).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = AccentCyan,
                trackColor = PanelNavyElevated, drawStopIndicator = {})
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onRemove, enabled = water > 0, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("−250 ml")
                }
                FilledTonalButton(onClick = onAdd, enabled = water < 5000, modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = AccentCyan.copy(alpha = .15f), contentColor = AccentCyan)) {
                    Icon(Icons.Outlined.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("250 ml ekle")
                }
            }
        }
    }
}

@Composable
private fun WaterSettingsDialog(prefs: SharedPreferences, target: Int, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableIntStateOf(target) }
    val reminderPrefs = remember { WinterArcNotificationHelper.getPrefs(context) }
    var reminder by remember { mutableStateOf(reminderPrefs.getBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, true)) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Su takibi") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Günlük hedef", color = TextSecondary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selected -= 250 }, enabled = selected > 250) { Icon(Icons.Outlined.Remove, "Hedefi azalt") }
                Text("$selected ml", Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { selected += 250 }, enabled = selected < 5000) { Icon(Icons.Outlined.Add, "Hedefi artır") }
            }
            Row(Modifier.fillMaxWidth().toggleable(reminder, role = Role.Switch, onValueChange = { reminder = it }),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Su hatırlatması"); Text("Her gün 15.00", style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                Switch(reminder, onCheckedChange = null)
            }
        }
    }, confirmButton = { TextButton(onClick = {
        prefs.edit().putInt("water_target_ml", selected).apply()
        reminderPrefs.edit().putBoolean(WinterArcNotificationHelper.KEY_WATER_ENABLED, reminder).apply()
        WinterArcNotificationHelper.syncAllReminders(context)
        onDismiss()
    }) { Text("Kaydet") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Vazgeç") } })
}
