package com.daprox.financeos.presentation.envelopeform

import androidx.compose.runtime.Stable
import com.daprox.financeos.domain.model.EnvelopeTypeEnum

@Stable
data class EnvelopeFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isError: Boolean = false,
    // null = create mode; non-null = edit mode
    val envelopeId: String? = null,
    val name: String = "",
    val type: EnvelopeTypeEnum = EnvelopeTypeEnum.VARIABLE,
    val iconKey: String = "shopping_basket",
    // Monthly allocation amount
    val amount: String = "",
    // Used only for PERMANENT type — prior accumulated balance
    val accumulated: String = "",
    // Used only for SAVINGS type — legal cap (Livret A / LDDS)
    val cap: String = "",
)
