package com.daprox.financeos.presentation.patrimoine

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.FinProgressBar
import com.daprox.financeos.presentation.core.designsystem.finColors
import org.koin.androidx.compose.koinViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

@Composable
fun PatrimoineScreenRoot(
    viewModel: PatrimoineViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PatrimoineUiEvent.NavigateToAddAccount -> Unit
        }
    }

    PatrimoineScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun PatrimoineScreen(
    state: PatrimoineUiState,
    onAction: (PatrimoineUiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        PatrimoineHeader(
            netWorth = state.netWorth,
            deltaLabel = state.deltaLabel,
            deltaPct = state.deltaPct,
        )
        Spacer(modifier = Modifier.height(22.dp))
        DonutCard(
            savings = state.savings,
            investment = state.investment,
            liquid = state.liquid,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(modifier = Modifier.height(14.dp))
        SparklineCard(
            data = state.sparklineData,
            selectedRange = state.selectedRange,
            onRangeSelected = { onAction(PatrimoineUiAction.OnRangeSelected(it)) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(modifier = Modifier.height(22.dp))
        AccountsSection(
            accounts = state.accounts,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}

@Composable
private fun PatrimoineHeader(
    netWorth: Double,
    deltaLabel: String,
    deltaPct: String,
    modifier: Modifier = Modifier,
) {
    var animTarget by remember { mutableFloatStateOf(0f) }
    val animatedValue by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "networth_countup",
    )
    LaunchedEffect(netWorth) { animTarget = netWorth.toFloat() }

    val displayAmount = (animatedValue.toDouble() * 1.0).roundToLong()
    val positive = MaterialTheme.finColors.positive

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "Patrimoine",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = displayAmount.frenchAmount(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 40.sp,
                    letterSpacing = (-1.2).sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "€",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = GeistMono,
                    fontSize = 22.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeltaPill(text = deltaLabel, color = positive)
            DeltaPill(text = deltaPct, color = positive)
        }
    }
}

@Composable
private fun DeltaPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
            color = color,
        )
    }
}

@Composable
private fun DonutCard(
    savings: Double,
    investment: Double,
    liquid: Double,
    modifier: Modifier = Modifier,
) {
    val total = savings + investment + liquid
    val savingsColor = MaterialTheme.finColors.savings
    val investColor = MaterialTheme.finColors.investment
    val liquidColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Column {
            Text(
                text = "RÉPARTITION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                DonutChart(
                    segments = listOf(
                        DonutSegment(savings, savingsColor),
                        DonutSegment(investment, investColor),
                        DonutSegment(liquid, liquidColor),
                    ),
                    total = total,
                    centerLabel = "${(total / 1000).roundToLong()}k",
                    modifier = Modifier.size(150.dp),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DonutLegendRow("Épargne", savingsColor, savings, total)
                    DonutLegendRow("Investissement", investColor, investment, total)
                    DonutLegendRow("Liquidités", liquidColor, liquid, total)
                }
            }
        }
    }
}

private data class DonutSegment(val value: Double, val color: Color)

