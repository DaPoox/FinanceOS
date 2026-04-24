package com.daprox.financeos.presentation.envelopes.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme

// Top bar for the Envelopes screen.
// "Envelopes" title in primary green, edit icon + avatar on the right.
// Navigation between screens is handled by the shared bottom nav — no back arrow needed.
@Composable
fun EnvelopesTopBar(
    onEditClick : () -> Unit = {},
    modifier    : Modifier   = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text  = "Envelopes",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
            ),
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.weight(1f))

        // Pencil icon — navigates to the EnvelopesEdit screen.
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector        = Icons.Default.Edit,
                contentDescription = "Edit allocations",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(20.dp),
            )
        }

        // Circular avatar — placeholder initials until profile system is built.
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = "A",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun EnvelopesTopBarPreview() {
    FinanceOSTheme {
        EnvelopesTopBar()
    }
}
