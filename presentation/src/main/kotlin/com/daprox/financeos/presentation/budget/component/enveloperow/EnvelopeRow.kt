package com.daprox.financeos.presentation.budget.component.enveloperow

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.FinProgressBar
import com.daprox.financeos.presentation.core.designsystem.finColors
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

/**
 * Clickable envelope row for the Budget screen. Supports 6 envelope types with
 * distinct color logic, conditional badges, and a permanent-type alternate layout.
 */
@Composable
fun EnvelopeRow(
    state: EnvelopeRowUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    animationDelayMs: Int = 0,
) {
    val barColor = when {
        state.status == EnvelopeStatusEnum.OVER -> MaterialTheme.colorScheme.error
        state.status == EnvelopeStatusEnum.WARNING -> MaterialTheme.finColors.warning
        state.type == EnvelopeTypeEnum.SAVINGS -> MaterialTheme.finColors.savings
        state.type == EnvelopeTypeEnum.INVESTMENT -> MaterialTheme.finColors.investment
        state.type == EnvelopeTypeEnum.FIXED -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    val iconTint = when {
        state.type == EnvelopeTypeEnum.SAVINGS -> MaterialTheme.finColors.savings
        state.type == EnvelopeTypeEnum.INVESTMENT -> MaterialTheme.finColors.investment
        state.type == EnvelopeTypeEnum.FIXED -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 800, delayMillis = animationDelayMs, easing = FastOutSlowInEasing),
        label = "envelope_row_progress_${state.id}",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Icon box
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = state.icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Name + progress bar
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = state.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (state.type == EnvelopeTypeEnum.MONTHLY) {
                        TypePill(
                            label = "NEW",
                            textColor = MaterialTheme.colorScheme.primary,
                            bgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        )
                    }
                    if (state.type == EnvelopeTypeEnum.PERMANENT) {
                        TypePill(
                            label = "∞",
                            textColor = MaterialTheme.colorScheme.onSurface,
                            bgColor = MaterialTheme.colorScheme.surfaceVariant,
                            fontSize = 11,
                        )
                    }
                    if (state.status == EnvelopeStatusEnum.OVER) {
                        TypePill(
                            label = "DÉPASSÉ",
                            textColor = MaterialTheme.colorScheme.error,
                            bgColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        )
                    }
                }
                FinProgressBar(progress = animatedProgress, color = barColor, height = 4.dp)
            }

            // Right-side amounts
            if (state.type == EnvelopeTypeEnum.PERMANENT) {
                PermanentAmount(accumulated = state.accumulated, monthly = state.allocated)
            } else {
                NormalAmount(spent = state.spent, allocated = state.allocated, isOver = state.status == EnvelopeStatusEnum.OVER)
            }
        }
    }
}

@Composable
private fun TypePill(
    label: String,
    textColor: Color,
    bgColor: Color,
    fontSize: Int = 9,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = bgColor,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize.sp,
            ),
            color = textColor,
        )
    }
}

@Composable
private fun NormalAmount(spent: Double, allocated: Double, isOver: Boolean) {
    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = spent.frenchAmount(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                ),
                color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = " / ${allocated.frenchAmount()} €",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = GeistMono,
                    fontSize = 11.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PermanentAmount(accumulated: Double, monthly: Double) {
    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = accumulated.frenchAmount(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = " €",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = GeistMono,
                    fontSize = 11.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "+${monthly.toLong().frenchAmount()} €/mois",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "Variable OK", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewNormal() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "1", name = "Courses", icon = Lucide.ShoppingCart,
                type = EnvelopeTypeEnum.VARIABLE, spent = 287.0, allocated = 420.0,
                status = EnvelopeStatusEnum.OK, progress = 0.68f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Warning ≥85%", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewWarning() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "2", name = "Restos", icon = Lucide.ShoppingCart,
                type = EnvelopeTypeEnum.VARIABLE, spent = 170.0, allocated = 200.0,
                status = EnvelopeStatusEnum.WARNING, progress = 0.85f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Over budget + DÉPASSÉ", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewOver() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "3", name = "Shopping", icon = Lucide.ShoppingCart,
                type = EnvelopeTypeEnum.VARIABLE, spent = 134.0, allocated = 120.0,
                status = EnvelopeStatusEnum.OVER, progress = 1f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Fixed (dim)", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewFixed() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "4", name = "Loyer", icon = Lucide.House,
                type = EnvelopeTypeEnum.FIXED, spent = 900.0, allocated = 900.0,
                status = EnvelopeStatusEnum.FIXED, progress = 1f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Savings (blue)", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewSavings() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "5", name = "Épargne", icon = Lucide.Wallet,
                type = EnvelopeTypeEnum.SAVINGS, spent = 300.0, allocated = 500.0,
                status = EnvelopeStatusEnum.OK, progress = 0.6f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Monthly + NEW", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewMonthly() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "6", name = "Voyage été", icon = Lucide.ShoppingCart,
                type = EnvelopeTypeEnum.MONTHLY, spent = 80.0, allocated = 400.0,
                status = EnvelopeStatusEnum.OK, progress = 0.2f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Permanent (accumulated)", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeRowPreviewPermanent() {
    FinanceOSTheme {
        EnvelopeRow(
            state = EnvelopeRowUiState(
                id = "7", name = "Fonds urgence", icon = Lucide.TrendingUp,
                type = EnvelopeTypeEnum.PERMANENT, spent = 0.0, allocated = 200.0,
                accumulated = 1_400.0, status = EnvelopeStatusEnum.OK, progress = 0f,
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
