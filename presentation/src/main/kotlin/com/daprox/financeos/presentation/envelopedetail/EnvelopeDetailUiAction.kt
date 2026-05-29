package com.daprox.financeos.presentation.envelopedetail

sealed interface EnvelopeDetailUiAction {
    data object OnBackClick : EnvelopeDetailUiAction
    data object OnModifierAllocationClick : EnvelopeDetailUiAction
    data object OnRetry : EnvelopeDetailUiAction
    // ⋯ 3-dot overflow menu
    data object OnMenuClick : EnvelopeDetailUiAction
    data object OnMenuDismiss : EnvelopeDetailUiAction
    data object OnRenameClick : EnvelopeDetailUiAction
    data object OnArchiveClick : EnvelopeDetailUiAction
    data object OnArchiveConfirm : EnvelopeDetailUiAction
    data object OnArchiveDismiss : EnvelopeDetailUiAction
}
