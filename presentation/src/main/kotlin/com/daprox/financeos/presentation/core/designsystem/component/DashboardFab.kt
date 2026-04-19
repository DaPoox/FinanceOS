package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme

// Gradient FAB for adding a new transaction.
// Background: linear gradient primary → primaryContainer (top to bottom).
// Glow: shadow using primary color approximated via a translucent shadow modifier.
@Composable
fun DashboardFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            )
            .clip(CircleShape)
            .background(gradientBrush),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter une opération",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun DashboardFabPreview() {
    FinanceOSTheme {
        DashboardFab(onClick = {})
    }
}
