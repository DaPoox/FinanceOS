package com.daprox.financeos.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.presentation.core.designsystem.component.BottomNavItem

// App-level bottom-nav destinations.
// Lives in :presentation so the icon library is available.
// Items will be finalised with the designer — placeholders for now.
enum class AppTab(
    val label: String,
    val icon: ImageVector,
) {
    WEALTH("WEALTH", Lucide.House),
    ENVELOPES("ENVELOPES", Lucide.Wallet),
    GROWTH("GROWTH", Lucide.TrendingUp),
    VAULT("VAULT", Lucide.Lock),
    ;

    companion object {
        fun toBottomNavItems(): List<BottomNavItem> =
            entries.map { BottomNavItem(it.label, it.icon) }
    }
}
