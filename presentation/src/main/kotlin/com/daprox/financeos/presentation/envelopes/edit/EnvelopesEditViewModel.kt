package com.daprox.financeos.presentation.envelopes.edit

import androidx.lifecycle.ViewModel
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

class EnvelopesEditViewModel : ViewModel() {

    // Total monthly income used to compute amounts from fractions.
    private val totalIncomeRaw = 8_400.0

    private val _state = MutableStateFlow(
        EnvelopesEditUiState(
            monthLabel  = "AVRIL 2026",
            totalIncome = "€8,400",
            items       = initialItems(),
        ).withRecomputedSummary(totalIncomeRaw)
    )
    val state = _state.asStateFlow()

    fun onAction(action: EnvelopesEditUiAction) {
        when (action) {
            is EnvelopesEditUiAction.OnSliderChanged ->
                updateItem(action.id) { it.copy(fraction = action.fraction.coerceIn(0f, 1f)) }

            is EnvelopesEditUiAction.OnIncrement ->
                updateItem(action.id) { it.copy(fraction = (it.fraction + 0.01f).coerceAtMost(1f)) }

            is EnvelopesEditUiAction.OnDecrement ->
                updateItem(action.id) { it.copy(fraction = (it.fraction - 0.01f).coerceAtLeast(0f)) }

            // Persist changes — wired when domain layer is ready.
            is EnvelopesEditUiAction.OnConfirm -> Unit
            // Discard changes — navigation handled by the screen, nothing to do in VM.
            is EnvelopesEditUiAction.OnClose -> Unit
        }
    }

    // Updates a single item by id, then refreshes all formatted fields and the summary.
    private fun updateItem(id: String, transform: (EnvelopeEditItemUi) -> EnvelopeEditItemUi) {
        _state.update { current ->
            val updated = current.items.map { if (it.id == id) transform(it) else it }
            val recomputed = updated.map { it.withRecomputedDisplay(totalIncomeRaw) }
            current.copy(items = recomputed).withRecomputedSummary(totalIncomeRaw)
        }
    }

    private fun initialItems() = listOf(
        item("fixed",       "Fixed",       "Priorité Haute", EnvelopeCategory.FIXED_EXPENSES, 0.40f),
        item("investment",  "Investment",  "Croissance",     EnvelopeCategory.INVESTMENT,     0.20f),
        item("savings",     "Savings",     "Réserve",        EnvelopeCategory.SAVINGS,        0.15f),
        item("restaurants", "Restaurants", "Lifestyle",      EnvelopeCategory.OTHER,          0.08f),
        item("loisirs",     "Loisirs",     "Plaisir",        EnvelopeCategory.OTHER,          0.06f),
    )

    private fun item(
        id       : String,
        name     : String,
        subtitle : String,
        category : EnvelopeCategory,
        fraction : Float,
    ) = EnvelopeEditItemUi(
        id                  = id,
        name                = name,
        subtitle            = subtitle,
        category            = category,
        fraction            = fraction,
        formattedAmount     = fraction.toFormattedAmount(totalIncomeRaw),
        formattedPercentage = fraction.toFormattedPercentage(),
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

// Recomputes the display-only fields of a single item after its fraction changes.
private fun EnvelopeEditItemUi.withRecomputedDisplay(totalIncome: Double) = copy(
    formattedAmount     = fraction.toFormattedAmount(totalIncome),
    formattedPercentage = fraction.toFormattedPercentage(),
)

// Recomputes the summary fields (allocatedLabel, allocatedAmount, statusLabel) for the state.
private fun EnvelopesEditUiState.withRecomputedSummary(totalIncome: Double): EnvelopesEditUiState {
    val totalFraction  = items.sumOf { it.fraction.toDouble() }
    val allocatedPct   = (totalFraction * 100).roundToInt()
    // Euro amount is clamped at the total income — you can't spend more than you have.
    val allocatedEuros = (totalFraction.coerceAtMost(1.0) * totalIncome).roundToInt()
    val totalEuros     = totalIncome.roundToInt()
    val isOver           = allocatedPct > 100
    val isUnder          = !isOver && allocatedPct < 100
    val overflowEuros    = if (isOver) ((totalFraction - 1.0) * totalIncome).roundToInt() else 0
    val unallocatedEuros = if (isUnder) ((1.0 - totalFraction) * totalIncome).roundToInt() else 0

    val status = when {
        isOver              -> "Dépassé"
        allocatedPct == 100 -> "Complet"
        allocatedPct >= 80  -> "Équilibré"
        else                -> "Sous-alloué"
    }

    return copy(
        allocatedLabel        = "$allocatedPct% Alloué",
        allocatedAmount       = "€$allocatedEuros / €$totalEuros",
        statusLabel           = status,
        isOverAllocated       = isOver,
        overflowEurosLabel    = if (isOver) "€$overflowEuros" else "",
        hasUnallocated        = isUnder,
        unallocatedEurosLabel = if (isUnder) "€$unallocatedEuros" else "",
    )
}

private fun Float.toFormattedAmount(totalIncome: Double): String {
    val amount = (this * totalIncome).roundToInt()
    return "€$amount"
}

private fun Float.toFormattedPercentage(): String {
    val pct = (this * 100).roundToInt()
    return "$pct%"
}
