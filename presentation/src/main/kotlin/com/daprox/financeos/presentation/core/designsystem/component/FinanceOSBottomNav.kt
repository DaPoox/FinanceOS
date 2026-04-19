package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
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
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme

// Top-level navigation destinations.
enum class DashboardTab(
    val label: String,
    // Placeholder icons — replace with bundled SVG assets once added to the project.
    val icon: ImageVector,
) {
    WEALTH("WEALTH", Icons.Default.Home),
    ASSETS("ASSETS", Icons.Default.AccountBalance),
    GROWTH("GROWTH", Icons.Default.TrendingUp),
    VAULT("VAULT", Icons.Default.Lock),
}

// Bottom navigation bar with a frosted-glass shell and a green pill active indicator.
// True backdrop blur is not natively supported in Compose — the glass effect is
// approximated via surface color at 80% alpha.
@Composable
fun FinanceOSBottomNav(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DashboardTab.entries.forEach { tab ->
            NavItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onSelected = { onTabSelected(tab) },
            )
        }
    }
}

// Single tab item. Active state renders a green pill; inactive state is icon + label only.
@Composable
private fun NavItem(
    tab: DashboardTab,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    // Active: dark content on the green pill. Inactive: muted onSurfaceVariant.
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .then(
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected,
            )
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = contentColor,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tab.label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun BottomNavWealthPreview() {
    FinanceOSTheme {
        FinanceOSBottomNav(selectedTab = DashboardTab.WEALTH, onTabSelected = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun BottomNavAssetsPreview() {
    FinanceOSTheme {
        FinanceOSBottomNav(selectedTab = DashboardTab.ASSETS, onTabSelected = {})
    }
}
