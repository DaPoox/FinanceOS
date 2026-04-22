package com.daprox.financeos.presentation.envelopes.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.categoryColors
import com.daprox.financeos.presentation.envelopes.model.EnvelopeCategory
import com.daprox.financeos.presentation.envelopes.model.EnvelopeUi

// Full allocation row card — icon circle, name/subtitle, amount, % pill, chevron.
// When [isExpanded] is true, child rows animate into view below the parent.
@Composable
fun EnvelopeRowCard(
    envelope    : EnvelopeUi,
    isExpanded  : Boolean,
    onClick     : () -> Unit,
    modifier    : Modifier = Modifier,
) {
    val accentColor = envelope.category.toColor()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick,
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Colored icon circle — accent fill at 15% alpha, accent icon tint.
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                // Placeholder initial until SVG category icons are bundled.
                Text(
                    text  = envelope.name.first().toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = accentColor,
                )
            }

            Spacer(Modifier.width(12.dp))

            // Name + subtitle stacked, grows to fill available space.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = envelope.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = envelope.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.width(8.dp))

            // Amount + percentage pill stacked on the right.
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text  = envelope.formattedAmount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                // Pill badge showing the percentage of total budget.
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text  = envelope.formattedPercentage,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 0.3.sp,
                        ),
                        color = accentColor,
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Chevron rotates 90° when expanded — smooth animation via animateFloatAsState
            // would require a parent state; the rotation here is instant for simplicity.
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier
                    .size(20.dp)
                    .rotate(if (isExpanded) 90f else 0f),
            )
        }

        // Child rows — slide in/out with a smooth height animation.
        AnimatedVisibility(
            visible = isExpanded && envelope.children.isNotEmpty(),
            enter   = expandVertically(animationSpec = tween(220)),
            exit    = shrinkVertically(animationSpec = tween(180)),
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, top = 4.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f)),
            ) {
                envelope.children.forEachIndexed { index, child ->
                    ChildEnvelopeRow(child = child, accentColor = accentColor)
                    if (index < envelope.children.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }
    }
}

// A simplified row used for child items inside an expanded envelope.
// No chevron, smaller text — it's a sub-entry, not a primary allocation.
@Composable
private fun ChildEnvelopeRow(
    child       : EnvelopeUi,
    accentColor : androidx.compose.ui.graphics.Color,
    modifier    : Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Small dot instead of a full icon circle for visual hierarchy.
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accentColor),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = child.name,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (child.subtitle.isNotBlank()) {
                Text(
                    text  = child.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text  = child.formattedAmount,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// Resolves the semantic color for a category using the theme-aware category colors.
// Keeps color logic out of the data layer — called only from composable context.
@Composable
private fun EnvelopeCategory.toColor() = when (this) {
    EnvelopeCategory.FIXED_EXPENSES -> MaterialTheme.categoryColors.fixedExpenses
    EnvelopeCategory.INVESTMENT     -> MaterialTheme.categoryColors.investment
    EnvelopeCategory.SAVINGS        -> MaterialTheme.categoryColors.savings
    EnvelopeCategory.OTHER          -> MaterialTheme.categoryColors.other
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopeRowCollapsedPreview() {
    FinanceOSTheme {
        EnvelopeRowCard(
            envelope   = EnvelopeUi(
                id                  = "fixed",
                name                = "Fixed Expenses",
                subtitle            = "Rent, utilities, insurance",
                formattedAmount     = "2 940,00 €",
                formattedPercentage = "35 %",
                fraction            = 0.35f,
                category            = EnvelopeCategory.FIXED_EXPENSES,
            ),
            isExpanded = false,
            onClick    = {},
            modifier   = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopeRowExpandedPreview() {
    FinanceOSTheme {
        EnvelopeRowCard(
            envelope   = EnvelopeUi(
                id                  = "fixed",
                name                = "Fixed Expenses",
                subtitle            = "Rent, utilities, insurance",
                formattedAmount     = "2 940,00 €",
                formattedPercentage = "35 %",
                fraction            = 0.35f,
                category            = EnvelopeCategory.FIXED_EXPENSES,
                children            = listOf(
                    EnvelopeUi("fixed_rent", "Rent", "Monthly lease", "1 200,00 €", "14 %", 0.14f, EnvelopeCategory.FIXED_EXPENSES),
                    EnvelopeUi("fixed_util", "Utilities", "Electricity, water", "180,00 €", "2 %", 0.02f, EnvelopeCategory.FIXED_EXPENSES),
                ),
            ),
            isExpanded = true,
            onClick    = {},
            modifier   = Modifier.padding(16.dp),
        )
    }
}
