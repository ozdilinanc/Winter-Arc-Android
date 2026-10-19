package com.example.ui.components

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.util.HealthSyncManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

internal class DailyHealthState(
    private val context: Context,
    private val date: LocalDate,
    private val scope: CoroutineScope
) {
    var result by mutableStateOf(HealthSyncManager.getLastSync(context, date.toString()))
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var isCached by mutableStateOf(result != null)
        private set
    var message by mutableStateOf("")
        private set
    var hasPermissions by mutableStateOf(false)
        private set

    fun refresh() {
        if (isRefreshing) return
        isRefreshing = true
        scope.launch {
            try {
                hasPermissions = HealthSyncManager.hasHealthPermissions(context)
                val read = HealthSyncManager.fetchHealthData(context, date)
                HealthSyncManager.saveLastSync(context, read.dateKey, read)
                result = HealthSyncManager.getLastSync(context, read.dateKey)
                isCached = !read.isSuccess && result != null
                message = read.message
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                isCached = result != null
                message = "Bağlantı kurulamadı. Yeniden deneyebilirsin."
            } finally {
                isRefreshing = false
            }
        }
    }
}

@Composable
internal fun rememberDailyHealthState(date: LocalDate): Pair<DailyHealthState, () -> Unit> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember(context, date) { DailyHealthState(context, date, scope) }
    val launcher = rememberLauncherForActivityResult(HealthSyncManager.createPermissionContract()) {
        state.refresh() // Also read any data whose permission was granted individually.
    }
    LaunchedEffect(state) { state.refresh() }
    val currentState by rememberUpdatedState(state)
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) currentState.refresh()
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    val connect: () -> Unit = {
        when {
            HealthSyncManager.checkHealthConnectAvailability(context) !=
                HealthSyncManager.HealthConnectAvailability.AVAILABLE ->
                HealthSyncManager.launchHealthConnectOrStore(context)
            !state.hasPermissions -> launcher.launch(HealthSyncManager.REQUIRED_HEALTH_PERMISSIONS)
            else -> state.refresh()
        }
    }
    return state to connect
}
