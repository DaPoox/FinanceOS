package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.dashboard.model.LiquidityItemUi

// Two side-by-side liquidity cards, each with a primary-colored left accent border.
// Items are displayed in equal-width columns with a gap between them.
@Composable
fun LiquiditySection(
    items: List<LiquidityItemUi>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.forEach { item ->
            LiquidityCard(
                item = item,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// Single liquidity card: green left-border accent + label / amount / subtitle stack.
@Composable
private fun LiquidityCard(
    item: LiquidityItemUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .height(96.dp),
    ) {
        // Green left-border accent
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.formattedAmount,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun LiquiditySectionPreview() {
    FinanceOSTheme {
        LiquiditySection(
            items = listOf(
                LiquidityItemUi(
                    label = "DISPONIBLE",
                    formattedAmount = "3 240 €",
                    subtitle = "Compte courant",
                ),
                LiquidityItemUi(
                    label = "ÉPARGNE",
                    formattedAmount = "8 500 €",
                    subtitle = "Livret A",
                ),
            )
        )
    }
}
