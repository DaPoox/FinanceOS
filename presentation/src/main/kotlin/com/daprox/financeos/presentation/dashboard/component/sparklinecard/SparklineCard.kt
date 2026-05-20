package com.daprox.financeos.presentation.dashboard.component.sparklinecard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.finColors

/**
 * Smooth 6-month patrimoine sparkline with cubic bezier curve, gradient fill,
 * draw-on-entry animation, and animated pulse dot at the last data point.
 * Hidden when [state] is null or has fewer than 2 data points.
 */
@Composable
fun SparklineCard(
    state: SparklineCardUiState?,
    modifier: Modifier = Modifier,
) {
    if (state == null || state.data.size < 2) return

    val trendColor = when (state.trend) {
        SparklineTrendEnum.POSITIVE -> MaterialTheme.finColors.positive
        SparklineTrendEnum.NEGATIVE -> MaterialTheme.colorScheme.error
    }
    val strokeColor = when (state.trend) {
        SparklineTrendEnum.POSITIVE -> MaterialTheme.colorScheme.primary
        SparklineTrendEnum.NEGATIVE -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SparklineHeader(pctLabel = state.pctLabel, trendColor = trendColor)
            Spacer(modifier = Modifier.height(12.dp))
            SparklineCanvas(
                data = state.data,
                strokeColor = strokeColor,
                backgroundColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            SparklineMonthLabels(monthLabels = state.monthLabels, count = state.data.size)
        }
    }
}

@Composable
private fun SparklineHeader(
    pctLabel: String,
    trendColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "PATRIMOINE 6 MOIS",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = trendColor.copy(alpha = 0.12f),
        ) {
            Text(
                text = pctLabel,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                color = trendColor,
            )
        }
    }
}

@Composable
private fun SparklineCanvas(
    data: List<Double>,
    strokeColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    var animTarget by remember { mutableFloatStateOf(0f) }
    val animatedFraction by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "sparkline_draw",
    )
    LaunchedEffect(data) { animTarget = 1f }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulse_radius",
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulse_alpha",
    )

    val gradientColors = listOf(strokeColor.copy(alpha = 0.15f), Color.Transparent)

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val n = data.size
        val w = size.width
        val h = size.height

        val minVal = data.min()
        val maxVal = data.max()
        val padding = (maxVal - minVal) * 0.1f
        val lo = minVal - padding
        val hi = maxVal + padding
        val range = (hi - lo).toFloat().coerceAtLeast(1f)

        fun xAt(i: Int) = i * w / (n - 1)
        fun yAt(v: Double) = h - ((v - lo) / range) * h

        val xs = List(n) { xAt(it) }
        val ys = data.map { yAt(it).toFloat() }

        // Build cubic bezier line path
        val linePath = Path().apply {
            moveTo(xs[0], ys[0])
            for (i in 1 until n) {
                val cx = (xs[i - 1] + xs[i]) / 2f
                cubicTo(cx, ys[i - 1], cx, ys[i], xs[i], ys[i])
            }
        }

        // Build gradient fill path (close below the line)
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(xs[n - 1], h)
            lineTo(xs[0], h)
            close()
        }

        // Clip to reveal L→R based on animation fraction
        clipRect(right = w * animatedFraction) {
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = h,
                ),
            )
            drawPath(
                path = linePath,
                color = strokeColor,
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        // Pulse dot at last point — only fully visible once animation completes
        if (animatedFraction >= 0.99f) {
            val dotX = xs[n - 1]
            val dotY = ys[n - 1]
            drawCircle(
                color = strokeColor.copy(alpha = pulseAlpha),
                radius = pulseRadius.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(dotX, dotY),
            )
            drawCircle(
                color = strokeColor,
                radius = 4.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(dotX, dotY),
            )
            drawCircle(
                color = backgroundColor,
                radius = 2.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(dotX, dotY),
            )
        }
    }
}

@Composable
private fun SparklineMonthLabels(
    monthLabels: List<String>,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        monthLabels.take(count).forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Positive trend", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun SparklineCardPreviewPositive() {
    FinanceOSTheme {
        SparklineCard(
            state = SparklineCardUiState(
                data = listOf(54200.0, 55610.0, 56590.0, 58210.0, 58740.0, 60580.0),
                monthLabels = listOf("déc", "jan", "fév", "mar", "avr", "mai"),
                pctLabel = "+11.8%",
                trend = SparklineTrendEnum.POSITIVE,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Negative trend", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun SparklineCardPreviewNegative() {
    FinanceOSTheme {
        SparklineCard(
            state = SparklineCardUiState(
                data = listOf(60580.0, 59800.0, 59200.0, 58740.0, 58100.0, 59200.0),
                monthLabels = listOf("déc", "jan", "fév", "mar", "avr", "mai"),
                pctLabel = "-2.3%",
                trend = SparklineTrendEnum.NEGATIVE,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}
