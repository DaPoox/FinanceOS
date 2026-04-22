package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.dashboard.model.CategoryUi

// ─────────────────────────────────────────────────────────────────────────────
// BudgetDonutSection
// ─────────────────────────────────────────────────────────────────────────────

/**
 * FinanceOS-specific wrapper around [DonutChart].
 *
 * Responsibility split:
 * - [DonutChart]            → portable chart primitive. No product knowledge.
 * - [BudgetDonutSection]    → maps [CategoryUi] to [DonutSegment], injects theme
 *                             colors, builds the center label, renders the pip grid.
 *
 * Future extensions (selection, time-frame switch, amount/percentage toggle)
 * are wired here by updating [selectedCategoryIndex] and [showAmounts] —
 * the chart itself requires no changes.
 *
 * @param categories           Budget categories to display.
 * @param selectedCategoryIndex Index of the selected category (null = none).
 * @param showAmounts          True → show formatted amounts; false → percentages.
 * @param onCategoryClick      Invoked with the tapped category index.
 */
@Composable
fun BudgetDonutSection(
    categories            : List<CategoryUi>,
    totalFormattedAmount  : String,
    modifier              : Modifier = Modifier,
    selectedCategoryIndex : Int? = null,
    showAmounts           : Boolean = true,
    onCategoryClick       : ((Int) -> Unit)? = null,
    // Tapping "Envelopes →" navigates to the Envelopes screen. Null = link hidden.
    onViewAllClick        : (() -> Unit)? = null,
) {
    val segments = categories.map { cat ->
        DonutSegment(
            fraction = cat.fraction,
            color    = cat.color,
            label    = cat.label,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Section header — "BUDGET" label + optional "Envelopes →" deep-link.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text  = "BUDGET",
                style = TextStyle(
                    fontWeight    = FontWeight.Bold,
                    fontSize      = 11.sp,
                    letterSpacing = 1.2.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (onViewAllClick != null) {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onViewAllClick,
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text  = "Envelopes",
                        style = TextStyle(
                            fontWeight    = FontWeight.SemiBold,
                            fontSize      = 11.sp,
                            letterSpacing = 0.3.sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(12.dp),
                    )
                }
            }
        }
        // ── Donut chart ───────────────────────────────────────────────────────
        DonutChart(
            segments             = segments,
            modifier             = Modifier.fillMaxWidth(fraction = 0.65f),
            strokeWidth          = 28.dp,
            trackColor           = MaterialTheme.colorScheme.surfaceContainerHigh,
            selectedSegmentIndex = selectedCategoryIndex,
            onSegmentClick       = onCategoryClick,
            centerContent        = {
                DonutCenterLabel(
                    categories           = categories,
                    totalFormattedAmount = totalFormattedAmount,
                    selectedIndex        = selectedCategoryIndex,
                    showAmounts          = showAmounts,
                )
            },
        )

        Spacer(Modifier.height(24.dp))

        // ── Category pip grid ─────────────────────────────────────────────────
        CategoryPipGrid(categories = categories)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Center label
// ─────────────────────────────────────────────────────────────────────────────

// Shows the selected category's amount/percentage, or the total when nothing
// is selected. Driven entirely by the caller's state — no local state here.
@Composable
private fun DonutCenterLabel(
    categories: List<CategoryUi>,
    totalFormattedAmount: String,
    selectedIndex: Int?,
    showAmounts: Boolean,
) {
    val selected = selectedIndex?.let { categories.getOrNull(it) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (selected != null) {
            // Selected category — name + amount or percentage
            Text(
                text = selected.label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.8.sp),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (showAmounts) selected.formattedAmount else selected.formattedPercentage,
                color = selected.color,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            )
        } else {
            // No selection — show total spend
            Text(
                text = "TOTAL",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 10.sp, letterSpacing = 1.sp),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = totalFormattedAmount,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category pip grid
// ─────────────────────────────────────────────────────────────────────────────

// Two-column grid of colour pips + category labels.
// Built with plain Column/Row — no LazyGrid — since category count is small and fixed.
@Composable
private fun CategoryPipGrid(categories: List<CategoryUi>) {
    val rows = categories.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                row.forEach { category ->
                    CategoryPip(
                        category = category,
                        modifier = Modifier.weight(1f),
                    )
                }
                // Pad odd rows so the single item stays left-aligned.
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// Single pip entry: coloured dot + label + percentage.
@Composable
private fun CategoryPip(
    category: CategoryUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(category.color),
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = category.label,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
            )
            Text(
                text = category.formattedPercentage,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun BudgetDonutSectionPreview() {
    FinanceOSTheme {
        BudgetDonutSection(
            categories           = previewCategories(),
            totalFormattedAmount = "3 000 €",
            selectedCategoryIndex = 0,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun BudgetDonutSectionNoSelectionPreview() {
    FinanceOSTheme {
        BudgetDonutSection(
            categories           = previewCategories(),
            totalFormattedAmount = "3 000 €",
        )
    }
}

private fun previewCategories() = listOf(
    CategoryUi("Logement",   "1 200 €", "40 %", 0.40f, androidx.compose.ui.graphics.Color(0xFF6EE591)),
    CategoryUi("Alimentation", "480 €",  "25 %", 0.25f, androidx.compose.ui.graphics.Color(0xFF93C5FD)),
    CategoryUi("Transport",   "240 €",  "20 %", 0.20f, androidx.compose.ui.graphics.Color(0xFFC4B5FD)),
    CategoryUi("Loisirs",     "180 €",  "15 %", 0.15f, androidx.compose.ui.graphics.Color(0xFFF87171)),
)
