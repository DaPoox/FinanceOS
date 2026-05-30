package com.daprox.financeos.presentation.allocation

import androidx.compose.runtime.Stable

/**
 * UI state for the Allocation screen.
 *
 * Represents the complete state of the 3-step budget allocation wizard.
 *
 * @param isLoading Whether data is being loaded from the database
 * @param isError Whether a loading or processing error occurred
 * @param step The current step: 0 (income), 1 (template), 2 (adjustment)
 * @param monthLabel Localized month label (e.g., "Mai 2026")
 * @param income The monthly income entered by the user (numeric string)
 * @param selectedTemplate The selected template type for allocation copying
 * @param groups Envelope groups grouped by type (Fixes, Variables, Du mois, Permanentes, Épargne, Investissement)
 * @param remaining The unallocated amount (income - sum of envelope amounts)
 * @param isSaving Whether the allocation is currently being saved to the database
 * @param lastRemovedEnvelope The envelope that was swipe-deleted; non-null while the undo snackbar is showing
 * @param isFirstMonth True when no month exists yet (virgin app); drives 2-step flow and "Crée tes enveloppes" wording
 * @param isNewEnvelopeSheetVisible Whether the new envelope creation bottom sheet is visible
 * @param newEnvelopePresetType The envelope type key to pre-select in the new envelope sheet (e.g., "VARIABLE")
 */
@Stable
data class AllocationUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isFirstMonth: Boolean = false,
    val step: Int = 0,
    val monthLabel: String = "Mai 2026",
    val income: String = "4200",
    val selectedTemplate: TemplateTypeEnum = TemplateTypeEnum.PREVIOUS,
    val groups: List<AllocationEnvelopeGroup> = emptyList(),
    val remaining: Double = 0.0,
    val isSaving: Boolean = false,
    // Swipe-to-delete undo state — non-null while the undo snackbar is showing
    val lastRemovedEnvelope: AllocationEnvelopeUiState? = null,
    // New envelope bottom sheet
    val isNewEnvelopeSheetVisible: Boolean = false,
    val newEnvelopePresetType: String? = null,
)
