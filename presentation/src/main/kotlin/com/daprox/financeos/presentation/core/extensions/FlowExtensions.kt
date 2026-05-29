package com.daprox.financeos.presentation.core.extensions

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
 * The optional `key1` and `key2` parameters allow you to specify extra dependencies
 * for the LaunchedEffect. Use them when the callback or event handler changes.
 *
 * @param T        The event type emitted by the flow.
 * @param flow     The event Flow to observe — typically a Channel converted to Flow.
 * @param key1     Optional dependency for LaunchedEffect; triggers re-collection if changed.
 * @param key2     Optional dependency for LaunchedEffect; triggers re-collection if changed.
 * @param onEvent  Callback invoked for each emitted event; fires once per event.
 *
 * Example:
 * ```kotlin
 * ObserveAsEvents(
 *   flow = viewModel.eventFlow,
 *   onEvent = { event ->
 *     when (event) {
 *       is UiEvent.Navigate -> navController.navigate(event.route)
 *       is UiEvent.ShowSnackbar -> { /* show snackbar */ }
 *     }
 *   }
 * )
 * ```
 *
 * @see com.daprox.financeos.presentation.core.ObserveAsEvents for the simpler overload.
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
