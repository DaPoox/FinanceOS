package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Styled progress bar component with consistent clipping and theming.
 *
 * This composable enforces a shared visual style for progress indicators. It delegates
 * animation and color selection logic to the caller — the caller is responsible for
 * computing the fill color (e.g., based on budget utilization thresholds).
 *
 * The bar includes a rounded clip shape and uses the outline color for the track background.
 *
 * @param progress Animated float in the range [0, 1] representing completion percentage.
 * @param color    Fill color for the progress indicator — computed by the caller based on
 *                 domain logic (e.g., green for OK, orange for warning, red for error).
 * @param modifier Optional modifier for layout customization.
 * @param height   Bar height in dp; defaults to 8dp for cards. Pass 4dp for compact
 *                 row items or smaller indicators.
 *
 * Example:
 * ```kotlin
 * val progress = remember { Animatable(0.75f) }
 * val color = when {
 *   progress.value > 1f -> MaterialTheme.colorScheme.error
 *   progress.value > 0.8f -> MaterialTheme.finColors.warning
 *   else -> MaterialTheme.finColors.positive
 * }
 * FinProgressBar(progress = progress.value, color = color)
 * ```
 */
@Composable
fun FinProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(4.dp)),
        color = color,
        trackColor = MaterialTheme.colorScheme.outline,
    )
}
