package com.daprox.financeos.presentation.history

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Lucide
import com.daprox.financeos.presentation.core.designsystem.component.EmptyStateView
import com.daprox.financeos.presentation.core.designsystem.component.ErrorStateView
import com.daprox.financeos.presentation.core.designsystem.component.ShimmerBox
import com.daprox.financeos.presentation.core.designsystem.finColors
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToLong

@Composable
fun HistoryScreenRoot(
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HistoryScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onAction: (HistoryUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseModifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)

    when {
        state.isLoading -> HistoryScreenSkeleton(modifier = baseModifier)
        state.isError -> Column(modifier = baseModifier) {
            ErrorStateView(onRetry = { onAction(HistoryUiAction.OnRetry) })
        }
        state.isEmpty -> Column(modifier = baseModifier) {
            EmptyStateView(
                icon = Lucide.CalendarDays,
                title = "Pas encore d'historique",
                subtitle = "Ton historique apparaîtra ici après ton premier mois complet.",
            )
        }
        else -> Column(
            modifier = baseModifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            HistoryHeader()
            Spacer(modifier = Modifier.height(14.dp))
            AnnualSummaryCard(
                totalIncome = state.totalIncome,
                totalContrib = state.totalContrib,
                avgSavingRate = state.avgSavingRate,
                barData = state.barData,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            MonthList(
                months = state.months,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

@Composable
private fun HistoryScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(160.dp))
        repeat(3) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(56.dp))
        }
    }
}

@Composable
private fun HistoryHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "Historique",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "12 derniers mois",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AnnualSummaryCard(
    totalIncome: Double,
    totalContrib: Double,
    avgSavingRate: Int,
    barData: List<Double>,
    modifier: Modifier = Modifier,
) {
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val positive = MaterialTheme.finColors.positive

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(surface, surfaceVariant),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
                shape = RoundedCornerShape(20.dp),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(18.dp),
    ) {
        Column {
            Text(
                text = "BILAN ANNUEL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatCell(
                    label = "REVENU TOTAL",
                    amount = totalIncome,
                    color = MaterialTheme.colorScheme.onSurface,
                    sign = false,
                    modifier = Modifier.weight(1f),
                )
                StatCell(
                    label = "PATRIMOINE AJOUTÉ",
                    amount = totalContrib,
                    color = positive,
                    sign = true,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = "Taux d'épargne moyen",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$avgSavingRate%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = GeistMono,
                            fontWeight = FontWeight.Medium,
                            fontSize = 26.sp,
                        ),
                        color = primary,
                    )
                }
                BarsMini(data = barData, color = primary)
            }
        }
    }
}

@Composable
private fun StatCell(
    label: String,
    amount: Double,
    color: Color,
    sign: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                letterSpacing = 0.8.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${if (sign) "+" else ""}${amount.roundToLong().frenchAmount()}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                ),
                color = color,
            )
            Text(
                text = "€",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = GeistMono,
                    fontSize = 12.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
    }
}

@Composable
private fun BarsMini(
    data: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val max = data.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val count = data.size.coerceAtLeast(1)

    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        data.forEachIndexed { index, value ->
            val fraction = (value / max).toFloat().coerceIn(0.04f, 1f)
            val alpha = 0.4f + (index.toFloat() / count) * 0.6f
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxSize(fraction)
                    .background(
                        color = color.copy(alpha = alpha),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        }
    }
}

@Composable
private fun MonthList(
    months: List<MonthRowUiState>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "TOUS LES MOIS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            months.forEach { month ->
                MonthRow(month = month)
            }
        }
    }
}

@Composable
private fun MonthRow(
    month: MonthRowUiState,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val positive = MaterialTheme.finColors.positive
    val warning = MaterialTheme.finColors.warning
    val error = MaterialTheme.colorScheme.error

    val barColor = when (month.status) {
        MonthStatusEnum.BEST -> primary
        MonthStatusEnum.GOOD -> positive
        MonthStatusEnum.MID -> warning
        MonthStatusEnum.HARD -> error
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(36.dp)
                .background(barColor.copy(alpha = 0.9f), RoundedCornerShape(4.dp)),
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = month.monthLabel.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (month.status == MonthStatusEnum.BEST) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = primary.copy(alpha = 0.12f),
                    ) {
                        Text(
                            text = "RECORD",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = primary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InlineMonthStat(
                    amount = month.income.roundToLong().frenchAmount(),
                    label = " revenu",
                )
                InlineMonthStat(
                    amount = month.spent.roundToLong().frenchAmount(),
                    label = " dépensé",
                )
            }
        }

        Text(
            text = "+${month.contrib.roundToLong().frenchAmount()} €",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            ),
            color = positive,
        )
    }
}

@Composable
private fun InlineMonthStat(
    amount: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = amount,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = GeistMono,
                fontSize = 11.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "HistoryScreen", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewHistoryScreen() {
    FinanceOSTheme {
        HistoryScreen(
            state = HistoryUiState(
                isLoading = false,
                totalIncome = 33150.0,
                totalContrib = 10220.0,
                avgSavingRate = 30,
                barData = listOf(1180.0, 1390.0, 520.0, 1280.0, 1620.0, 980.0, 1410.0, 1840.0),
                months = listOf(
                    MonthRowUiState("mai 26", 4200.0, 2960.0, 1840.0, MonthStatusEnum.BEST),
                    MonthRowUiState("avr 26", 4200.0, 3210.0, 1410.0, MonthStatusEnum.GOOD),
                    MonthRowUiState("mar 26", 3950.0, 3380.0, 980.0, MonthStatusEnum.MID),
                    MonthRowUiState("fév 26", 4400.0, 3210.0, 1620.0, MonthStatusEnum.GOOD),
                    MonthRowUiState("jan 26", 4200.0, 3340.0, 1280.0, MonthStatusEnum.GOOD),
                    MonthRowUiState("déc 25", 4800.0, 4520.0, 520.0, MonthStatusEnum.HARD),
                    MonthRowUiState("nov 25", 4200.0, 3120.0, 1390.0, MonthStatusEnum.GOOD),
                    MonthRowUiState("oct 25", 4200.0, 3290.0, 1180.0, MonthStatusEnum.GOOD),
                ),
            ),
            onAction = {},
        )
    }
}
