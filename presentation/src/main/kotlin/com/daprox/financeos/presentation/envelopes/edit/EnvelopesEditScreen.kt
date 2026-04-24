package com.daprox.financeos.presentation.envelopes.edit

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.categoryColors
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import org.koin.androidx.compose.koinViewModel

@Composable
fun EnvelopesEditScreenRoot(
    viewModel      : EnvelopesEditViewModel = koinViewModel(),
    onNavigateBack : () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EnvelopesEditScreen(
        state          = state,
        onAction       = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun EnvelopesEditScreen(
    state          : EnvelopesEditUiState,
    onAction       : (EnvelopesEditUiAction) -> Unit,
    onNavigateBack : () -> Unit = {},
) {
    Scaffold(
        topBar = {
            EnvelopesEditTopBar(
                onBack    = onNavigateBack,
                onConfirm = {
                    onAction(EnvelopesEditUiAction.OnConfirm)
                    onNavigateBack()
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Text(
                text  = state.monthLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = state.totalIncome,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Revenu du mois",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )

            Spacer(Modifier.height(28.dp))

            // ── Allocation status card ────────────────────────────────────────
            AllocationStatusCard(state = state)

            Spacer(Modifier.height(24.dp))

            // ── Envelope edit cards ───────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.items.forEach { item ->
                    EnvelopeEditCard(
                        item     = item,
                        onAction = onAction,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

// Fixed frosted-glass top bar: back arrow · "ENVELOPES" · confirm check.
@Composable
private fun EnvelopesEditTopBar(
    onBack    : () -> Unit,
    onConfirm : () -> Unit,
    modifier  : Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
            .height(56.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text  = "ENVELOPES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = 3.sp,
            ),
            color     = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        IconButton(onClick = onConfirm) {
            Icon(
                imageVector        = Icons.Default.CheckCircle,
                contentDescription = "Confirm",
                tint               = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ── Allocation Status Card ────────────────────────────────────────────────────

@Composable
private fun AllocationStatusCard(
    state    : EnvelopesEditUiState,
    modifier : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(20.dp),
    ) {
        // Header: label + status badge
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = "Allocation Mensuelle",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // "Équilibré" status pill
            Row(
                modifier          = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Text(
                    text  = state.statusLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.Black,
                        letterSpacing = 1.sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Segmented allocation bar
        AllocationBar(items = state.items)

        Spacer(Modifier.height(12.dp))

        // Footer: percentage + amount
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = state.allocatedLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text  = state.allocatedAmount,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// Proportional horizontal bar made of coloured segments — one per envelope.
// The unallocated remainder is shown as a muted trailing segment.
@Composable
private fun AllocationBar(
    items    : List<EnvelopeEditItemUi>,
    modifier : Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50)),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val totalFraction = items.sumOf { it.fraction.toDouble() }.toFloat().coerceAtMost(1f)

        items.forEach { item ->
            if (item.fraction > 0f) {
                Box(
                    modifier = Modifier
                        .weight(item.fraction)
                        .fillMaxSize()
                        .background(item.category.toColor()),
                )
            }
        }

        // Unallocated remainder
        val remainder = 1f - totalFraction
        if (remainder > 0.005f) {
            Box(
                modifier = Modifier
                    .weight(remainder)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            )
        }
    }
}

// ── Envelope Edit Card ────────────────────────────────────────────────────────

@Composable
private fun EnvelopeEditCard(
    item     : EnvelopeEditItemUi,
    onAction : (EnvelopesEditUiAction) -> Unit,
    modifier : Modifier = Modifier,
) {
    val accentColor = item.category.toColor()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(20.dp),
    ) {
        // ── Header row: icon · name/subtitle · amount/percentage ──────────
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = item.name.first().toString(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text  = item.subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text  = item.formattedAmount,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text  = item.formattedPercentage,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = accentColor,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Slider ────────────────────────────────────────────────────────
        Slider(
            value         = item.fraction,
            onValueChange = { onAction(EnvelopesEditUiAction.OnSliderChanged(item.id, it)) },
            modifier      = Modifier.fillMaxWidth(),
            colors        = SliderDefaults.colors(
                thumbColor            = MaterialTheme.colorScheme.primary,
                activeTrackColor      = MaterialTheme.colorScheme.primary,
                inactiveTrackColor    = MaterialTheme.colorScheme.surfaceContainerHighest,
                activeTickColor       = Color.Transparent,
                inactiveTickColor     = Color.Transparent,
            ),
        )

        Spacer(Modifier.height(8.dp))

        // ── Controls: − · AJUSTER · + ─────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // − button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick  = { onAction(EnvelopesEditUiAction.OnDecrement(item.id)) },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint               = MaterialTheme.colorScheme.onSurface,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }

            // "AJUSTER" label pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = "AJUSTER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // + button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick  = { onAction(EnvelopesEditUiAction.OnIncrement(item.id)) },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint               = MaterialTheme.colorScheme.onSurface,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

// Resolves semantic color for a category — same pattern as EnvelopeRowCard.
@Composable
private fun EnvelopeCategory.toColor(): Color = when (this) {
    EnvelopeCategory.FIXED_EXPENSES -> MaterialTheme.categoryColors.fixedExpenses
    EnvelopeCategory.INVESTMENT     -> MaterialTheme.categoryColors.investment
    EnvelopeCategory.SAVINGS        -> MaterialTheme.categoryColors.savings
    EnvelopeCategory.OTHER          -> MaterialTheme.categoryColors.other
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopesEditScreenPreview() {
    FinanceOSTheme {
        EnvelopesEditScreen(
            state = EnvelopesEditUiState(
                monthLabel      = "AVRIL 2026",
                totalIncome     = "€8,400",
                allocatedLabel  = "89% Alloué",
                allocatedAmount = "€7,476 / €8,400",
                statusLabel     = "Équilibré",
                items           = listOf(
                    EnvelopeEditItemUi("fixed",      "Fixed",       "Priorité Haute", EnvelopeCategory.FIXED_EXPENSES, 0.40f, "€3,360", "40%"),
                    EnvelopeEditItemUi("investment", "Investment",  "Croissance",     EnvelopeCategory.INVESTMENT,     0.20f, "€1,680", "20%"),
                    EnvelopeEditItemUi("savings",    "Savings",     "Réserve",        EnvelopeCategory.SAVINGS,        0.15f, "€1,260", "15%"),
                    EnvelopeEditItemUi("restaurants","Restaurants", "Lifestyle",      EnvelopeCategory.OTHER,          0.08f, "€672",   "8%"),
                    EnvelopeEditItemUi("loisirs",    "Loisirs",     "Plaisir",        EnvelopeCategory.OTHER,          0.06f, "€504",   "6%"),
                ),
            ),
            onAction = {},
        )
    }
}
