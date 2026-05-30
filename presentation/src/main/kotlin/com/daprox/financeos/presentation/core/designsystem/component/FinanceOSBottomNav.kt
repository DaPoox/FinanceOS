package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme

/**
 * Custom bottom navigation bar component.
 *
 * Renders a row of navigation items with smooth color transitions and optional selection
 * state. Each item displays an icon and label, with the selected item highlighted in
 * primary color and an optional semi-transparent background.
 *
 * The component includes a top divider and styled container background.
 *
 * @param items            List of [BottomNavItem]s to display.
 * @param selectedIndex    The index of the currently selected item (0-based).
 * @param onItemSelected   Callback invoked with the index when an item is tapped.
 * @param modifier         Optional modifier for layout customization.
 *
 * Example:
 * ```kotlin
 * val items = listOf(
 *   BottomNavItem("Home", Lucide.House),
 *   BottomNavItem("Budget", Lucide.Calendar),
 *   BottomNavItem("Wealth", Lucide.Wallet),
 * )
 * var selectedIndex by remember { mutableIntStateOf(0) }
 *
 * FinanceOSBottomNav(
 *   items = items,
 *   selectedIndex = selectedIndex,
 *   onItemSelected = { selectedIndex = it }
 * )
 * ```
 *
 * @see BottomNavItem
 */
@Composable
fun FinanceOSBottomNav(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(
                    top = 8.dp,
                    bottom = 12.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                    start = 12.dp,
                    end = 12.dp,
                ),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                NavItem(
                    item = item,
                    isSelected = index == selectedIndex,
                    onSelected = { onItemSelected(index) },
                )
            }
        }
    }
}

/**
 * Individual navigation item within the bottom nav bar.
 *
 * Renders an icon inside a pill-shaped container with optional background color,
 * and a label below. The item animates to show selection state with color and
 * font weight changes.
 *
 * @param item       The [BottomNavItem] to render.
 * @param isSelected Whether this item is currently selected.
 * @param onSelected Callback invoked when the user taps this item.
 */
@Composable
private fun NavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected,
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else androidx.compose.ui.graphics.Color.Transparent
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = item.label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            ),
        )
    }
}

/**
 * Preview of the bottom navigation bar in light mode with sample navigation items.
 */
@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun BottomNavPreview() {
    FinanceOSTheme {
        FinanceOSBottomNav(
            items = listOf(
                BottomNavItem("Accueil", Lucide.House),
                BottomNavItem("Budget", Lucide.CalendarDays),
                BottomNavItem("Patrimoine", Lucide.Wallet),
                BottomNavItem("Historique", Lucide.Clock),
            ),
            selectedIndex = 1,
            onItemSelected = {},
        )
    }
}
