package com.daprox.financeos.presentation.core.designsystem

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a colored blur glow behind the composable using BlurMaskFilter.
 *
 * Unlike Modifier.shadow(), this approach reliably renders colored glows on dark backgrounds
 * and supports custom blur and offset parameters. Useful for creating depth and emphasis
 * on cards, buttons, and other prominent components.
 *
 * @param color       The color of the glow/shadow.
 * @param borderRadius Optional corner radius for the glow shape; defaults to 0dp for sharp shadows.
 * @param blurRadius  Optional blur amount in dp; defaults to 0dp for a sharp edge.
 * @param offsetX     Optional horizontal offset from the component; defaults to 0dp.
 * @param offsetY     Optional vertical offset from the component; defaults to 0dp.
 *
 * @return A new Modifier with the colored shadow effect applied.
 */
fun Modifier.coloredShadow(
    color: Color,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().also {
            it.asFrameworkPaint().apply {
                isAntiAlias = true
                this.color = android.graphics.Color.TRANSPARENT
                if (blurRadius != 0.dp) {
                    maskFilter = BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL)
                }
            }
        }
        paint.color = color
        canvas.drawRoundRect(
            left = offsetX.toPx(),
            top = offsetY.toPx(),
            right = size.width + offsetX.toPx(),
            bottom = size.height + offsetY.toPx(),
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint,
        )
    }
}
