package com.daprox.financeos.presentation.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import com.daprox.financeos.presentation.dashboard.model.TransactionUi

// List of recent transactions inside a card container.
// Each row has a colored avatar (first letter of merchant), merchant + date, and amount.
@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionUi>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        // Section header
        Text(
            text = "DERNIÈRES OPÉRATIONS",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            ),
        )

        Spacer(Modifier.height(16.dp))

        transactions.forEachIndexed { index, transaction ->
            TransactionRow(transaction = transaction)

            if (index < transactions.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
            }
        }
    }
}

// Single transaction row: avatar | merchant + date | amount
@Composable
private fun TransactionRow(transaction: TransactionUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Circular avatar — first letter of merchant name on a category-colored background.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(transaction.avatarColor.copy(alpha = 0.15f)),
        ) {
            Text(
                text = transaction.merchant.first().uppercase(),
                color = transaction.avatarColor,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            )
        }

        // Merchant name + category/date
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.merchant,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
            )
            Text(
                text = "${transaction.category} · ${transaction.formattedDate}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
            )
        }

        // Amount — red for expenses, green for income
        Text(
            text = transaction.formattedAmount,
            color = if (transaction.isExpense) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1417)
@Composable
private fun RecentTransactionsSectionPreview() {
    FinanceOSTheme {
        RecentTransactionsSection(transactions = previewTransactions())
    }
}

private fun previewTransactions() = listOf(
    TransactionUi("1", "Carrefour",  "Alimentation", "Aujourd'hui",  "- 87,50 €",  true,  androidx.compose.ui.graphics.Color(0xFF93C5FD)),
    TransactionUi("2", "Salaire",    "Revenus",      "15 avr.",      "+ 2 800 €",  false, androidx.compose.ui.graphics.Color(0xFF6EE591)),
    TransactionUi("3", "Netflix",    "Loisirs",      "14 avr.",      "- 17,99 €",  true,  androidx.compose.ui.graphics.Color(0xFFC4B5FD)),
    TransactionUi("4", "RATP",       "Transport",    "13 avr.",      "- 1,90 €",   true,  androidx.compose.ui.graphics.Color(0xFFF87171)),
)
