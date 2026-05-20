package com.daprox.financeos.presentation.dashboard.component.recentmonthssection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.finColors

/**
 * Section showing the 2 most recent past months with status dot, revenue, and
 * patrimoine contribution. Each row taps to HistoryScreen.
 */
@Composable
fun RecentMonthsSection(
    months: List<RecentMonthUiState>,
    onMonthClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "MOIS RÉCENTS",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            months.forEach { month ->
                RecentMonthRow(
                    state = month,
                    onClick = { onMonthClick(month.id) },
                )
            }
        }
    }
}

@Composable
private fun RecentMonthRow(
    state: RecentMonthUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dotColor = when (state.status) {
        MonthStatusEnum.POSITIVE -> MaterialTheme.finColors.positive
        MonthStatusEnum.WARNING -> MaterialTheme.finColors.warning
        MonthStatusEnum.NEGATIVE -> MaterialTheme.colorScheme.error
    }
    val contribAmountColor = when (state.status) {
        MonthStatusEnum.NEGATIVE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.monthLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = state.revenueLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.contribAmountLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = GeistMono,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    color = contribAmountColor,
                )
                Text(
                    text = state.contribLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Positive + Warning", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun RecentMonthsSectionPreviewGood() {
    FinanceOSTheme {
        RecentMonthsSection(
            months = listOf(
                RecentMonthUiState(
                    id = "1",
                    monthLabel = "Avril 2026",
                    revenueLabel = "Revenu 3 800 €",
                    contribAmountLabel = "+1 840 €",
                    contribLabel = "ajouté au patrimoine",
                    status = MonthStatusEnum.POSITIVE,
                ),
                RecentMonthUiState(
                    id = "2",
                    monthLabel = "Mars 2026",
                    revenueLabel = "Revenu 4 200 €",
                    contribAmountLabel = "+640 €",
                    contribLabel = "ajouté au patrimoine",
                    status = MonthStatusEnum.WARNING,
                ),
            ),
            onMonthClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Warning + Negative", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun RecentMonthsSectionPreviewBad() {
    FinanceOSTheme {
        RecentMonthsSection(
            months = listOf(
                RecentMonthUiState(
                    id = "1",
                    monthLabel = "Avril 2026",
                    revenueLabel = "Revenu 3 800 €",
                    contribAmountLabel = "+320 €",
                    contribLabel = "ajouté au patrimoine",
                    status = MonthStatusEnum.WARNING,
                ),
                RecentMonthUiState(
                    id = "2",
                    monthLabel = "Mars 2026",
                    revenueLabel = "Revenu 4 200 €",
                    contribAmountLabel = "-320 €",
                    contribLabel = "retiré du patrimoine",
                    status = MonthStatusEnum.NEGATIVE,
                ),
            ),
            onMonthClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
