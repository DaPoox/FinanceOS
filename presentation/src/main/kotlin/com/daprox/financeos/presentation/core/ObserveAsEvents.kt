package com.daprox.financeos.presentation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Collects a Flow as one-shot events, respecting the composition lifecycle.
 *
 * Use in ScreenRoot composables to consume Channel-backed UiEvent flows (navigation,
 * snackbar, bottom sheet). Automatically stops collecting when the lifecycle is paused
 * and resumes when it returns to the STARTED state.
 *
 * @param T        The event type emitted by the flow.
 * @param flow     The event Flow to observe — typically a Channel converted to Flow.
 * @param onEvent  Callback invoked for each emitted event; fires once per event.
 *
 * @see FlowExtensions.ObserveAsEvents for the overload with key parameters.
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    onEvent: (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
