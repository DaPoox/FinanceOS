package com.daprox.financeos.presentation.envelopedetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.EnvelopeTypeEnum as DomainEnvelopeType
import com.daprox.financeos.domain.usecase.ArchiveEnvelopeUseCase
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.ObserveCurrentMonthUseCase
import com.daprox.financeos.domain.usecase.ObserveEnvelopeTransactionsUseCase
import com.daprox.financeos.domain.usecase.ObserveMonthAllocationsUseCase
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset

private val FR_MONTHS = arrayOf("jan", "fév", "mar", "avr", "mai", "jun", "jul", "aoû", "sep", "oct", "nov", "déc")
private fun Long.toDateLabel(): String {
    val ld = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
    return "${ld.dayOfMonth} ${FR_MONTHS[ld.monthValue - 1]}"
}

private fun DomainEnvelopeType.toPresentation(): EnvelopeTypeEnum = EnvelopeTypeEnum.valueOf(name)

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Enveloppe fixe",
    EnvelopeTypeEnum.VARIABLE to "Variable standard",
    EnvelopeTypeEnum.MONTHLY to "Enveloppe du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanente · accumulée",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

/**
 * ViewModel for the Envelope Detail screen.
 *
 * Displays comprehensive details for a single envelope, including:
 * - Name, type label, allocated vs. spent amounts
 * - Progress bar with color coding (OK, WARNING, OVER)
 * - Accumulated balance (for permanent envelopes)
 * - Transaction list for the current month (sorted by date descending)
 * - Monthly spending history
 * - Status badge
 *
 * Features:
 * - Menu toggle (Rename, Archive)
 * - Archive confirmation dialog
 * - Loading, error, and retry states
 * - Transient UI state preservation (menu/dialog visibility across data reloads)
 *
 * State is provided via a [StateFlow<EnvelopeDetailUiState>]; events are sent through a [Channel<EnvelopeDetailUiEvent>].
 *
 * @param id The envelope ID to display details for
 * @param observeCurrentMonth UseCase to observe the current month
 * @param observeActiveEnvelopes UseCase to observe all active envelopes
 * @param observeMonthAllocations UseCase to observe allocations for the current month
 * @param observeEnvelopeTransactions UseCase to observe transactions for this envelope
 * @param archiveEnvelope UseCase to archive the envelope
 */
