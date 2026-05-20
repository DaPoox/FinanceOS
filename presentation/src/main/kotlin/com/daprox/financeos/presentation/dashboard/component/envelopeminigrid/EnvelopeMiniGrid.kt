package com.daprox.financeos.presentation.dashboard.component.envelopeminigrid

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Utensils
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.finColors

/**
 * 2×2 grid of the most-active envelopes for the current month.
 * Renders up to [envelopes].size items (max 4). Each card is tappable.
 */
@Composable
fun EnvelopeMiniGrid(
    envelopes: List<EnvelopeMiniUiState>,
    onEnvelopeClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        EnvelopeMiniSectionHeader(onSeeAllClick = onSeeAllClick)
        Spacer(modifier = Modifier.height(8.dp))
        val rows = envelopes.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rows.forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { item ->
                        EnvelopeMiniCard(
                            state = item,
                            onClick = { onEnvelopeClick(item.id) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EnvelopeMiniSectionHeader(
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "ENVELOPPES ACTIVES",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Tout voir →",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onSeeAllClick),
        )
    }
}

@Composable
private fun EnvelopeMiniCard(
    state: EnvelopeMiniUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (state.status) {
        EnvelopeStatusEnum.OVER -> MaterialTheme.colorScheme.error
        EnvelopeStatusEnum.WARNING -> MaterialTheme.finColors.warning
        EnvelopeStatusEnum.FIXED -> MaterialTheme.finColors.market
        EnvelopeStatusEnum.OK -> MaterialTheme.finColors.positive
    }
    val iconTint = when (state.status) {
        EnvelopeStatusEnum.OVER -> MaterialTheme.colorScheme.error
        EnvelopeStatusEnum.WARNING -> MaterialTheme.finColors.warning
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "envelope_progress_${state.id}",
    )

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = state.icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                if (state.status == EnvelopeStatusEnum.OVER || state.status == EnvelopeStatusEnum.FIXED) {
                    StatusBadge(status = state.status)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = state.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = state.spent.toLong().frenchAmount(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = GeistMono,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    ),
                    color = if (state.status == EnvelopeStatusEnum.OVER) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Text(
                    text = " / ${state.allocated.toLong().frenchAmount()} €",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = GeistMono,
                        fontSize = 11.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
            ) {
                Surface(
                    modifier = Modifier.matchParentSize(),
                    color = MaterialTheme.colorScheme.outline,
                ) {}
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = statusColor,
                ) {}
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: EnvelopeStatusEnum,
    modifier: Modifier = Modifier,
) {
    val isOver = status == EnvelopeStatusEnum.OVER
    val label = if (isOver) "↑" else "FIXE"
    val bgColor = if (isOver) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    }
    val textColor = if (isOver) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = bgColor,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (isOver) 10.sp else 9.sp,
            ),
            color = textColor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeMiniGridPreview() {
    FinanceOSTheme {
        EnvelopeMiniGrid(
            envelopes = listOf(
                EnvelopeMiniUiState(
                    id = "1",
                    name = "Courses",
                    icon = Lucide.ShoppingCart,
                    type = EnvelopeTypeEnum.VARIABLE,
                    spent = 287.0,
                    allocated = 420.0,
                    status = EnvelopeStatusEnum.OK,
                    progress = 0.68f,
                ),
                EnvelopeMiniUiState(
                    id = "2",
                    name = "Shopping",
                    icon = Lucide.Car,
                    type = EnvelopeTypeEnum.VARIABLE,
                    spent = 170.0,
                    allocated = 200.0,
                    status = EnvelopeStatusEnum.WARNING,
                    progress = 0.85f,
                ),
                EnvelopeMiniUiState(
                    id = "3",
                    name = "Restos",
                    icon = Lucide.Utensils,
                    type = EnvelopeTypeEnum.VARIABLE,
                    spent = 134.0,
                    allocated = 120.0,
                    status = EnvelopeStatusEnum.OVER,
                    progress = 1f,
                ),
                EnvelopeMiniUiState(
                    id = "4",
                    name = "Loyer",
                    icon = Lucide.House,
                    type = EnvelopeTypeEnum.FIXED,
                    spent = 900.0,
                    allocated = 900.0,
                    status = EnvelopeStatusEnum.FIXED,
                    progress = 1f,
                ),
            ),
            onEnvelopeClick = {},
            onSeeAllClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
