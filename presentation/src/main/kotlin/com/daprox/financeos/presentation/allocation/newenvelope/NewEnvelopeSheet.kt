package com.daprox.financeos.presentation.allocation.newenvelope

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PackageOpen
import com.daprox.financeos.presentation.core.designsystem.ENVELOPE_ICON_KEYS
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

private val FORM_TYPES = setOf(
    EnvelopeTypeEnum.VARIABLE,
    EnvelopeTypeEnum.MONTHLY,
)

/**
 * Bottom sheet for creating a new envelope directly from StepAdjust.
 *
 * Displays a form for VARIABLE and MONTHLY envelope types with fields for:
 * - Name input
 * - Icon picker (8-column grid)
 * - Amount input
 *
 * For SAVINGS/INVESTMENT/PERMANENT/FIXED types, shows a redirect message instead
 * (users must create these from the dedicated Envelopes screen).
 *
 * @param presetTypeKey The envelope type key to pre-select (e.g., "VARIABLE"), or null
 * @param onDismiss Callback invoked when the sheet is dismissed
 * @param onSave Callback invoked when the form is submitted; parameters are name, typeKey, iconKey, and amount
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEnvelopeSheet(
    presetTypeKey: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, typeKey: String, iconKey: String, amount: Double) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presetType = presetTypeKey?.let { runCatching { EnvelopeTypeEnum.valueOf(it) }.getOrNull() }
    val isFormType = presetType in FORM_TYPES

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        if (isFormType && presetType != null) {
            NewEnvelopeForm(
                typeKey = presetType.name,
                typeLabel = typeLabelFor(presetType),
                onDismiss = onDismiss,
                onSave = onSave,
            )
        } else {
            // SAVINGS, INVESTMENT, PERMANENT, FIXED — redirect to the full form screen
            NewEnvelopeRedirect(onDismiss = onDismiss)
        }
    }
}

/**
 * Full form for creating a new envelope (VARIABLE and MONTHLY types only).
 *
 * Fields:
 * - Name: Required, custom input with placeholder
 * - Icon: 8-column grid picker from the design system's icon library
 * - Amount: Required, numeric input with decimal support
 *
 * The save button is disabled until name and amount are both valid.
 *
 * @param typeKey The envelope type key (e.g., "VARIABLE")
 * @param typeLabel The localized type label for display (e.g., "Variable")
 * @param onDismiss Callback invoked when the sheet is dismissed
 * @param onSave Callback invoked when the form is submitted; parameters are name, typeKey, iconKey, and amount
 */
@Composable
private fun NewEnvelopeForm(
    typeKey: String,
    typeLabel: String,
    onDismiss: () -> Unit,
    onSave: (name: String, typeKey: String, iconKey: String, amount: Double) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedIconKey by remember { mutableStateOf(ENVELOPE_ICON_KEYS.first()) }
    var amount by remember { mutableStateOf("") }
    val primary = MaterialTheme.colorScheme.primary
    val canSave = name.isNotBlank() && amount.toDoubleOrNull() != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
    ) {
        // Sheet title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Nouvelle enveloppe",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name input
        Text(
            text = "NOM",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            textStyle = TextStyle(
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(primary),
            singleLine = true,
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    if (name.isEmpty()) {
                        Text(
                            text = "ex. Restaurants",
                            style = TextStyle(fontSize = 15.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                    inner()
                }
            },
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Icon picker
        Text(
            text = "ICÔNE",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(ENVELOPE_ICON_KEYS) { key ->
                val selected = key == selectedIconKey
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable { selectedIconKey = key },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = iconKeyToImageVector(key),
                        contentDescription = null,
                        tint = if (selected) primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Amount input
        Text(
            text = "MONTANT ALLOUÉ",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                textStyle = TextStyle(
                    fontFamily = GeistMono,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                cursorBrush = SolidColor(primary),
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (amount.isEmpty()) {
                        Text(
                            text = "0",
                            style = TextStyle(
                                fontFamily = GeistMono,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            ),
                        )
                    }
                    inner()
                },
            )
            Text(
                text = "€",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = GeistMono,
                    fontSize = 14.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                onSave(name.trim(), typeKey, selectedIconKey, parsedAmount)
            },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
        ) {
            Text(
                text = "Créer l'enveloppe",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
            )
        }
    }
}

/**
 * Redirect message for complex envelope types.
 *
 * Shown for SAVINGS/INVESTMENT/PERMANENT/FIXED types, instructing users
 * to create these envelope types from the dedicated Envelopes screen.
 *
 * @param onDismiss Callback invoked when the close button is clicked
 */
@Composable
private fun NewEnvelopeRedirect(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Lucide.PackageOpen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Crée ce type d'enveloppe depuis l'écran Enveloppes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Fermer",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onDismiss),
        )
    }
}

/**
 * Returns the localized label for an envelope type.
 *
 * @param type The [EnvelopeTypeEnum]
 * @return The localized label (e.g., "Variable", "Du mois")
 */
private fun typeLabelFor(type: EnvelopeTypeEnum): String = when (type) {
    EnvelopeTypeEnum.VARIABLE -> "Variable"
    EnvelopeTypeEnum.MONTHLY -> "Du mois"
    else -> type.name
}
