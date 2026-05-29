package com.daprox.financeos.presentation.fixes

sealed interface FixesUiAction {
    data object OnBackClick : FixesUiAction
    data object OnRetry : FixesUiAction
}
