package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.dashboard.model.NetWorthUi

// Pill-shaped row card. Label on the left, formatted net worth on the right.
// Background is one tonal step above the screen background (surfaceContainerLow).
@Composable
fun NetWorthCard(
    netWorth: NetWorthUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = netWorth.label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
            ),
        )
        Text(
            text = netWorth.formattedAmount,
            color = MaterialTheme.colorScheme.onSurface,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun NetWorthCardPreview() {
    FinanceOSTheme {
        NetWorthCard(
            netWorth = NetWorthUi(
                label = "NET WORTH",
                formattedAmount = "12 450 €",
            )
        )
    }
}
