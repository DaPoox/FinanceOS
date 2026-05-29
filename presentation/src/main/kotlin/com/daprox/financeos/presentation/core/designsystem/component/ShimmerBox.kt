package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Generic animated shimmer placeholder component.
 *
 * Renders a pulsing box with alpha fading between 0.05 and 0.15 to simulate
 * loading state. Use this component to build skeleton screens for content that
 * is being fetched or processed.
 *
 * The shimmer animation runs indefinitely and respects compose lifecycle bounds.
 *
 * @param modifier Optional modifier for layout customization (size, padding, etc.).
 * @param shape    Optional shape for the shimmer box; defaults to RoundedCornerShape(12.dp).
 *
 * Example:
 * ```kotlin
 * Column {
 *   ShimmerBox(modifier = Modifier.size(width = 200.dp, height = 20.dp))
 *   Spacer(modifier = Modifier.height(8.dp))
 *   ShimmerBox(modifier = Modifier.fillMaxWidth().height(16.dp))
 * }
 * ```
 *
 * @see ErrorStateView for displaying error states instead of shimmer.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer_alpha",
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)),
    )
}
