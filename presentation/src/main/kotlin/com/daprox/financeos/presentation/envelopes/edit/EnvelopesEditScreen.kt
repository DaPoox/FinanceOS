package com.daprox.financeos.presentation.envelopes.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
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
    viewModel : EnvelopesEditViewModel = koinViewModel(),
    onClose   : () -> Unit = {},
    onConfirm : () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EnvelopesEditScreen(
        state     = state,
        onAction  = viewModel::onAction,
        onClose   = onClose,
        onConfirm = {
            viewModel.onAction(EnvelopesEditUiAction.OnConfirm)
            onConfirm()
        },
    )
}

@Composable
fun EnvelopesEditScreen(
    state     : EnvelopesEditUiState,
    onAction  : (EnvelopesEditUiAction) -> Unit,
    onClose   : () -> Unit = {},
    onConfirm : () -> Unit = {},
) {
    Scaffold(
        topBar = {
            RebalanceTopBar(
                isOverAllocated = state.isOverAllocated,
                onClose         = onClose,
                onConfirm       = onConfirm,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Allocation Visualizer ─────────────────────────────────────────
            AllocationVisualizer(state = state)

            // ── Over-allocation warning — red, blocks confirm ─────────────────
            AnimatedVisibility(
                visible = state.isOverAllocated,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically(),
            ) {
                WarningCard(overflowEurosLabel = state.overflowEurosLabel)
            }

            // ── Under-allocation nudge — primary, confirm still enabled ───────
            AnimatedVisibility(
                visible = state.hasUnallocated,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically(),
            ) {
                SurplusCard(unallocatedEurosLabel = state.unallocatedEurosLabel)
            }

            // ── Envelope list header ──────────────────────────────────────────
            EnvelopeListHeader(isOverAllocated = state.isOverAllocated)

            // ── Envelope cards — unchanged from original ──────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.items.forEach { item ->
                    EnvelopeEditCard(item = item, onAction = onAction)
                }
            }

            // ── Add Allocation ────────────────────────────────────────────────
            AddAllocationButton()
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

// ✕ discard · "REBALANCE ENVELOPES" · ✓ confirm (greyed when over-allocated).
@Composable
private fun RebalanceTopBar(
    isOverAllocated : Boolean,
    onClose         : () -> Unit,
    onConfirm       : () -> Unit,
    modifier        : Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Discard",
                tint               = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text      = "REBALANCE ENVELOPES",
            modifier  = Modifier.weight(1f),
            style     = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = 2.sp,
            ),
            color     = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        IconButton(onClick = onConfirm, enabled = !isOverAllocated) {
            Icon(
                imageVector        = Icons.Default.Check,
                contentDescription = "Confirm",
                tint               = if (isOverAllocated)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                else
                    MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ── Allocation Visualizer ─────────────────────────────────────────────────────

// Standalone section: "TOTAL ALLOCATION" + big %, status italic text, LIMIT on right, segmented bar.
@Composable
private fun AllocationVisualizer(
    state    : EnvelopesEditUiState,
    modifier : Modifier = Modifier,
) {
    val accentColor = if (state.isOverAllocated) MaterialTheme.colorScheme.tertiary
                      else MaterialTheme.colorScheme.primary

    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Bottom,
        ) {
            // Left: label + big % + italic status text
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text  = "TOTAL ALLOCATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 3.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    verticalAlignment      = Alignment.Bottom,
                    horizontalArrangement  = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text  = state.allocatedLabel.substringBefore('%') + "%",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp,
                        ),
                        color = accentColor,
                    )
                    Text(
                        text     = if (state.isOverAllocated) "Over Budget" else state.statusLabel,
                        style    = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontStyle  = FontStyle.Italic,
                        ),
                        color    = accentColor.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }

            // Right: LIMIT label + income amount
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text  = "LIMIT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 3.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text  = state.totalIncome,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        // Segmented bar — 16dp tall, red border when over-allocated.
        AllocationBar(items = state.items, isOverAllocated = state.isOverAllocated)
    }
}

// Proportional segmented bar. Red border signals over-allocation.
@Composable
private fun AllocationBar(
    items           : List<EnvelopeEditItemUi>,
    isOverAllocated : Boolean = false,
    modifier        : Modifier = Modifier,
) {
    val errorColor    = MaterialTheme.colorScheme.error
    val trackColor    = MaterialTheme.colorScheme.surfaceContainerHighest
    val totalFraction = items.sumOf { it.fraction.toDouble() }.toFloat().coerceAtMost(1f)
    val remainder     = 1f - totalFraction

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .then(
                if (isOverAllocated)
                    Modifier.border(1.5.dp, errorColor, RoundedCornerShape(50))
                else Modifier
            ),
    ) {
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
        if (remainder > 0.005f) {
            Box(modifier = Modifier.weight(remainder).fillMaxSize().background(trackColor))
        }
    }
}

// ── Warning Card ──────────────────────────────────────────────────────────────

