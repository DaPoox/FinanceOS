package com.daprox.financeos.presentation.budget.component.fixessummary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Zap
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.FinProgressBar
import androidx.compose.ui.tooling.preview.Preview

/**
 * Collapsible card showing all FIXED envelopes on BudgetScreen.
 * Replaces the standard group section for the "Fixes" type.
 * Tapping "Voir le détail →" calls [onSeeDetail] to navigate to FixesScreen.
 */
@Composable
fun FixesSummaryCard(
    state: FixesSummaryUiState,
    onSeeDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val chevronAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(280),
        label = "fixes_chevron",
    )
    val rawProgress = if (state.totalAllocated > 0) state.totalSpent / state.totalAllocated else 0.0
    val barColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column {
            // Clickable header — toggles expanded state
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "FIXES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${state.totalAllocated.toLong().frenchAmount()} €",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMono),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Lucide.ChevronDown,
                    contentDescription = if (expanded) "Replier" else "Déplier",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(chevronAngle),
                )
            }

            // Progress bar always visible below the header
            FinProgressBar(
                progress = rawProgress.coerceIn(0.0, 1.0).toFloat(),
                color = barColor,
                height = 3.dp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // Expandable charge list + detail link
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(280, easing = FastOutSlowInEasing)) +
                    fadeIn(tween(280, easing = FastOutSlowInEasing)),
                exit = shrinkVertically(tween(280, easing = FastOutSlowInEasing)) +
                    fadeOut(tween(280, easing = FastOutSlowInEasing)),
            ) {
                Column {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    state.charges.forEachIndexed { index, charge ->
                        FixesMiniRow(charge = charge)
                        if (index < state.charges.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "Voir le détail →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onSeeDetail)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun FixesMiniRow(
    charge: FixesSummaryChargeUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = charge.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp),
            )
        }
        Text(
            text = charge.name,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                text = "Fixe",
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${charge.allocated.toLong().frenchAmount()} €",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = GeistMono),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "Fixes — replié", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewCollapsed() {
    FinanceOSTheme {
        FixesSummaryCard(
            state = FixesSummaryUiState(
                totalAllocated = 1_350.0,
                totalSpent = 1_350.0,
                charges = listOf(
                    FixesSummaryChargeUiState("loyer", "Loyer", Lucide.House, 900.0, 900.0),
                    FixesSummaryChargeUiState("elec", "Électricité", Lucide.Zap, 90.0, 90.0),
                    FixesSummaryChargeUiState("inet", "Internet", Lucide.House, 0.0, 360.0),
                ),
            ),
            onSeeDetail = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
