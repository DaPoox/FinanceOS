package com.daprox.financeos.presentation.dashboard.component.budgetmonthcard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.finColors

/**
 * Clickable budget summary card on the Home screen. Shows month remaining,
 * spent vs allocated, and a color-coded progress bar. Tap navigates to BudgetScreen.
 */
@Composable
fun BudgetMonthCard(
    state: BudgetMonthCardUiState,
    onTap: () -> Unit,
    onAllocateTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = "BUDGET ${state.monthLabel.uppercase()}",
            rightText = if (state.isAllocated) "${state.income.toLong().frenchAmount()} € de revenu" else null,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onTap),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        ) {
            if (!state.isAllocated) {
                UnallocatedContent(onAllocateTap = onAllocateTap)
            } else {
                AllocatedContent(state = state)
            }
        }
    }
}

@Composable
private fun AllocatedContent(state: BudgetMonthCardUiState) {
    val remaining = state.monthAllocated - state.monthSpent
    val rawProgress = if (state.monthAllocated > 0) state.monthSpent / state.monthAllocated else 0.0
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "budget_progress",
    )

    val barColor = when {
        rawProgress > 1.0 -> MaterialTheme.colorScheme.error
        rawProgress >= 0.8 -> MaterialTheme.finColors.warning
        else -> MaterialTheme.colorScheme.primary
    }
    val remainingColor = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Column(modifier = Modifier.padding(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text(
                    text = "Restant à dépenser",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = remaining.toLong().frenchAmount(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 28.sp,
                        ),
                        color = remainingColor,
                    )
                    Text(
                        text = " €",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = GeistMono,
                            fontSize = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Dépensé",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${state.monthSpent.toLong().frenchAmount()} / ${state.monthAllocated.toLong().frenchAmount()} €",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = GeistMono,
                        fontSize = 14.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun UnallocatedContent(onAllocateTap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Mois non alloué",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Allouer le mois →",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = onAllocateTap),
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    rightText: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        rightText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


@Preview(name = "Normal", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetMonthCardPreviewNormal() {
    FinanceOSTheme {
        BudgetMonthCard(
            state = BudgetMonthCardUiState(
                monthLabel = "Mai",
                income = 4200.0,
                monthSpent = 2374.0,
                monthAllocated = 2720.0,
                isAllocated = true,
            ),
            onTap = {},
            onAllocateTap = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Warning (≥80%)", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetMonthCardPreviewWarning() {
    FinanceOSTheme {
        BudgetMonthCard(
            state = BudgetMonthCardUiState(
                monthLabel = "Mai",
                income = 4200.0,
                monthSpent = 2500.0,
                monthAllocated = 2720.0,
                isAllocated = true,
            ),
            onTap = {},
            onAllocateTap = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Dépassé", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetMonthCardPreviewOver() {
    FinanceOSTheme {
        BudgetMonthCard(
            state = BudgetMonthCardUiState(
                monthLabel = "Mai",
                income = 4200.0,
                monthSpent = 2900.0,
                monthAllocated = 2720.0,
                isAllocated = true,
            ),
            onTap = {},
            onAllocateTap = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Non alloué", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BudgetMonthCardPreviewUnallocated() {
    FinanceOSTheme {
        BudgetMonthCard(
            state = BudgetMonthCardUiState(
                monthLabel = "Mai",
                isAllocated = false,
            ),
            onTap = {},
            onAllocateTap = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
