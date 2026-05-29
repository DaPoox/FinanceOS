package com.daprox.financeos.presentation.fixes

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Zap
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.EmptyStateView
import com.daprox.financeos.presentation.core.designsystem.component.ErrorStateView
import com.daprox.financeos.presentation.core.designsystem.component.FinProgressBar
import com.daprox.financeos.presentation.core.designsystem.component.ShimmerBox
import com.daprox.financeos.presentation.core.designsystem.finColors
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeStatusEnum
import org.koin.androidx.compose.koinViewModel

@Composable
fun FixesScreenRoot(
    viewModel: FixesViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FixesUiEvent.NavigateBack -> onNavigateBack()
        }
    }

    FixesScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun FixesScreen(
    state: FixesUiState,
    onAction: (FixesUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        val listPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + navBarPadding.calculateBottomPadding() + 8.dp,
        )

        when {
            state.isLoading -> FixesScreenSkeleton(contentPadding = listPadding)
            state.isError -> ErrorStateView(
                onRetry = { onAction(FixesUiAction.OnRetry) },
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            )
            state.charges.isEmpty() -> EmptyStateView(
                icon = Lucide.Lock,
                title = "Aucune charge fixe",
                subtitle = "Crée des enveloppes de type « Fixe » pour les voir ici.",
                modifier = Modifier.padding(innerPadding),
            )
            else -> LazyColumn(contentPadding = listPadding) {
                item {
                    FixesScreenHeader(
                        monthLabel = state.monthLabel,
                        onBack = { onAction(FixesUiAction.OnBackClick) },
                    )
                }

                item {
                    FixesTotalCard(
                        totalAllocated = state.totalAllocated,
                        totalSpent = state.totalSpent,
                        chargeCount = state.charges.size,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                item {
                    Text(
                        text = "CHARGES · ${state.charges.size}",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 8.dp),
                    )
                }

                itemsIndexed(state.charges, key = { _, c -> c.id }) { index, charge ->
                    FixesChargeRow(
                        charge = charge,
                        animationDelayMs = index * 40,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp),
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun FixesScreenSkeleton(contentPadding: PaddingValues) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 16.dp)) }
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 16.dp)) }
        repeat(3) {
            item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 16.dp)) }
        }
    }
}

@Composable
private fun FixesScreenHeader(
    monthLabel: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            onClick = onBack,
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Icon(
                imageVector = Lucide.ArrowLeft,
                contentDescription = "Retour",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp),
            )
        }
        Column {
            Text(
                text = "Charges fixes",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (monthLabel.isNotBlank()) {
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Summary card showing total allocated vs spent for all fixed charges this month. */
@Composable
private fun FixesTotalCard(
    totalAllocated: Double,
    totalSpent: Double,
    chargeCount: Int,
    modifier: Modifier = Modifier,
) {
    val rawProgress = if (totalAllocated > 0) totalSpent / totalAllocated else 0.0
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "fixes_total_progress",
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = "TOTAL ALLOUÉ",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = totalAllocated.toLong().frenchAmount(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = GeistMono,
                                fontWeight = FontWeight.Medium,
                                fontSize = 30.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
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
                        text = "$chargeCount charges",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${totalSpent.toLong().frenchAmount()} € réglées",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMono),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            FinProgressBar(
                progress = animatedProgress,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                height = 5.dp,
            )
        }
    }
}

/**
 * One fixed charge row: icon, name, status badge, spent/allocated amounts, and a thin progress bar.
 */
@Composable
private fun FixesChargeRow(
    charge: FixesChargeUiState,
    animationDelayMs: Int,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (charge.status) {
        EnvelopeStatusEnum.OVER -> MaterialTheme.colorScheme.error
        EnvelopeStatusEnum.WARNING -> MaterialTheme.finColors.warning
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusLabel = when (charge.status) {
        EnvelopeStatusEnum.OVER -> "Dépassé"
        EnvelopeStatusEnum.WARNING -> "Attention"
        else -> "Fixe"
    }
    val rawProgress = if (charge.allocated > 0) charge.spent / charge.allocated else 0.0
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 600, delayMillis = animationDelayMs, easing = FastOutSlowInEasing),
        label = "fixes_row_${charge.id}",
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = charge.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = charge.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = statusColor.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = statusColor,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${charge.spent.toLong().frenchAmount()} / ${charge.allocated.toLong().frenchAmount()} €",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = GeistMono,
                        fontSize = 11.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            FinProgressBar(
                progress = animatedProgress,
                color = statusColor,
                height = 4.dp,
            )
        }
    }
}

@Preview(name = "Fixes Screen", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewFixesScreen() {
    FinanceOSTheme {
        FixesScreen(
            state = FixesUiState(
                isLoading = false,
                monthLabel = "Mai 2026",
                totalAllocated = 1_350.0,
                totalSpent = 990.0,
                charges = listOf(
                    FixesChargeUiState("loyer", "Loyer", Lucide.House, 900.0, 900.0, EnvelopeStatusEnum.FIXED),
                    FixesChargeUiState("elec", "Électricité", Lucide.Zap, 90.0, 90.0, EnvelopeStatusEnum.FIXED),
                    FixesChargeUiState("inet", "Internet", Lucide.House, 0.0, 360.0, EnvelopeStatusEnum.FIXED),
                ),
            ),
            onAction = {},
        )
    }
}
