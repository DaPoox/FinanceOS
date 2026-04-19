package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.dashboard.model.ProgressBarUi

// Card section containing two stacked progress bars (month + week budget tracking).
// Background matches surfaceContainerLow — one tonal step above the screen background.
@Composable
fun DualProgressBarsSection(
    progressBars: List<ProgressBarUi>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        progressBars.forEach { bar ->
            GradientProgressBar(bar = bar)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun DualProgressBarsSectionPreview() {
    FinanceOSTheme {
        DualProgressBarsSection(
            progressBars = listOf(
                ProgressBarUi(
                    label = "MOIS",
                    formattedAmount = "2 400 €",
                    progress = 0.62f,
                    isGradient = false,
                ),
                ProgressBarUi(
                    label = "SEMAINE",
                    formattedAmount = "480 €",
                    progress = 0.45f,
                    isGradient = true,
                ),
            )
        )
    }
}
