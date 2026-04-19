package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi

// Pill-shaped progress bar with a label + amount row above.
// isGradient=false → solid primary fill (month, green).
// isGradient=true  → secondary→secondaryContainer horizontal gradient (week, blue).
@Composable
fun GradientProgressBar(
    bar: ProgressBarUi,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh

    // Week bar: dark-blue start → light-blue end (left to right)
    // Month bar: solid primary green
    val fillBrush: Brush = if (bar.isGradient) {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.secondaryContainer, // Blue700 — dark start
                MaterialTheme.colorScheme.secondary,          // Blue300 — light end
            )
        )
    } else {
        SolidColor(MaterialTheme.colorScheme.primary)         // Green400
    }

    Column(modifier = modifier) {
        // Label row — left: bar label (e.g. "MOIS"), right: formatted amount
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = bar.label,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                ),
            )
            Text(
                text = bar.formattedAmount,
                color = if (bar.isGradient) {
                    MaterialTheme.colorScheme.secondary        // Blue300 for week amount
                } else {
                    MaterialTheme.colorScheme.primary          // Green400 for month amount
                },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                ),
            )
        }

        Spacer(Modifier.height(8.dp))

        // Pill progress bar drawn on Canvas — avoids recomposition-per-frame animations
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) {
            val radius = CornerRadius(size.height / 2f)

            // Track (full-width background)
            drawRoundRect(
                color = trackColor,
                cornerRadius = radius,
            )

            // Fill (clipped to progress ratio)
            if (bar.progress > 0f) {
                drawRoundRect(
                    brush = fillBrush,
                    size = Size(
                        width = (size.width * bar.progress.coerceIn(0f, 1f)),
                        height = size.height,
                    ),
                    cornerRadius = radius,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun GradientProgressBarPreview() {
    FinanceOSTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientProgressBar(
                bar = ProgressBarUi(
                    label = "MOIS",
                    formattedAmount = "2 400 €",
                    progress = 0.62f,
                    isGradient = false,
                )
            )
            GradientProgressBar(
                bar = ProgressBarUi(
                    label = "SEMAINE",
                    formattedAmount = "480 €",
                    progress = 0.45f,
                    isGradient = true,
                )
            )
        }
    }
}
