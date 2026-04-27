package com.daprox.financeos.presentation.envelopes.edit

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory

// UI model for a single editable allocation row.
// fraction is the live value mutated by slider / +/- actions.
data class EnvelopeEditItemUi(
    val id                  : String,
    val name                : String,
    val subtitle            : String,
    val category            : EnvelopeCategory,
    val fraction            : Float,            // 0f..1f, the editable allocation share
    val formattedAmount     : String,           // pre-computed from fraction × totalIncome
    val formattedPercentage : String,           // e.g. "40%"
)

// UI state for the Envelopes Edit screen.
@Stable
data class EnvelopesEditUiState(
    val monthLabel      : String                   = "",
    val totalIncome     : String                   = "",
    val allocatedLabel  : String                   = "",  // e.g. "89% Alloué"
    val allocatedAmount : String                   = "",  // e.g. "€7,476 / €8,400"
    val statusLabel        : String                   = "Équilibré",
    val items              : List<EnvelopeEditItemUi> = emptyList(),
    // true when total allocation exceeds 100% — confirm is blocked.
    val isOverAllocated    : Boolean                  = false,
    // e.g. "€924", euro overflow amount shown in the warning card.
    val overflowEurosLabel : String                   = "",
    // true when total allocation is below 100% — surplus nudge card is shown.
    val hasUnallocated        : Boolean               = false,
    // e.g. "€924", unallocated euro amount shown in the surplus card.
    val unallocatedEurosLabel : String                = "",
)
