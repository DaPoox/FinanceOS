package com.daprox.financeos.presentation.dashboard.component.insightcard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.finColors
import java.text.NumberFormat
import java.util.Locale


/**
 * Contextual alert card with a colored left accent border, icon box, and inline
 * highlighted amount. Positioned below NetWorthHeroCard on the Home screen.
 * Renders nothing when [state] is null.
 */
@Composable
fun InsightCard(
    state: InsightCardUiState?,
    modifier: Modifier = Modifier,
) {
    if (state == null) return

    val typeColor = when (state.type) {
        InsightType.WARNING -> MaterialTheme.finColors.warning
        InsightType.ERROR -> MaterialTheme.colorScheme.error
        InsightType.SUCCESS -> MaterialTheme.finColors.positive
    }
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                // Background fill
                drawRect(color = surfaceColor)
                // 1dp outline border on all sides
                drawRoundRect(
                    color = outlineColor,
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
                // 3dp left accent — overrides the left border (matches CSS borderLeft behavior)
                drawRect(
                    color = typeColor,
                    size = Size(width = 3.dp.toPx(), height = size.height),
                )
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            InsightIconBox(type = state.type, color = typeColor)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val titleText = buildAnnotatedString {
                    append(state.title)
                    state.highlightAmount?.let { amount ->
                        withStyle(
                            SpanStyle(
                                fontFamily = GeistMono,
                                fontWeight = FontWeight.SemiBold,
                                color = typeColor,
                                fontSize = 14.sp,
                            ),
                        ) {
                            append(insightFormatAmount(amount))
                        }
                    }
                }
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = state.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InsightIconBox(
    type: InsightType,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        when (type) {
            InsightType.WARNING, InsightType.ERROR -> WarningIcon(color = color)
            InsightType.SUCCESS -> Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// SVG reference: screen-home.jsx — triangle M12 3l9 16H3Z + exclamation M12 10v4 M12 16.5v.01
@Composable
private fun WarningIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val sx = size.width / 24f
        val sy = size.height / 24f
        val strokeW = 1.9f * ((sx + sy) / 2f)

        val triangle = Path().apply {
            moveTo(12 * sx, 3 * sy)
            lineTo(21 * sx, 19 * sy)
            lineTo(3 * sx, 19 * sy)
            close()
        }
        drawPath(
            path = triangle,
            color = color,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        // Exclamation body
        drawLine(
            color = color,
            start = Offset(12 * sx, 10 * sy),
            end = Offset(12 * sx, 14 * sy),
            strokeWidth = 2f * ((sx + sy) / 2f),
            cap = StrokeCap.Round,
        )
        // Exclamation dot
        drawLine(
            color = color,
            start = Offset(12 * sx, 16.5f * sy),
            end = Offset(12 * sx, 16.51f * sy),
            strokeWidth = 2f * ((sx + sy) / 2f),
            cap = StrokeCap.Round,
        )
    }
}

private fun insightFormatAmount(value: Double): String =
    NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 0
    }.format(value.toLong()) + " €"

@Preview(name = "WARNING", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun InsightCardPreviewWarning() {
    FinanceOSTheme {
        InsightCard(
            state = InsightCardUiState(
                type = InsightType.WARNING,
                title = "Courses à 87% du budget",
                subtitle = "Il te reste 54 € pour 12 jours.",
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "ERROR", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun InsightCardPreviewError() {
    FinanceOSTheme {
        InsightCard(
            state = InsightCardUiState(
                type = InsightType.ERROR,
                title = "Restos dépassé de ",
                subtitle = "5 sorties ce mois. Tu en es à 112% du budget.",
                highlightAmount = 26.0,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "SUCCESS", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun InsightCardPreviewSuccess() {
    FinanceOSTheme {
        InsightCard(
            state = InsightCardUiState(
                type = InsightType.SUCCESS,
                title = "Taux d'épargne à 30% ce mois",
                subtitle = "Objectif atteint. Meilleur résultat depuis mars.",
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}
