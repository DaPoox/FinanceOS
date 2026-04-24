package com.daprox.financeos.presentation.envelopes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.component.IconPosition
import com.daprox.financeos.presentation.core.designsystem.component.TextWithIcon
import com.daprox.financeos.presentation.envelopes.component.EnvelopeRowCard
import com.daprox.financeos.presentation.envelopes.component.EnvelopesTopBar
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import com.daprox.financeos.presentation.envelopes.model.EnvelopeUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun EnvelopesScreenRoot(
    onNavigateToEdit : () -> Unit          = {},
    viewModel        : EnvelopesViewModel  = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EnvelopesScreen(state = state, onAction = viewModel::onAction, onNavigateToEdit = onNavigateToEdit)
}

@Composable
fun EnvelopesScreen(
    state            : EnvelopesUiState,
    onAction         : (EnvelopesUiAction) -> Unit,
    onNavigateToEdit : () -> Unit = {},
) {
    Scaffold(
        topBar = { EnvelopesTopBar(onEditClick = onNavigateToEdit) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AllocationOverviewHeader(totalFormattedAmount = state.totalFormattedAmount)

            // Envelope rows — each can expand to reveal its sublist.
            state.envelopes.forEach { envelope ->
                EnvelopeRowCard(
                    envelope   = envelope,
                    isExpanded = envelope.id == state.expandedId,
                    onClick    = { onAction(EnvelopesUiAction.OnEnvelopeClick(envelope.id)) },
                )
            }

            // Ghost button for adding a new allocation category.
            AddAllocationButton(onClick = { onAction(EnvelopesUiAction.OnAddAllocationClick) })

         //   InsightOfTheWeekCard()

            Spacer(Modifier.height(8.dp))
        }
    }
}

// "ALLOCATION OVERVIEW" label + total budget amount.
@Composable
private fun AllocationOverviewHeader(
    totalFormattedAmount : String,
    modifier             : Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = 4.dp)) {
        Text(
            text  = "ALLOCATION OVERVIEW",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = totalFormattedAmount,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// Ghost (outline) button — matches Figma's dashed/outline "Add New Allocation" style.
@Composable
private fun AddAllocationButton(
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        TextButton(
            onClick  = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            TextWithIcon(
                text  = "Add New Allocation",
                icon  = {
                    // Circular green badge with a white + icon inside.
                    Box(
                        modifier         = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Add,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.onPrimary,
                            modifier           = Modifier.size(14.dp),
                        )
                    }
                },
                style        = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color        = MaterialTheme.colorScheme.onSurfaceVariant,
                iconPosition = IconPosition.START,
                iconSpacing  = 10.dp,
            )
        }
    }
}

// Dimmed insight card — a quote/tip of the week shown at the bottom of the screen.
// Content is static mock — will be driven by data once the insight system is built.
@Composable
private fun InsightOfTheWeekCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f))
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier              = Modifier.fillMaxWidth(),
        ) {
            Text(
                text  = "INSIGHT OF THE WEEK",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.0.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text      = "\"Allocating before you spend is the only budget that actually works. Make the decision once — then let the envelopes do the discipline.\"",
            style     = MaterialTheme.typography.bodySmall.copy(
                fontWeight  = FontWeight.Normal,
                lineHeight  = 20.sp,
            ),
            color     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Start,
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopesScreenPreview() {
    FinanceOSTheme {
        EnvelopesScreen(
            state    = EnvelopesUiState(
                totalFormattedAmount = "8 400,00 €",
                envelopes            = listOf(
                    EnvelopeUi("fixed",   "Fixed Expenses", "Rent, utilities", "2 940,00 €", "35 %", 0.35f, EnvelopeCategory.FIXED_EXPENSES),
                    EnvelopeUi("invest",  "Investment",     "ETF, stocks",     "1 680,00 €", "20 %", 0.20f, EnvelopeCategory.INVESTMENT),
                    EnvelopeUi("savings", "Savings",        "Emergency fund",  "1 260,00 €", "15 %", 0.15f, EnvelopeCategory.SAVINGS),
                    EnvelopeUi("other",   "Other",          "Food, leisure",   "2 520,00 €", "30 %", 0.30f, EnvelopeCategory.OTHER),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopesScreenExpandedPreview() {
    FinanceOSTheme {
        EnvelopesScreen(
            state    = EnvelopesUiState(
                totalFormattedAmount = "8 400,00 €",
                expandedId           = "fixed",
                envelopes            = listOf(
                    EnvelopeUi(
                        id                  = "fixed",
                        name                = "Fixed Expenses",
                        subtitle            = "Rent, utilities, insurance",
                        formattedAmount     = "2 940,00 €",
                        formattedPercentage = "35 %",
                        fraction            = 0.35f,
                        category            = EnvelopeCategory.FIXED_EXPENSES,
                        children            = listOf(
                            EnvelopeUi("fixed_rent", "Rent", "Monthly lease", "1 200,00 €", "14 %", 0.14f, EnvelopeCategory.FIXED_EXPENSES),
                            EnvelopeUi("fixed_util", "Utilities", "Electricity, water", "180,00 €", "2 %", 0.02f, EnvelopeCategory.FIXED_EXPENSES),
                        ),
                    ),
                    EnvelopeUi("invest",  "Investment", "ETF, stocks",    "1 680,00 €", "20 %", 0.20f, EnvelopeCategory.INVESTMENT),
                    EnvelopeUi("savings", "Savings",    "Emergency fund", "1 260,00 €", "15 %", 0.15f, EnvelopeCategory.SAVINGS),
                    EnvelopeUi("other",   "Other",      "Food, leisure",  "2 520,00 €", "30 %", 0.30f, EnvelopeCategory.OTHER),
                ),
            ),
            onAction = {},
        )
    }
}