// Full red card shown only when total > 100%.
@Composable
private fun WarningCard(
    overflowEurosLabel : String,
    modifier           : Modifier = Modifier,
) {
    val errorColor = MaterialTheme.colorScheme.error

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(errorColor.copy(alpha = 0.08f))
            .border(1.dp, errorColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(errorColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Warning,
                contentDescription = null,
                tint               = errorColor,
                modifier           = Modifier.size(20.dp),
            )
        }

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text  = "Budget dépassé de $overflowEurosLabel",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = errorColor,
            )
            Text(
                text  = "Réduisez d'autres enveloppes pour équilibrer votre plan financier mensuel.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            )
        }
    }
}

// ── Surplus Card ──────────────────────────────────────────────────────────────

// Friendly nudge shown when total allocation < 100% — confirm remains enabled.
@Composable
private fun SurplusCard(
    unallocatedEurosLabel : String,
    modifier              : Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(primaryColor.copy(alpha = 0.08f))
            .border(1.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Add,
                contentDescription = null,
                tint               = primaryColor,
                modifier           = Modifier.size(20.dp),
            )
        }

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text  = "$unallocatedEurosLabel à distribuer",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryColor,
            )
            Text(
                text  = "Assignez ce montant à vos enveloppes pour optimiser votre budget.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            )
        }
    }
}

// ── Envelope List Header ──────────────────────────────────────────────────────

@Composable
private fun EnvelopeListHeader(
    isOverAllocated : Boolean,
    modifier        : Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = "ACTIVE ENVELOPES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = 3.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AnimatedVisibility(visible = isOverAllocated, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text  = "DÉPASSEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                    ),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

// ── Envelope Edit Card — unchanged from original ──────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
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

        // ── Slider — circular thumb with category-color glow + thin track ─
        val inactiveTrack = MaterialTheme.colorScheme.surfaceContainerHighest
        Slider(
            value         = item.fraction,
            onValueChange = { onAction(EnvelopesEditUiAction.OnSliderChanged(item.id, it)) },
            modifier      = Modifier.fillMaxWidth(),
            thumb         = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .shadow(
                            elevation    = 8.dp,
                            shape        = CircleShape,
                            clip         = false,
                            ambientColor = accentColor.copy(alpha = 0.35f),
                            spotColor    = accentColor.copy(alpha = 0.55f),
                        )
                        .clip(CircleShape)
                        .background(Color.White),
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState       = sliderState,
                    modifier          = Modifier.height(4.dp),
                    colors            = SliderDefaults.colors(
                        activeTrackColor   = accentColor,
                        inactiveTrackColor = inactiveTrack,
                        activeTickColor    = Color.Transparent,
                        inactiveTickColor  = Color.Transparent,
                    ),
                    thumbTrackGapSize = 4.dp,
                    drawStopIndicator = null,
                )
            },
        )

        Spacer(Modifier.height(8.dp))

        // ── Controls: − · AJUSTER · + ─────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
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

// ── Add Allocation Button ─────────────────────────────────────────────────────

@Composable
private fun AddAllocationButton(modifier: Modifier = Modifier) {
    val borderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f))
            .drawBehind {
                drawRoundRect(
                    color        = borderColor,
                    style        = Stroke(
                        width      = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
            }
            .clickable { /* wired when domain layer is ready */ }
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector        = Icons.Default.AddCircle,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(16.dp),
            )
            Text(
                text  = "ADD ALLOCATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Category color resolution ─────────────────────────────────────────────────

@Composable
private fun EnvelopeCategory.toColor(): Color = when (this) {
    EnvelopeCategory.FIXED_EXPENSES -> MaterialTheme.categoryColors.fixedExpenses
    EnvelopeCategory.INVESTMENT     -> MaterialTheme.categoryColors.investment
    EnvelopeCategory.SAVINGS        -> MaterialTheme.categoryColors.savings
    EnvelopeCategory.OTHER          -> MaterialTheme.categoryColors.other
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopesEditScreenPreview() {
    FinanceOSTheme {
        EnvelopesEditScreen(
            state = EnvelopesEditUiState(
                monthLabel         = "AVRIL 2026",
                totalIncome        = "€8,400",
                allocatedLabel     = "89% Alloué",
                allocatedAmount    = "€7,476 / €8,400",
                statusLabel        = "Équilibré",
                isOverAllocated    = false,
                overflowEurosLabel = "",
                items              = listOf(
                    EnvelopeEditItemUi("fixed",       "Fixed",       "Priorité Haute", EnvelopeCategory.FIXED_EXPENSES, 0.40f, "€3,360", "40%"),
                    EnvelopeEditItemUi("investment",  "Investment",  "Croissance",     EnvelopeCategory.INVESTMENT,     0.20f, "€1,680", "20%"),
                    EnvelopeEditItemUi("savings",     "Savings",     "Réserve",        EnvelopeCategory.SAVINGS,        0.15f, "€1,260", "15%"),
                    EnvelopeEditItemUi("restaurants", "Restaurants", "Lifestyle",      EnvelopeCategory.OTHER,          0.08f, "€672",   "8%"),
                    EnvelopeEditItemUi("loisirs",     "Loisirs",     "Plaisir",        EnvelopeCategory.OTHER,          0.06f, "€504",   "6%"),
                ),
            ),
            onAction = {},
        )
    }
}
