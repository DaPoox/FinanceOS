package com.daprox.financeos.core

// Typed result wrapper for operation outcomes across all layers.
// Loading state is intentionally omitted — it belongs in UiState.isLoading.
sealed interface Result<out T> {
    // Wraps a successful value returned from a repository or use case.
    data class Success<T>(val data: T) : Result<T>

    // Wraps a failure. Callers inspect exception.message or cast to domain-specific subtypes.
    data class Error(val exception: Throwable) : Result<Nothing>
}
