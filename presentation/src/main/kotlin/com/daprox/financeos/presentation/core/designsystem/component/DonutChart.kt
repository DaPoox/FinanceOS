package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// Public API
// ─────────────────────────────────────────────────────────────────────────────

/**
 * One segment of the donut chart.
 *
 * @param fraction  Share of the full circle (0f–1f). All segment fractions
 *                  should sum to ≤ 1f; any remainder is rendered as empty track.
 * @param color     Fill color for this segment.
 * @param label     Semantic label — used for accessibility and as an animation key.
 */
data class DonutSegment(
    val fraction: Float,
    val color: Color,
    val label: String = "",
)

/**
 * Sensible defaults for [DonutChart]. Override any value at the call site.
 */
object DonutChartDefaults {
    /** Thickness of the ring. */
    val StrokeWidth: Dp = 26.dp

    /** Empty space between adjacent segments, in degrees. */
    const val GapAngleDegrees: Float = 3f

    /** How far the selected segment lifts outward along its bisector. */
    val SelectedLift: Dp = 10.dp

    /**
     * Background ring color. Defaults to a dark neutral that reads well on
     * dark surfaces — override with a theme-appropriate token at the call site.
     */
    val TrackColor: Color = Color(0xFF222930)

    /**
     * Animation spec applied to every sweep and lift transition.
     * Medium-bounce spring gives a lively, financial-app feel.
     */
    val AnimationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// DonutChart
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Self-contained, portable donut chart for Jetpack Compose.
 *
 * **Portability** — this file has no imports from any product package.
 * Copy it into any Compose project and it will compile as-is.
 *
 * **Features**
 * - Animated draw-in on first composition.
 * - Every segment sweep animates independently on data changes (time-frame
 *   switches, category updates) with no interruption.
 * - Optional tap-to-select: the selected segment lifts outward along its
 *   bisector, animated.
 * - `centerContent` slot: render anything in the hole — label, icon, summary.
 * - Zero chart-library dependency.
 *
 * **Selection / display-mode / time-frame**
 * These features are intentionally driven from the outside via [selectedSegmentIndex]
 * and [centerContent]. The chart is purely visual; all state lives in the caller's
 * ViewModel. Future additions (e.g. display-mode toggle) require no changes here.
 *
 * @param segments             Data to render — fraction, color, semantic label.
 * @param modifier             Applied to the outer square bounding box.
 * @param strokeWidth          Ring thickness.
 * @param gapAngleDegrees      Gap between adjacent arcs, in degrees.
 * @param selectedSegmentIndex Index of the currently highlighted segment, or null.
 * @param selectedLift         Distance the selected arc shifts outward.
 * @param trackColor           Color of the empty ring rendered behind all segments.
 * @param onSegmentClick       Invoked with the index of the tapped segment.
 *                             Pass null to disable tap interaction entirely.
 * @param animationSpec        Spec for all sweep and lift animations.
 * @param centerContent        Composable rendered in the centre hole. Receives
 *                             [BoxScope] so alignment helpers are available.
 */
@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = DonutChartDefaults.StrokeWidth,
    gapAngleDegrees: Float = DonutChartDefaults.GapAngleDegrees,
    selectedSegmentIndex: Int? = null,
    selectedLift: Dp = DonutChartDefaults.SelectedLift,
    trackColor: Color = DonutChartDefaults.TrackColor,
    onSegmentClick: ((Int) -> Unit)? = null,
    animationSpec: AnimationSpec<Float> = DonutChartDefaults.AnimationSpec,
    centerContent: @Composable BoxScope.() -> Unit = {},
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    val liftPx = with(density) { selectedLift.toPx() }

    // ── Fraction animations ───────────────────────────────────────────────────
    // One Animatable per segment, recreated only when the segment COUNT changes.
    // Starts at 0f to produce a draw-in animation on first composition.
    val fractionAnims = remember(segments.size) {
        List(segments.size) { Animatable(0f) }
    }

    // Animate to the current fraction targets whenever data or spec changes.
    LaunchedEffect(segments, animationSpec) {
        fractionAnims.forEachIndexed { i, anim ->
            launch {
                anim.animateTo(
                    targetValue = segments.getOrNull(i)?.fraction ?: 0f,
                    animationSpec = animationSpec,
                )
            }
        }
    }

    // ── Lift animations ───────────────────────────────────────────────────────
    val liftAnims = remember(segments.size) {
        List(segments.size) { Animatable(0f) }
    }

