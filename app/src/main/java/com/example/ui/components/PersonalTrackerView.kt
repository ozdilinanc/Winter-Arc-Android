package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.repository.*
import com.example.ui.theme.*

@Composable
fun PersonalTrackerView(kind: TrackerKind, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PersonalTrackerRepository.PREFS_NAME, Context.MODE_PRIVATE) }
    var revision by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<TrackerEntry?>(null) }
    var deleting by remember { mutableStateOf<TrackerEntry?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val entries = remember(kind, revision) {
        runCatching { PersonalTrackerRepository.getEntries(prefs, kind) }
            .onFailure { error = "Kayıtlar okunamadı. Verilerin korunuyor." }.getOrDefault(emptyList())
    }
    BackHandler {
        when { deleting != null -> deleting = null; editing != null -> editing = null; else -> onBack() }
    }
    editing?.let { entry ->
        TrackerEntryEditor(kind, entry, onDismiss = { editing = null }, onSave = { updated ->
            runCatching { PersonalTrackerRepository.save(prefs, kind, updated) }
                .onSuccess { revision++; editing = null }
                .onFailure { error = "Kayıt kaydedilemedi. Mevcut veriler değiştirilmedi." }
        }, onDelete = if (entries.any { it.id == entry.id }) {{ deleting = entry }} else null)
    }
    deleting?.let { entry ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Kaydı sil?") },
            text = { Text(entry.name) }, confirmButton = {
                TextButton(onClick = {
                    runCatching { PersonalTrackerRepository.delete(prefs, kind, entry.id) }
                        .onSuccess { revision++; deleting = null; editing = null }
                        .onFailure { error = "Kayıt silinemedi."; deleting = null }
                }) { Text("Sil") }
            }, dismissButton = { TextButton(onClick = { deleting = null }) { Text("Vazgeç") } })
    }
    if (editing != null) return
    LazyColumn(
        modifier = modifier.fillMaxSize().background(CanvasDark).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = TextPrimary) }
                Text(kind.title, modifier = Modifier.padding(top = 12.dp), color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            }
            Text(when (kind) {
                TrackerKind.GITHUB -> "Backend ve mobil projelerin. Küçük, düzenli adımlarla ilerle."
                TrackerKind.PLAY_STORE -> "Yayındaki uygulamaların ve sıradaki fikirlerin."
                TrackerKind.CARD_TRICKS -> "Öğrenmek istediğin numaralar ve hatırlatma notların."
            }, color = TextSecondary)
            Button(onClick = { editing = TrackerEntry(name = "") }, enabled = error == null) { Text("${kind.singular} ekle") }
        }
        error?.let { message -> item { Text(message, color = TextSecondary) } }
        if (entries.isEmpty() && error == null) item { Text("Henüz kayıt yok. İstediğinde ilkini ekleyebilirsin.", color = TextSecondary) }
        items(entries, key = { it.id }) { entry ->
            Card(onClick = { editing = entry }, colors = CardDefaults.cardColors(containerColor = PanelNavy)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.name, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text(if (kind == TrackerKind.GITHUB) "${entry.discipline} · ${kind.states[entry.status]}" else kind.states[entry.status], color = AccentCyan)
                    if (entry.requirements.isNotBlank()) Text("Gerekenler: ${entry.requirements}", color = TextSecondary)
                    if (entry.notes.isNotBlank()) Text(entry.notes, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (entry.url.isNotBlank()) TextButton(onClick = {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(entry.url))) }
                        }) { Text("Bağlantıyı aç") }
                        TextButton(onClick = { editing = entry }) { Text("Düzenle") }
                    }
                }
            }
        }
    }

}

@Composable
private fun TrackerEntryEditor(
    kind: TrackerKind, entry: TrackerEntry, onDismiss: () -> Unit,
    onSave: (TrackerEntry) -> Unit, onDelete: (() -> Unit)?
) {
    var name by remember(entry.id) { mutableStateOf(entry.name) }
    var status by remember(entry.id) { mutableIntStateOf(entry.status) }
    var url by remember(entry.id) { mutableStateOf(entry.url) }
    var notes by remember(entry.id) { mutableStateOf(entry.notes) }
    var requirements by remember(entry.id) { mutableStateOf(entry.requirements) }
    var discipline by remember(entry.id) { mutableStateOf(entry.discipline) }
    val validUrl = url.isBlank() || runCatching {
        val uri = Uri.parse(url.trim())
        uri.scheme == "https" && !uri.host.isNullOrBlank()
    }.getOrDefault(false)
    Column(Modifier.fillMaxSize().background(CanvasDark).imePadding().padding(16.dp)) {
        Row {
            IconButton(onClick = onDismiss) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = TextPrimary) }
            Text(kind.singular, color = TextPrimary, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
        }
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("İsim") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            kind.states.forEachIndexed { index, label ->
                Row(Modifier.fillMaxWidth().clickable { status = index }) {
                    RadioButton(status == index, onClick = { status = index })
                    Text(label, Modifier.padding(top = 12.dp), color = TextPrimary)
                }
            }
            if (kind == TrackerKind.GITHUB) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Backend", "Mobil").forEach { label ->
                        FilterChip(discipline == label, onClick = { discipline = label }, label = { Text(label) })
                    }
                }
            }
            if (kind == TrackerKind.CARD_TRICKS) {
                OutlinedTextField(requirements, { requirements = it }, label = { Text("Gerekenler") }, placeholder = { Text("Deste, özel kart vb.") }, modifier = Modifier.fillMaxWidth())
            } else {
                OutlinedTextField(url, { url = it }, label = { Text("Bağlantı (isteğe bağlı)") }, isError = !validUrl,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { if (!validUrl) Text("https:// ile başlayan bir bağlantı gir.") })
            }
            OutlinedTextField(notes, { notes = it }, label = { Text(if (kind == TrackerKind.CARD_TRICKS) "Nasıl yapılıyor?" else "Notlar / sıradaki adım") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            onDelete?.let { action -> TextButton(onClick = action) { Text("Kaydı sil") } }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Vazgeç") }
            Button(onClick = {
                onSave(entry.copy(name = name.trim(), status = status, url = url.trim(), requirements = requirements.trim(), notes = notes.trim(), discipline = discipline))
            }, enabled = name.isNotBlank() && validUrl) { Text("Kaydet") }
        }
    }
}
