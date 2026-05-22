package com.daprox.financeos.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.presentation.core.designsystem.component.BottomNavItem

enum class AppTab(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Accueil", Lucide.House),
    BUDGET("Budget", Lucide.CalendarDays),
    PATRIMOINE("Patrimoine", Lucide.Wallet),
    HISTORIQUE("Historique", Lucide.Clock),
    ;

    companion object {
        fun toBottomNavItems(): List<BottomNavItem> =
            entries.map { BottomNavItem(it.label, it.icon) }
    }
}
