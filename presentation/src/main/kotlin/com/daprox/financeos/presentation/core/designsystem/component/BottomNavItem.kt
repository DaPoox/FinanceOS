package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a single bottom navigation item.
 *
 * Used by [FinanceOSBottomNav] to render navigation bar items.
 *
 * @property label Accessibility label and displayed text for the nav item.
 * @property icon  Lucide ImageVector icon to display.
 */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
)