class EnvelopeDetailViewModel(
    private val id: String,
    private val observeCurrentMonth: ObserveCurrentMonthUseCase,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val observeMonthAllocations: ObserveMonthAllocationsUseCase,
    private val observeEnvelopeTransactions: ObserveEnvelopeTransactionsUseCase,
    private val archiveEnvelope: ArchiveEnvelopeUseCase,
) : ViewModel() {

    private val _retryTrigger = MutableStateFlow(0)

    private val _state = MutableStateFlow(EnvelopeDetailUiState(id = id))
    val state = _state.asStateFlow()

    private val _events = Channel<EnvelopeDetailUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        _retryTrigger
            .flatMapLatest {
                observeCurrentMonth()
                    .filterNotNull()
                    .flatMapLatest { month ->
                        combine(
                            observeActiveEnvelopes().map { list -> list.find { it.id == id } },
                            observeMonthAllocations(month.id),
                            observeEnvelopeTransactions(id, month.id),
                        ) { envelope, allocations, transactions ->
                            val env = envelope ?: return@combine _state.value.copy(isLoading = false)
                            val allocation = allocations.find { it.envelopeId == id }
                            val allocated = allocation?.allocated ?: 0.0
                            val accumulated = allocation?.accumulated ?: 0.0
                            val spent = transactions.sumOf { it.amount }
                            val type = env.type.toPresentation()
                            val status = when {
                                type == EnvelopeTypeEnum.FIXED -> EnvelopeStatusEnum.FIXED
                                spent > allocated -> EnvelopeStatusEnum.OVER
                                allocated > 0 && spent / allocated > 0.8 -> EnvelopeStatusEnum.WARNING
                                else -> EnvelopeStatusEnum.OK
                            }
                            // Preserve transient UI state (menu/dialog visibility)
                            val current = _state.value
                            EnvelopeDetailUiState(
                                isLoading = false,
                                id = id,
                                name = env.name,
                                typeLabel = TYPE_LABELS[type] ?: "",
                                type = type,
                                spent = spent,
                                allocated = allocated,
                                accumulated = accumulated,
                                status = status,
                                transactions = transactions
                                    .sortedByDescending { it.date }
                                    .map { tx -> TransactionUiState(tx.id, tx.note, tx.date.toDateLabel(), tx.amount) },
                                isMenuVisible = current.isMenuVisible,
                                isArchiveDialogVisible = current.isArchiveDialogVisible,
                            )
                        }
                    }
                    .catch { e ->
                        Log.e("EnvelopeDetailViewModel", "Flow error", e)
                        emit(EnvelopeDetailUiState(id = id, isLoading = false, isError = true))
                    }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    /**
     * Processes UI actions dispatched from the Envelope Detail screen.
     *
     * Action handlers:
     * - [EnvelopeDetailUiAction.OnBackClick]: Navigate back
     * - [EnvelopeDetailUiAction.OnModifierAllocationClick]: Reserved for future allocation editing
     * - [EnvelopeDetailUiAction.OnRetry]: Retry after an error
     * - [EnvelopeDetailUiAction.OnMenuClick]: Open the 3-dot overflow menu
     * - [EnvelopeDetailUiAction.OnMenuDismiss]: Close the menu
     * - [EnvelopeDetailUiAction.OnRenameClick]: Navigate to the envelope edit screen
     * - [EnvelopeDetailUiAction.OnArchiveClick]: Open the archive confirmation dialog
     * - [EnvelopeDetailUiAction.OnArchiveConfirm]: Archive the envelope (requires confirmation)
     * - [EnvelopeDetailUiAction.OnArchiveDismiss]: Close the archive dialog without archiving
     *
     * @param action The [EnvelopeDetailUiAction] to process
     */
    fun onAction(action: EnvelopeDetailUiAction) {
        viewModelScope.launch {
            when (action) {
                is EnvelopeDetailUiAction.OnBackClick ->
                    _events.send(EnvelopeDetailUiEvent.NavigateBack)

                is EnvelopeDetailUiAction.OnModifierAllocationClick ->
                    _events.send(EnvelopeDetailUiEvent.NavigateToEditEnvelope(id))

                is EnvelopeDetailUiAction.OnRetry -> {
                    _state.update { it.copy(isLoading = true, isError = false) }
                    _retryTrigger.update { it + 1 }
                }

                // ⋯ overflow menu
                is EnvelopeDetailUiAction.OnMenuClick ->
                    _state.update { it.copy(isMenuVisible = true) }

                is EnvelopeDetailUiAction.OnMenuDismiss ->
                    _state.update { it.copy(isMenuVisible = false) }

                is EnvelopeDetailUiAction.OnRenameClick -> {
                    _state.update { it.copy(isMenuVisible = false) }
                    _events.send(EnvelopeDetailUiEvent.NavigateToEditEnvelope(id))
                }

                is EnvelopeDetailUiAction.OnArchiveClick ->
                    _state.update { it.copy(isMenuVisible = false, isArchiveDialogVisible = true) }

                is EnvelopeDetailUiAction.OnArchiveConfirm -> archiveConfirmed()

                is EnvelopeDetailUiAction.OnArchiveDismiss ->
                    _state.update { it.copy(isArchiveDialogVisible = false) }
            }
        }
    }

    private fun archiveConfirmed() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, isArchiveDialogVisible = false) }
            when (archiveEnvelope(id)) {
                is Result.Success -> _events.send(EnvelopeDetailUiEvent.NavigateBack)
                is Result.Error -> _state.update { it.copy(isSaving = false) }
            }
        }
    }
}
