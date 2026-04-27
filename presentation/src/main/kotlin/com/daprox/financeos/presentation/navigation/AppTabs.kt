package com.daprox.financeos.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector
import com.daprox.financeos.presentation.core.designsystem.component.BottomNavItem

// App-level bottom-nav destinations.
// Lives in :presentation so the icon library is available.
// Items will be finalised with the designer — placeholders for now.
enum class AppTab(
    val label: String,
    val icon: ImageVector,
) {
    WEALTH("WEALTH", Icons.Default.Home),
    ENVELOPES("ENVELOPES", Icons.Default.Wallet),
    GROWTH("GROWTH", Icons.Default.TrendingUp),
    VAULT("VAULT", Icons.Default.Lock),
    ;

    companion object {
        fun toBottomNavItems(): List<BottomNavItem> =
            entries.map { BottomNavItem(it.label, it.icon) }
    }
}
