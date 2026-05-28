package com.daprox.financeos.presentation.envelopedetail

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Receipt
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
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EnvelopeDetailScreenRoot(
    id: String,
    viewModel: EnvelopeDetailViewModel = koinViewModel(parameters = { parametersOf(id) }),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EnvelopeDetailUiEvent.NavigateBack -> onNavigateBack()
        }
    }

    EnvelopeDetailScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun EnvelopeDetailScreen(
    state: EnvelopeDetailUiState,
    onAction: (EnvelopeDetailUiAction) -> Unit,
) {
    val isPerm = state.type == EnvelopeTypeEnum.PERMANENT
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        val listPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + navBarPadding.calculateBottomPadding() + 8.dp,
        )

        when {
            state.isLoading -> EnvelopeDetailScreenSkeleton(contentPadding = listPadding)
            state.isError -> ErrorStateView(
                onRetry = { onAction(EnvelopeDetailUiAction.OnRetry) },
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            )
            else -> LazyColumn(contentPadding = listPadding) {
                item {
                    DetailScreenHeader(
                        name = state.name,
                        typeLabel = state.typeLabel,
                        onBack = { onAction(EnvelopeDetailUiAction.OnBackClick) },
                    )
                }

                item {
                    HeroStatusCard(
                        state = state,
                        isPerm = isPerm,
                        onModifierAllocation = { onAction(EnvelopeDetailUiAction.OnModifierAllocationClick) },
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
                    )
                }

                item {
                    TransactionSection(
                        transactions = state.transactions,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp),
                    )
                }

                if (isPerm && state.monthlyHistory.size >= 2) {
                    item {
                        HistorySection(
                            history = state.monthlyHistory,
                            monthsAgo = state.monthsAgo,
                            accumulated = state.accumulated,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp),
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun EnvelopeDetailScreenSkeleton(contentPadding: PaddingValues) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 16.dp)) }
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(140.dp).padding(horizontal = 20.dp)) }
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 20.dp)) }
    }
}

@Composable
private fun DetailScreenHeader(
    name: String,
    typeLabel: String,
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
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HeroStatusCard(
    state: EnvelopeDetailUiState,
    isPerm: Boolean,
    onModifierAllocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (statusText, statusColor) = when (state.status) {
        EnvelopeStatusEnum.OVER -> "Dépassé" to MaterialTheme.colorScheme.error
        EnvelopeStatusEnum.WARNING -> "Attention" to MaterialTheme.finColors.warning
        EnvelopeStatusEnum.FIXED -> "Fixe" to MaterialTheme.colorScheme.onSurfaceVariant
        EnvelopeStatusEnum.OK -> "OK" to MaterialTheme.finColors.positive
    }
    val isOver = state.status == EnvelopeStatusEnum.OVER
    val mainAmount = if (isPerm) state.accumulated else state.spent
    val rawProgress = if (state.allocated > 0) state.spent / state.allocated else 0.0
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "envelope_detail_progress",
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isPerm) "ACCUMULÉ" else "DÉPENSÉ",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = mainAmount.toLong().frenchAmount(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = GeistMono,
                                fontWeight = FontWeight.Medium,
                                fontSize = 36.sp,
                            ),
                            color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = " €",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = GeistMono,
                                fontSize = 18.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPerm) "+${state.allocated.toLong().frenchAmount()} €/mois"
                        else "sur ${state.allocated.toLong().frenchAmount()} € alloués",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = GeistMono,
                            fontSize = 13.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = statusColor.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                        color = statusColor,
                    )
                }
            }

            if (!isPerm) {
                Spacer(modifier = Modifier.height(18.dp))
                FinProgressBar(progress = animatedProgress, color = statusColor, height = 10.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${(rawProgress * 100).toInt()}% utilisé",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = if (isOver) "+${(state.spent - state.allocated).toLong().frenchAmount()} € de dépassement"
                        else "${(state.allocated - state.spent).toLong().frenchAmount()} € restants",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = GeistMono,
                            fontSize = 11.sp,
                        ),
                        color = if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                onClick = onModifierAllocation,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Lucide.Pencil,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Modifier l'allocation",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionSection(
    transactions: List<TransactionUiState>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "CE MOIS",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${transactions.size} transactions",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMono),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            EmptyStateView(
                icon = Lucide.Receipt,
                title = "Aucune dépense ce mois",
                subtitle = "Appuie sur + pour enregistrer ta première dépense.",
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                transactions.forEachIndexed { index, tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = tx.note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tx.dateLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = "−${tx.amount.toLong().frenchAmount()} €",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = GeistMono,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (index < transactions.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySection(
    history: List<Double>,
    monthsAgo: Int,
    accumulated: Double,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "HISTORIQUE MENSUEL",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                EnvelopeSparkline(
                    data = history,
                    strokeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Démarré il y a $monthsAgo mois",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "+${accumulated.toLong().frenchAmount()} €",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMono),
                        color = MaterialTheme.finColors.positive,
                    )
                }
            }
        }
    }
}

