package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.example.data.model.EngineeringProject
import com.example.data.model.ProjectWorkflowStage
import com.example.data.model.RoadmapDataStore
import com.example.data.model.RoadmapProgressHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class CategorySubItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String = "",
    val description: String,
    val tag: String,
    val icon: ImageVector? = null
)

@Composable
fun CategorySubItemsHubView(
    categoryTitle: String, categoryEmoji: String, categorySubtitle: String, categoryDescription: String,
    accentColor: Color, subItems: List<CategorySubItem>, onBack: () -> Unit,
    modifier: Modifier = Modifier, categoryIcon: ImageVector? = null, onOpenThemePicker: () -> Unit = {}
) {
    var selected by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var query by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
    BackHandler { if (selected != null) selected = null else onBack() }
    selected?.let { id ->
        val back = { selected = null }
        when (id) {
            "sub_btk_akademi" -> BtkCoursesView(back, modifier)
            "sub_github" -> PersonalTrackerView(com.example.data.repository.TrackerKind.GITHUB, back, modifier)
            "sub_play_store" -> PersonalTrackerView(com.example.data.repository.TrackerKind.PLAY_STORE, back, modifier)
            "sub_card_sleights" -> PersonalTrackerView(com.example.data.repository.TrackerKind.CARD_TRICKS, back, modifier)
            "sub_anime_manhwa" -> com.example.ui.components.anime.AnimeTrackingHubView(onBack = back, modifier = modifier)
            "sub_knowledge_archive" -> CategorySubItemsHubView(
                "Konu arşivi", "", "Bir konu gerektiğinde buradan bulabilirsin.", "", accentColor,
                knowledgeArchiveItems, back, modifier = modifier, onOpenThemePicker = onOpenThemePicker
            )
            else -> SubItemChecklistView(subItemId = id, accentColor = accentColor, onBack = back, modifier = modifier)
        }
        return
    }
    val filtered = subItems.filter { query.isBlank() || "${it.title} ${it.subtitle} ${it.description}".contains(query, ignoreCase = true) }
    LazyColumn(
        modifier = modifier.fillMaxSize().background(CanvasDark).padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = TextPrimary) }
                Text(categoryTitle, color = TextPrimary, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                ThemeToggleButton(onOpenThemePicker = onOpenThemePicker)
            }
            Text(categorySubtitle, color = TextSecondary)
        }
        if (subItems.size > 6) item {
            OutlinedTextField(query, { query = it }, label = { Text("Konu ara") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        if (filtered.isEmpty()) item { Text("Eşleşen konu yok.", color = TextSecondary) }
        items(filtered, key = { it.id }) { entry ->
            Card(onClick = { selected = entry.id }, colors = CardDefaults.cardColors(containerColor = PanelNavy)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    entry.icon?.let { Icon(it, null, tint = accentColor, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(12.dp)) }
                    Column(Modifier.weight(1f)) {
                        Text(entry.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(entry.subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                }
            }
        }
    }
}