    LaunchedEffect(selectedSegmentIndex, animationSpec) {
        liftAnims.forEachIndexed { i, anim ->
            launch {
                anim.animateTo(
                    targetValue = if (i == selectedSegmentIndex) 1f else 0f,
                    animationSpec = animationSpec,
                )
            }
        }
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier.aspectRatio(1f),
    ) {
        // Compute ring geometry so the drawn ring — including stroke half-width
        // and the maximum lift offset — never clips outside the bounding box.
        val sizePx = with(density) { minOf(maxWidth, maxHeight).toPx() }

        // Centre of stroke path — pulled inward to leave room for lift + half-stroke.
        val ringRadius = (sizePx / 2f) - (strokeWidthPx / 2f) - liftPx

        // Edges of the ring band — used for hit-testing.
        val outerEdge = ringRadius + strokeWidthPx / 2f + liftPx
        val innerEdge = ringRadius - strokeWidthPx / 2f

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(segments, onSegmentClick) {
                    if (onSegmentClick == null) return@pointerInput
                    detectTapGestures { tapOffset ->
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val dx = tapOffset.x - cx
                        val dy = tapOffset.y - cy
                        val dist = sqrt(dx * dx + dy * dy)

                        // Reject taps outside the ring band (generous tolerance for lifted arcs).
                        if (dist < innerEdge - 8f || dist > outerEdge + 8f) return@detectTapGestures

                        // Convert to an angle in [0°, 360°) measured clockwise from 12 o'clock.
                        var tapAngle = (atan2(dy, dx) * (180f / Math.PI.toFloat())) + 90f
                        if (tapAngle < 0f) tapAngle += 360f

                        // Walk the arc layout and find the hit segment.
                        // Segments fill the full 360° — gap is only visual on the selected arc.
                        // Use the full sweep for hit testing (more forgiving UX).
                        var cursor = 0f
                        segments.forEachIndexed { index, _ ->
                            val sweep = fractionAnims[index].value * 360f
                            val arcEnd = cursor + sweep
                            if (tapAngle in cursor..arcEnd) {
                                onSegmentClick(index)
                                return@detectTapGestures
                            }
                            cursor += sweep
                        }
                    }
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val ovalTL = Offset(center.x - ringRadius, center.y - ringRadius)
                val ovalSize = Size(ringRadius * 2f, ringRadius * 2f)
                val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)

                // ── Track (full-circle background) ────────────────────────────
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = ovalTL,
                    size = ovalSize,
                    style = stroke,
                )

                // ── Segments ──────────────────────────────────────────────────
                // Segments fill the full 360° with no universal gap.
                // The selected segment gets a gap carved from its own arc (half on each side)
                // driven by its lift animation — so gap and lift animate together.
                var startAngle = -90f  // 12 o'clock

                segments.forEachIndexed { index, segment ->
                    val liftProgress = liftAnims[index].value
                    val sweep = fractionAnims[index].value * 360f

                    // Gap opens only on the selected arc, proportional to lift progress.
                    val halfGap = liftProgress * (gapAngleDegrees / 2f)
                    val arcStart = startAngle + halfGap
                    val arcSweep = (sweep - halfGap * 2f).coerceAtLeast(0f)

                    // Bisector direction for the outward lift translate.
                    val bisectorRad = Math.toRadians((arcStart + arcSweep / 2f).toDouble()).toFloat()
                    val lift = liftProgress * liftPx

                    withTransform(
                        transformBlock = {
                            translate(
                                left = lift * cos(bisectorRad),
                                top = lift * sin(bisectorRad),
                            )
                        }
                    ) {
                        drawArc(
                            color = segment.color,
                            startAngle = arcStart,
                            sweepAngle = arcSweep,
                            useCenter = false,
                            topLeft = ovalTL,
                            size = ovalSize,
                            style = stroke,
                        )
                    }

                    startAngle += sweep  // advance by full sweep — gap is visual only
                }
            }

            // ── Centre hole content ───────────────────────────────────────────
            // Constrained to the inner ring so content never overlaps the arcs.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(fraction = 1f - (strokeWidthPx * 2.5f / sizePx)),
            ) {
                centerContent()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun DonutChartPreview() {
    DonutChart(
        segments = listOf(
            DonutSegment(fraction = 0.40f, color = Color(0xFF6EE591), label = "Housing"),
            DonutSegment(fraction = 0.25f, color = Color(0xFF93C5FD), label = "Food"),
            DonutSegment(fraction = 0.20f, color = Color(0xFFC4B5FD), label = "Transport"),
            DonutSegment(fraction = 0.15f, color = Color(0xFFF87171), label = "Other"),
        ),
        selectedSegmentIndex = 0,
        modifier = Modifier.fillMaxSize(0.6f),
    )
}
