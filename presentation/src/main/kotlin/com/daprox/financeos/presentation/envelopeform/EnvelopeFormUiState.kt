package com.daprox.financeos.presentation.envelopeform

import androidx.compose.runtime.Stable
import com.daprox.financeos.domain.model.EnvelopeTypeEnum

/**
 * UI state for the envelope form screen (create or edit mode).
 *
 * Tracks form input, loading/saving states, and validation errors. The form supports all envelope
 * types (FIXED, VARIABLE, MONTHLY, PERMANENT, SAVINGS, INVESTMENT) with conditional display of
 * amount, accumulated balance (PERMANENT only), and cap (SAVINGS only).
 *
 * @property isLoading loading state while fetching envelope data in edit mode
 * @property isSaving saving state during form submission
 * @property isError error state if data loading fails
 * @property envelopeId null = create mode; non-null = edit mode for that envelope
 * @property name envelope display name (max 50 chars typically)
 * @property type envelope classification (FIXED, VARIABLE, MONTHLY, PERMANENT, SAVINGS, INVESTMENT)
 * @property iconKey key for the selected envelope icon (e.g. "shopping_basket")
 * @property amount monthly allocation amount as a string
 * @property accumulated prior accumulated balance (PERMANENT envelopes only)
 * @property cap legal savings cap like Livret A or LDDS max (SAVINGS envelopes only)
 */
@Stable
data class EnvelopeFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isError: Boolean = false,
    val envelopeId: String? = null,
    val name: String = "",
    val type: EnvelopeTypeEnum = EnvelopeTypeEnum.VARIABLE,
    val iconKey: String = "shopping_basket",
    val amount: String = "",
    val accumulated: String = "",
    val cap: String = "",
)
