package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.util.HealthSyncManager
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
internal fun DailyWatchCard(
    result: HealthSyncManager.HealthSyncResult?, isRefreshing: Boolean, isCached: Boolean,
    onOpenConnection: () -> Unit
) {
    val partial = result != null && (!result.hasStepsData || !result.hasSleepData ||
        !result.hasCaloriesData || !result.hasExerciseData)
    DailySurface {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Watch, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text("Huawei Sağlık", color = TextPrimary, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall)
                    Text(when {
                        isRefreshing -> "Kontrol ediliyor…"
                        isCached -> "Son kayıt gösteriliyor"
                        result == null -> "Henüz veri gelmedi"
                        partial -> "Bazı kayıtlar henüz gelmedi"
                        result.staleMetrics.isNotEmpty() -> "Bazı değerler önceki okumadan"
                        else -> "Güncellendi · ${Instant.ofEpochMilli(result.syncedAtMillis).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"))}"
                    }, color = TextSecondary, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 3.dp))
                }
                IconButton(onClick = onOpenConnection, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Outlined.Tune, "Huawei bağlantısı", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                WatchMetric("Adım", if (result?.hasStepsData == true) NumberFormat.getIntegerInstance(dailyLocale).format(result.stepsCount) else "—",
                    "adım", Icons.AutoMirrored.Outlined.DirectionsWalk, !isCached && result?.staleMetrics?.contains("steps") == true, Modifier.weight(1f))
                WatchMetric("Aktif kalori", if (result?.hasCaloriesData == true) result.activeCaloriesKcal.roundToInt().toString() else "—",
                    "kcal", Icons.Outlined.LocalFireDepartment, !isCached && result?.staleMetrics?.contains("calories") == true, Modifier.weight(1f))
            }
            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = BorderSubtle.copy(alpha = .55f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                WatchMetric("Egzersiz", if (result?.hasExerciseData == true) result.exerciseMinutesTotal.toString() else "—",
                    if (result?.hasExerciseData == true) "dk · ${result.exerciseCount} oturum" else "dk", Icons.Outlined.FitnessCenter,
                    !isCached && result?.staleMetrics?.contains("exercise") == true, Modifier.weight(1f))
                WatchMetric("Uyku", if (result?.hasSleepData == true) "${result.sleepMinutesTotal / 60} sa ${result.sleepMinutesTotal % 60} dk" else "—",
                    "uyku", Icons.Outlined.Bedtime, !isCached && result?.staleMetrics?.contains("sleep") == true, Modifier.weight(1f), inlineUnit = false)
            }
        }
    }
}

@Composable
private fun WatchMetric(label: String, value: String, unit: String, icon: ImageVector, stale: Boolean,
    modifier: Modifier = Modifier, inlineUnit: Boolean = true) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(15.dp))
            Text(label, color = TextSecondary, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, color = TextPrimary, fontSize = if (inlineUnit) 26.sp else 21.sp,
            fontWeight = FontWeight.Medium)
        Text(when { value == "—" -> "Kayıt yok"; stale -> "$unit · önceki okuma"; inlineUnit -> unit; else -> "Toplam süre" },
            color = TextSecondary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 2.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyHealthConnectionSheet(state: DailyHealthState, onConnect: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = PanelNavy,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Saat bağlantısı", color = TextPrimary, style = MaterialTheme.typography.headlineSmall)
            Text("Adım, aktif kalori, egzersiz ve uyku kayıtlarını Huawei Sağlık’tan alır.", color = TextSecondary)
            Surface(color = PanelNavyElevated, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (state.result != null) "Son okuma" else "Veri bekleniyor", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    if (state.isCached) Text("Şu an bağlantı yenilenemedi; önceki kayıtlar gösteriliyor.", color = TextSecondary)
                    Text(state.message.ifBlank { "Bağlantı kontrol ediliyor." }, color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text("Önce saati Huawei Sağlık ile eşitle. Huawei Sağlık’ın veri paylaşımı ayarlarında Health Connect varsa aç; ardından buradan okuma izni ver.",
                color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text("Bu paylaşım seçeneği yoksa izinler tek başına yeterli olmaz. Bazı Huawei sürümleri egzersiz oturumlarını paylaşmıyor. Gelmeyen kayıtlar sıfır sayılmaz.",
                color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text("Aktif kalori, dinlenirken yakılan kaloriyi içermez. Egzersiz süresi, paylaşılan tamamlanmış oturumların molalar dahil süresidir.",
                color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onConnect, enabled = !state.isRefreshing, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (state.isRefreshing) "Kontrol ediliyor…" else if (state.hasPermissions) "Yeniden kontrol et" else "İzin ver ve kontrol et")
            }
            OutlinedButton(onClick = { HealthSyncManager.launchInstalledHealthApp(context) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text("Huawei Sağlık’ı aç")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Outlined.OpenInNew, null, Modifier.size(16.dp))
            }
            TextButton(onClick = { HealthSyncManager.launchHealthConnectOrStore(context) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Health Connect izinleri")
            }
        }
    }
}
