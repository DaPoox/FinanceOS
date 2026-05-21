package com.daprox.financeos.presentation.dashboard.component.networthhero

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono


/**
 * Hero card showing total net worth with count-up animation, monthly delta pill,
 * optional insight label, and contribution breakdown bars.
 */
@Composable
fun NetWorthHeroCard(
    state: NetWorthHeroUiState,
    modifier: Modifier = Modifier,
) {
    val hasData = state.netWorth > 0

    var animTarget by remember { mutableFloatStateOf(0f) }
    val animatedAmount by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "netWorth",
    )

    LaunchedEffect(state.netWorth) {
        animTarget = state.netWorth.toFloat()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "PATRIMOINE TOTAL",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.4.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (hasData) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = animatedAmount.toLong().frenchAmount(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            letterSpacing = (-1.2).sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "€",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = "— €",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            if (hasData) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    DeltaPill(delta = state.delta)

                    state.insightLabel?.let { insight ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = insight,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CONTRIBUTION DU MOIS",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.4.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    ContribColumn(
                        label = "Épargne",
                        value = state.contribSavings,
                        modifier = Modifier.weight(1f),
                    )
                    ContribColumn(
                        label = "Investissement",
                        value = state.contribInvest,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DeltaPill(
    delta: Double,
    modifier: Modifier = Modifier,
) {
    val isPositive = delta >= 0
    val pillColor = if (isPositive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    val prefix = if (isPositive) "+" else ""
    val formattedDelta = "$prefix${delta.toLong().frenchAmount()} €"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = pillColor.copy(alpha = 0.12f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArrowIcon(
                isUp = isPositive,
                color = pillColor,
                modifier = Modifier.size(10.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formattedDelta,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                color = pillColor,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "ce mois",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = GeistMono,
                    fontSize = 12.sp,
                ),
                color = pillColor.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun ArrowIcon(
    isUp: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            if (isUp) {
                moveTo(w / 2f, 0f)
                lineTo(w, h)
                lineTo(0f, h)
            } else {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w / 2f, h)
            }
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
private fun ContribColumn(
    label: String,
    value: Double,
    modifier: Modifier = Modifier,
) {
    val prefix = if (value >= 0) "+" else ""
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$prefix${value.toLong().frenchAmount()} €",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(name = "Normal state", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun NetWorthHeroCardPreviewNormal() {
    FinanceOSTheme {
        NetWorthHeroCard(
            state = NetWorthHeroUiState(
                netWorth = 60580.0,
                delta = 1840.0,
                insightLabel = "Meilleur mois depuis 4 mois 🔥",
                contribSavings = 600.0,
                contribInvest = 650.0,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Negative delta", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun NetWorthHeroCardPreviewNegative() {
    FinanceOSTheme {
        NetWorthHeroCard(
            state = NetWorthHeroUiState(
                netWorth = 58740.0,
                delta = -320.0,
                insightLabel = null,
                contribSavings = 400.0,
                contribInvest = 200.0,
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "Empty state", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun NetWorthHeroCardPreviewEmpty() {
    FinanceOSTheme {
        NetWorthHeroCard(
            state = NetWorthHeroUiState(),
            modifier = Modifier.padding(20.dp),
        )
    }
}