@Composable
private fun DonutChart(
    segments: List<DonutSegment>,
    total: Double,
    centerLabel: String,
    modifier: Modifier = Modifier,
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val trackColor = Color.White.copy(alpha = 0.04f)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val gapAngle = (3.dp.toPx() / ((size.minDimension - strokeWidth) / 2f)) * (180f / PI.toFloat())
            val inset = strokeWidth / 2f

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth),
            )

            var startAngle = -90f
            val totalVal = total.toFloat().coerceAtLeast(1f)
            segments.forEach { seg ->
                val sweep = (seg.value.toFloat() / totalVal) * 360f - gapAngle
                if (sweep > 0f) {
                    drawArc(
                        color = seg.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth),
                    )
                    startAngle += sweep + gapAngle
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                ),
                color = onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "TOTAL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DonutLegendRow(
    label: String,
    color: Color,
    value: Double,
    total: Double,
    modifier: Modifier = Modifier,
) {
    val pct = if (total > 0) ((value / total) * 100).roundToLong() else 0L

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$pct%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${value.roundToLong().frenchAmount()} €",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Composable
private fun SparklineCard(
    data: List<Double>,
    selectedRange: SparklineRangeEnum,
    onRangeSelected: (SparklineRangeEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ÉVOLUTION 12 MOIS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                RangeToggle(
                    selected = selectedRange,
                    onSelect = onRangeSelected,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PatrimoineSparkline(
                data = data,
                strokeColor = primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
        }
    }
}

@Composable
private fun RangeToggle(
    selected: SparklineRangeEnum,
    onSelect: (SparklineRangeEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ranges = listOf(SparklineRangeEnum.M6 to "6M", SparklineRangeEnum.M12 to "12M", SparklineRangeEnum.Y3 to "3A")

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        ranges.forEach { (range, label) ->
            val active = selected == range
            Box(
                modifier = Modifier
                    .background(
                        color = if (active) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        shape = RoundedCornerShape(100.dp),
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .clickable { onSelect(range) }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                    ),
                    color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PatrimoineSparkline(
    data: List<Double>,
    strokeColor: Color,
    modifier: Modifier = Modifier,
) {
    var animTarget by remember { mutableFloatStateOf(0f) }
    val animatedFraction by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "patrimoine_sparkline",
    )
    LaunchedEffect(data) {
        animTarget = 0f
        animTarget = 1f
    }

    val gradientColors = listOf(strokeColor.copy(alpha = 0.15f), Color.Transparent)

    Canvas(modifier = modifier) {
        val n = data.size
        if (n < 2) return@Canvas
        val w = size.width
        val h = size.height

        val minVal = data.min()
        val maxVal = data.max()
        val pad = (maxVal - minVal) * 0.1
        val lo = minVal - pad
        val hi = maxVal + pad
        val range = (hi - lo).toFloat().coerceAtLeast(1f)

        val xs = List(n) { i -> i * w / (n - 1) }
        val ys = data.map { v -> (h - ((v - lo) / range) * h).toFloat() }

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

@Composable
private fun AccountsSection(
    accounts: List<AccountUiState>,
    modifier: Modifier = Modifier,
) {
    val grouped = accounts.groupBy { it.type }
    val order = listOf(
        AccountTypeEnum.COURANT to "Compte courant",
        AccountTypeEnum.EPARGNE to "Épargne",
        AccountTypeEnum.INVESTISSEMENT to "Investissement",
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "COMPTES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "+ Ajouter",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        order.forEach { (type, label) ->
            val group = grouped[type] ?: return@forEach
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                group.forEach { account ->
                    AccountRow(account = account)
                }
            }
        }
    }
}

@Composable
private fun AccountRow(
    account: AccountUiState,
    modifier: Modifier = Modifier,
) {
    val hasCap = account.cap != null
    val capProgress = if (hasCap) (account.balance / account.cap!!).toFloat().coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(account.color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = account.name.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = GeistMono,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                        ),
                        color = account.color,
                    )
                }
                Column {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (hasCap) {
                        Text(
                            text = "Cap ${account.cap!!.roundToLong().frenchAmount()} €",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Text(
                text = "${account.balance.roundToLong().frenchAmount()} €",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (hasCap) {
            Spacer(modifier = Modifier.height(10.dp))
            FinProgressBar(
                progress = capProgress,
                color = account.color,
                height = 4.dp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${(capProgress * 100).roundToLong()}% de capacité",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${(account.cap!! - account.balance).roundToLong().frenchAmount()} € disponibles",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = GeistMono,
                        fontSize = 10.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "PatrimoineScreen", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewPatrimoineScreen() {
    FinanceOSTheme {
        PatrimoineScreen(
            state = PatrimoineUiState(
                netWorth = 60520.0,
                deltaLabel = "+12 380 € · 6 mois",
                deltaPct = "+11.8%",
                savings = 26600.0,
                investment = 29100.0,
                liquid = 4820.0,
                sparklineData = listOf(48200.0, 49100.0, 50320.0, 51100.0, 51980.0, 52900.0, 54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0),
                sparklineMonths = listOf("jun", "jul", "aoû", "sep", "oct", "nov", "déc", "jan", "fév", "mar", "avr", "mai"),
                selectedRange = SparklineRangeEnum.M12,
                accounts = listOf(
                    AccountUiState("Boursorama", AccountTypeEnum.COURANT, 4820.0, null, Color(0xFFE8EEF5)),
                    AccountUiState("Livret A", AccountTypeEnum.EPARGNE, 18400.0, 22950.0, Color(0xFF7EB8F7)),
                    AccountUiState("LDDS", AccountTypeEnum.EPARGNE, 8200.0, 12000.0, Color(0xFF7EB8F7)),
                    AccountUiState("PEA", AccountTypeEnum.INVESTISSEMENT, 22680.0, null, Color(0xFFA78BFA)),
                    AccountUiState("CTO Trade Rep.", AccountTypeEnum.INVESTISSEMENT, 6420.0, null, Color(0xFFA78BFA)),
                ),
            ),
            onAction = {},
        )
    }
}
