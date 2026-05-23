package com.daprox.financeos.presentation.envelopedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private val TYPE_LABELS = mapOf(
    EnvelopeTypeEnum.FIXED to "Enveloppe fixe",
    EnvelopeTypeEnum.VARIABLE to "Variable standard",
    EnvelopeTypeEnum.MONTHLY to "Enveloppe du mois",
    EnvelopeTypeEnum.PERMANENT to "Permanente • accumulée",
    EnvelopeTypeEnum.SAVINGS to "Épargne",
    EnvelopeTypeEnum.INVESTMENT to "Investissement",
)

private val MOCK_ENVELOPES = mapOf(
    "loyer" to EnvelopeDetailUiState(
        id = "loyer",
        name = "Loyer",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.FIXED]!!,
        type = EnvelopeTypeEnum.FIXED,
        spent = 900.0,
        allocated = 900.0,
        status = EnvelopeStatusEnum.FIXED,
        transactions = listOf(
            TransactionUiState("t1", "Virement loyer", "1 mai", 900.0),
        ),
    ),
    "courses" to EnvelopeDetailUiState(
        id = "courses",
        name = "Courses",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.VARIABLE]!!,
        type = EnvelopeTypeEnum.VARIABLE,
        spent = 287.0,
        allocated = 420.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t2", "Lidl", "12 mai", 42.0),
            TransactionUiState("t3", "Carrefour", "8 mai", 95.0),
            TransactionUiState("t4", "Biocoop", "5 mai", 67.0),
            TransactionUiState("t5", "Marché", "3 mai", 83.0),
        ),
    ),
    "restos" to EnvelopeDetailUiState(
        id = "restos",
        name = "Restos",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.VARIABLE]!!,
        type = EnvelopeTypeEnum.VARIABLE,
        spent = 134.0,
        allocated = 120.0,
        status = EnvelopeStatusEnum.OVER,
        transactions = listOf(
            TransactionUiState("t6", "Sushi House", "15 mai", 38.0),
            TransactionUiState("t7", "Brasserie du Nord", "9 mai", 52.0),
            TransactionUiState("t8", "McDonald's", "2 mai", 14.0),
            TransactionUiState("t9", "Pizzeria Napoli", "1 mai", 30.0),
        ),
    ),
    "voyage" to EnvelopeDetailUiState(
        id = "voyage",
        name = "Voyage été",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.MONTHLY]!!,
        type = EnvelopeTypeEnum.MONTHLY,
        spent = 80.0,
        allocated = 400.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t10", "Booking hôtel", "10 mai", 80.0),
        ),
    ),
    "fonds-urgence" to EnvelopeDetailUiState(
        id = "fonds-urgence",
        name = "Fonds urgence",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.PERMANENT]!!,
        type = EnvelopeTypeEnum.PERMANENT,
        spent = 0.0,
        allocated = 200.0,
        accumulated = 1_400.0,
        status = EnvelopeStatusEnum.OK,
        transactions = emptyList(),
        monthlyHistory = listOf(200.0, 400.0, 600.0, 800.0, 1000.0, 1200.0, 1400.0),
        monthsAgo = 7,
    ),
    "epargne" to EnvelopeDetailUiState(
        id = "epargne",
        name = "Épargne",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.SAVINGS]!!,
        type = EnvelopeTypeEnum.SAVINGS,
        spent = 300.0,
        allocated = 500.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t11", "Virement Livret A", "5 mai", 300.0),
        ),
    ),
    "etf" to EnvelopeDetailUiState(
        id = "etf",
        name = "ETF World",
        typeLabel = TYPE_LABELS[EnvelopeTypeEnum.INVESTMENT]!!,
        type = EnvelopeTypeEnum.INVESTMENT,
        spent = 150.0,
        allocated = 300.0,
        status = EnvelopeStatusEnum.OK,
        transactions = listOf(
            TransactionUiState("t12", "Achat ETF MSCI World", "6 mai", 150.0),
        ),
    ),
)

class EnvelopeDetailViewModel(private val id: String) : ViewModel() {

    private val _state = MutableStateFlow(
        MOCK_ENVELOPES[id] ?: EnvelopeDetailUiState(id = id, name = id)
    )
    val state = _state.asStateFlow()

    private val _events = Channel<EnvelopeDetailUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: EnvelopeDetailUiAction) {
        viewModelScope.launch {
            when (action) {
                is EnvelopeDetailUiAction.OnBackClick -> _events.send(EnvelopeDetailUiEvent.NavigateBack)
                is EnvelopeDetailUiAction.OnModifierAllocationClick -> Unit
            }
        }
    }
}
