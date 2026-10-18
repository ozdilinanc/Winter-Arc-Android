package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun BtkCoursesView(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(RoadmapProgressHelper.PREFS_ROADMAP, Context.MODE_PRIVATE) }
    var revision by remember { mutableIntStateOf(0) }
    BackHandler(onBack = onBack)
    LazyColumn(
        modifier = modifier.fillMaxSize().background(CanvasDark).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = TextPrimary) }
                Text("BTK kursları", color = TextPrimary, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.titleLarge)
            }
            Text("İlgilendiğin eğitimleri aç, bitirdiklerini işaretle.", color = TextSecondary)
        }
        BtkCourseCatalog.courses.groupBy { it.group }.forEach { (group, courses) ->
            item { Text(group, color = AccentCyan, style = MaterialTheme.typography.titleMedium) }
            courses.forEach { course ->
                item(key = course.id) {
                    val key = "status_${course.id}"
                    val completed = remember(revision) { TopicProgressState.fromKey(prefs.getString(key, null)) == TopicProgressState.COMPLETED }
                    Card(colors = CardDefaults.cardColors(containerColor = PanelNavy)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Row {
                                Text(course.title, color = TextPrimary, modifier = Modifier.weight(1f).padding(top = 12.dp))
                                Checkbox(completed, onCheckedChange = {
                                    prefs.edit().putString(key, if (it) TopicProgressState.COMPLETED.key else TopicProgressState.NOT_STARTED.key).apply()
                                    revision++
                                })
                            }
                            TextButton(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(course.url))) } }) {
                                Text(when {
                                    course.url.contains("kariyerrehberi") -> "BTK rehberinde bul"
                                    course.url.contains("catalog") -> "BTK kataloğunda bul"
                                    else -> "Eğitimi aç"
                                })
                            }
                        }
                    }
                }
            }
        }
        item { Text("Eğitim ve sertifika koşulları BTK sayfalarında yer alıyor.", color = TextSecondary, style = MaterialTheme.typography.bodySmall) }
    }
}
