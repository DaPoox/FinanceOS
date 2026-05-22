package com.daprox.financeos.presentation.budget.component.budgetglobalcard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.FinProgressBar
import com.daprox.financeos.presentation.core.designsystem.finColors

/**
 * Global budget summary card at the top of the Budget screen.
 * Shows income vs. total spent, a progress bar, and allocated/spent totals.
 */
@Composable
fun BudgetGlobalCard(
    state: BudgetGlobalCardUiState,
    modifier: Modifier = Modifier,
) {
    val remaining = state.income - state.totalSpent
    val rawProgress = if (state.income > 0) state.totalSpent / state.income else 0.0
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "global_budget_progress",
    )
    val barColor = when {
        rawProgress > 1.0 -> MaterialTheme.colorScheme.error
        rawProgress >= 0.8 -> MaterialTheme.finColors.warning
        else -> MaterialTheme.colorScheme.primary
    }
    val remainingColor = if (remaining < 0) MaterialTheme.colorScheme.error
                         else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                // Left — remaining
                Column {
                    Text(
                        text = "Revenu — Dépenses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = remaining.frenchAmount(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 28.sp,
                            ),
                            color = remainingColor,
                        )
                        Text(
                            text = " € restant",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = GeistMono,
                                fontSize = 16.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Right — income
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${state.income.frenchAmount()} €",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = GeistMono,
                            fontSize = 14.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "REVENU",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FinProgressBar(progress = animatedProgress, color = barColor)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BottomLabel(amount = state.totalSpent, suffix = "dépensé")
                BottomLabel(amount = state.totalAllocated, suffix = "alloué")
            }
        }
    }
}

@Composable
private fun BottomLabel(amount: Double, suffix: String) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = onSurface, fontFamily = GeistMono, fontSize = 11.sp)) {
                append("${amount.frenchAmount()} €")
            }
            withStyle(SpanStyle(color = onSurfaceVariant, fontSize = 11.sp)) {
                append("  $suffix")
            }
        },
        style = MaterialTheme.typography.bodySmall,
    )
}

@Preview(name = "Normal", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetGlobalCardPreviewNormal() {
    FinanceOSTheme {
        BudgetGlobalCard(
            state = BudgetGlobalCardUiState(
                income = 4200.0,
                totalSpent = 1800.0,
                totalAllocated = 2720.0,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Warning (≥80%)", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetGlobalCardPreviewWarning() {
    FinanceOSTheme {
        BudgetGlobalCard(
            state = BudgetGlobalCardUiState(
                income = 4200.0,
                totalSpent = 3400.0,
                totalAllocated = 3800.0,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Dépassé", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetGlobalCardPreviewOver() {
    FinanceOSTheme {
        BudgetGlobalCard(
            state = BudgetGlobalCardUiState(
                income = 4200.0,
                totalSpent = 4500.0,
                totalAllocated = 4200.0,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Vide", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetGlobalCardPreviewEmpty() {
    FinanceOSTheme {
        BudgetGlobalCard(
            state = BudgetGlobalCardUiState(),
            modifier = Modifier.padding(20.dp),
        )
    }
}