@Composable
private fun EnvelopeSparkline(
    data: List<Double>,
    strokeColor: Color,
    modifier: Modifier = Modifier,
) {
    var animTarget by remember { mutableFloatStateOf(0f) }
    val animatedFraction by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "envelope_sparkline",
    )
    LaunchedEffect(data) { animTarget = 1f }

    val gradientColors = listOf(strokeColor.copy(alpha = 0.15f), Color.Transparent)

    Canvas(modifier = modifier) {
        val n = data.size
        if (n < 2) return@Canvas
        val w = size.width
        val h = size.height

        val minVal = data.min()
        val maxVal = data.max()
        val pad = (maxVal - minVal) * 0.1f
        val lo = minVal - pad
        val hi = maxVal + pad
        val range = (hi - lo).toFloat().coerceAtLeast(1f)

        fun xAt(i: Int) = i * w / (n - 1)
        fun yAt(v: Double) = h - ((v - lo) / range) * h

        val xs = List(n) { xAt(it) }
        val ys = data.map { yAt(it).toFloat() }

        val linePath = Path().apply {
            moveTo(xs[0], ys[0])
            for (i in 1 until n) {
                val cx = (xs[i - 1] + xs[i]) / 2f
                cubicTo(cx, ys[i - 1], cx, ys[i], xs[i], ys[i])
            }
        }

        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(xs[n - 1], h)
            lineTo(xs[0], h)
            close()
        }

        clipRect(right = w * animatedFraction) {
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(colors = gradientColors, startY = 0f, endY = h),
            )
            drawPath(
                path = linePath,
                color = strokeColor,
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }
}

@Preview(name = "OK", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewOK() {
    FinanceOSTheme {
        EnvelopeDetailScreen(
            state = EnvelopeDetailUiState(
                isLoading = false,
                id = "courses",
                name = "Courses",
                typeLabel = "Variable standard",
                type = EnvelopeTypeEnum.VARIABLE,
                spent = 287.0,
                allocated = 420.0,
                status = EnvelopeStatusEnum.OK,
                transactions = listOf(
                    TransactionUiState("t1", "Lidl", "12 mai", 42.0),
                    TransactionUiState("t2", "Carrefour", "8 mai", 95.0),
                    TransactionUiState("t3", "Biocoop", "5 mai", 67.0),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Attention", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewWarning() {
    FinanceOSTheme {
        EnvelopeDetailScreen(
            state = EnvelopeDetailUiState(
                isLoading = false,
                id = "restos",
                name = "Restos",
                typeLabel = "Variable standard",
                type = EnvelopeTypeEnum.VARIABLE,
                spent = 104.0,
                allocated = 120.0,
                status = EnvelopeStatusEnum.WARNING,
                transactions = listOf(
                    TransactionUiState("t1", "Sushi House", "15 mai", 38.0),
                    TransactionUiState("t2", "Brasserie du Nord", "9 mai", 52.0),
                    TransactionUiState("t3", "McDonald's", "2 mai", 14.0),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Dépassé", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewOver() {
    FinanceOSTheme {
        EnvelopeDetailScreen(
            state = EnvelopeDetailUiState(
                isLoading = false,
                id = "restos",
                name = "Restos",
                typeLabel = "Variable standard",
                type = EnvelopeTypeEnum.VARIABLE,
                spent = 134.0,
                allocated = 120.0,
                status = EnvelopeStatusEnum.OVER,
                transactions = listOf(
                    TransactionUiState("t1", "Sushi House", "15 mai", 38.0),
                    TransactionUiState("t2", "Brasserie du Nord", "9 mai", 52.0),
                    TransactionUiState("t3", "McDonald's", "2 mai", 14.0),
                    TransactionUiState("t4", "Pizzeria Napoli", "1 mai", 30.0),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(name = "PERMANENT", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewPermanent() {
    FinanceOSTheme {
        EnvelopeDetailScreen(
            state = EnvelopeDetailUiState(
                isLoading = false,
                id = "fonds-urgence",
                name = "Fonds urgence",
                typeLabel = "Permanente • accumulée",
                type = EnvelopeTypeEnum.PERMANENT,
                spent = 0.0,
                allocated = 200.0,
                accumulated = 1_400.0,
                status = EnvelopeStatusEnum.OK,
                transactions = emptyList(),
                monthlyHistory = listOf(200.0, 400.0, 600.0, 800.0, 1000.0, 1200.0, 1400.0),
                monthsAgo = 7,
            ),
            onAction = {},
        )
    }
}
