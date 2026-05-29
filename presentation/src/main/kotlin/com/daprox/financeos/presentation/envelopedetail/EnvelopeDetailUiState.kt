package com.daprox.financeos.presentation.envelopedetail

import androidx.compose.runtime.Stable
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

/**
 * UI state for the Envelope Detail screen.
 *
 * Represents the complete state of the envelope detail view, including envelope data,
 * spending metrics, and transient UI state (menu/dialog visibility).
 *
 * @param isLoading Whether data is being loaded from the database
 * @param isError Whether a loading or processing error occurred
 * @param isSaving Whether an archive operation is in progress
 * @param id The envelope's unique identifier
 * @param name The envelope's display name
 * @param typeLabel Localized type label (e.g., "Enveloppe fixe", "Variable standard")
 * @param type The envelope's [EnvelopeTypeEnum]
 * @param spent The amount spent this month
 * @param allocated The amount allocated this month
 * @param accumulated The accumulated balance (for permanent envelopes, carries over month-to-month)
 * @param status The [EnvelopeStatusEnum] status badge (FIXED, OK, WARNING, OVER)
 * @param transactions Transactions for this envelope this month, sorted by date descending
 * @param monthlyHistory Monthly spending history (last N months)
 * @param monthsAgo How many months of history are shown
 * @param isMenuVisible Whether the 3-dot overflow menu is visible
 * @param isArchiveDialogVisible Whether the archive confirmation dialog is visible
 */
@Stable
data class EnvelopeDetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isSaving: Boolean = false,
    val id: String = "",
    val name: String = "",
    val typeLabel: String = "",
    val type: EnvelopeTypeEnum = EnvelopeTypeEnum.VARIABLE,
    val spent: Double = 0.0,
    val allocated: Double = 0.0,
    val accumulated: Double = 0.0,
    val status: EnvelopeStatusEnum = EnvelopeStatusEnum.OK,
    val transactions: List<TransactionUiState> = emptyList(),
    val monthlyHistory: List<Double> = emptyList(),
    val monthsAgo: Int = 0,
    // ⋯ menu state
    val isMenuVisible: Boolean = false,
    val isArchiveDialogVisible: Boolean = false,
)
