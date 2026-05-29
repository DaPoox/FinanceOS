package com.daprox.financeos.presentation.envelopeform

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.core.Result
import com.daprox.financeos.domain.model.Envelope
import com.daprox.financeos.domain.model.EnvelopeTypeEnum
import com.daprox.financeos.domain.usecase.ArchiveEnvelopeUseCase
import com.daprox.financeos.domain.usecase.ObserveActiveEnvelopesUseCase
import com.daprox.financeos.domain.usecase.SaveEnvelopeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for envelope creation and editing screens.
 *
 * Manages form state (name, type, icon, amount, accumulated, cap) and provides two modes:
 * - **Create**: null [envelopeId], optionally pre-select type via [presetTypeKey]
 * - **Edit**: non-null [envelopeId], loads envelope and populates form
 *
 * Validates before save (name required) and persists via [SaveEnvelopeUseCase].
 * Deletion (edit mode) is handled via [ArchiveEnvelopeUseCase].
 *
 * @param envelopeId null = create mode; non-null = edit mode for that envelope
 * @param presetTypeKey serialized EnvelopeTypeEnum name to pre-select in create mode (e.g. "VARIABLE")
 * @param observeActiveEnvelopes fetches active envelopes when loading in edit mode
 * @param saveEnvelope persists new or updated envelope
 * @param archiveEnvelope archives (soft-deletes) envelope in edit mode
 */
class EnvelopeFormViewModel(
    private val envelopeId: String?,
    presetTypeKey: String?,
    private val observeActiveEnvelopes: ObserveActiveEnvelopesUseCase,
    private val saveEnvelope: SaveEnvelopeUseCase,
    private val archiveEnvelope: ArchiveEnvelopeUseCase,
) : ViewModel() {

    private val presetType: EnvelopeTypeEnum? =
        presetTypeKey?.let { runCatching { EnvelopeTypeEnum.valueOf(it) }.getOrNull() }

    private val _state = MutableStateFlow(
        EnvelopeFormUiState(
            envelopeId = envelopeId,
            type = presetType ?: EnvelopeTypeEnum.VARIABLE,
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<EnvelopeFormUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (envelopeId != null) loadEnvelope(envelopeId)
    }

    /** Fetches envelope data and populates form fields in edit mode. */
    private fun loadEnvelope(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val envelope = observeActiveEnvelopes().first().find { it.id == id }
            if (envelope != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = envelope.name,
                        type = envelope.type,
                        iconKey = envelope.iconKey,
                    )
                }
            } else {
                Log.e("EnvelopeFormViewModel", "Envelope $id not found")
                _state.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    /** Handles all user actions from the form screen. */
    fun onAction(action: EnvelopeFormUiAction) {
        viewModelScope.launch {
            when (action) {
                is EnvelopeFormUiAction.OnNameChanged ->
                    _state.update { it.copy(name = action.value) }

                is EnvelopeFormUiAction.OnTypeSelected ->
                    _state.update { it.copy(type = action.type) }

                is EnvelopeFormUiAction.OnIconSelected ->
                    _state.update { it.copy(iconKey = action.key) }

                is EnvelopeFormUiAction.OnAmountChanged ->
                    _state.update { it.copy(amount = action.value) }

                is EnvelopeFormUiAction.OnAccumulatedChanged ->
                    _state.update { it.copy(accumulated = action.value) }

                is EnvelopeFormUiAction.OnCapChanged ->
                    _state.update { it.copy(cap = action.value) }

                is EnvelopeFormUiAction.OnSaveClick -> save()

                is EnvelopeFormUiAction.OnDeleteClick -> deleteConfirmed()

                is EnvelopeFormUiAction.OnBackClick ->
                    _events.send(EnvelopeFormUiEvent.NavigateBack)
            }
        }
    }

    /** Validates form and persists envelope. Clears isSaving flag on error. */
    private fun save() {
        viewModelScope.launch {
            val s = _state.value
            if (s.name.isBlank()) return@launch
            _state.update { it.copy(isSaving = true) }
            val envelope = Envelope(
                id = envelopeId ?: "",
                name = s.name.trim(),
                type = s.type,
                iconKey = s.iconKey,
                colorHex = colorHexForType(s.type),
                isActive = true,
            )
            when (saveEnvelope(envelope)) {
                is Result.Success -> _events.send(EnvelopeFormUiEvent.NavigateBack)
                is Result.Error -> _state.update { it.copy(isSaving = false) }
            }
        }
    }

    /** Archives (soft-deletes) the envelope in edit mode. */
    private fun deleteConfirmed() {
        val id = envelopeId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            when (archiveEnvelope(id)) {
                is Result.Success -> _events.send(EnvelopeFormUiEvent.NavigateBack)
                is Result.Error -> _state.update { it.copy(isSaving = false) }
            }
        }
    }

    /** Returns hex color for envelope type; used at persist time. */
    private fun colorHexForType(type: EnvelopeTypeEnum): String = when (type) {
        EnvelopeTypeEnum.FIXED -> "#4a5568"
        EnvelopeTypeEnum.VARIABLE -> "#e8eef5"
        EnvelopeTypeEnum.MONTHLY -> "#fb923c"
        EnvelopeTypeEnum.PERMANENT -> "#22c55e"
        EnvelopeTypeEnum.SAVINGS -> "#7eb8f7"
        EnvelopeTypeEnum.INVESTMENT -> "#a78bfa"
    }
}
