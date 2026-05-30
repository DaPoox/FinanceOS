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
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.X
import com.daprox.financeos.presentation.core.designsystem.ENVELOPE_ICON_KEYS
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import com.daprox.financeos.presentation.core.designsystem.finColors
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum

// SAVINGS and INVESTMENT are account-based — redirect to Patrimoine instead of showing a form
private val ACCOUNT_TYPES = setOf(
    EnvelopeTypeEnum.SAVINGS,
    EnvelopeTypeEnum.INVESTMENT,
)

private data class EnvelopeTypeLabels(
    val title: String,
    val placeholder: String,
    val amountLabel: String,
    val showGoal: Boolean = false,
)

private fun labelsFor(type: EnvelopeTypeEnum): EnvelopeTypeLabels = when (type) {
    EnvelopeTypeEnum.FIXED -> EnvelopeTypeLabels("Nouvelle charge fixe", "Mutuelle, Téléphone…", "Montant mensuel")
    EnvelopeTypeEnum.VARIABLE -> EnvelopeTypeLabels("Nouvelle catégorie", "Loisirs, Pharmacie…", "Budget mensuel")
    EnvelopeTypeEnum.MONTHLY -> EnvelopeTypeLabels("Nouvelle dépense du mois", "Vacances Lisbonne…", "Montant à allouer")
    EnvelopeTypeEnum.PERMANENT -> EnvelopeTypeLabels("Nouvel objectif", "Voyage Japon, Vélo…", "Contribution mensuelle", showGoal = true)
    else -> EnvelopeTypeLabels("Nouvelle enveloppe", "", "Montant")
}

/**
 * Bottom sheet for creating a new envelope directly from the allocation adjustment step.
 *
 * Shows a full creation form for FIXED, VARIABLE, MONTHLY, and PERMANENT types.
 * SAVINGS and INVESTMENT redirect to Patrimoine (account-based flow).
 *
 * @param presetTypeKey The envelope type key to pre-select (e.g., "FIXED"), or null
 * @param onDismiss Callback invoked when the sheet is dismissed
 * @param onSave Callback invoked on save; parameters are name, typeKey, iconKey, amount
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
    val isAccountType = presetType in ACCOUNT_TYPES

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        if (isAccountType || presetType == null) {
            NewEnvelopePatrimoineRedirect(onDismiss = onDismiss)
        } else {
            NewEnvelopeForm(
                type = presetType,
                onDismiss = onDismiss,
                onSave = onSave,
            )
        }
    }
}

@Composable
private fun NewEnvelopeForm(
    type: EnvelopeTypeEnum,
    onDismiss: () -> Unit,
    onSave: (name: String, typeKey: String, iconKey: String, amount: Double) -> Unit,
) {
    val labels = labelsFor(type)
    var name by remember { mutableStateOf("") }
    var selectedIconKey by remember { mutableStateOf(ENVELOPE_ICON_KEYS.first()) }
    var amount by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    val primary = MaterialTheme.colorScheme.primary
    val canSave = name.isNotBlank() && amount.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "CRÉER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.4.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = labels.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Icon(
                imageVector = Lucide.X,
                contentDescription = "Fermer",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onDismiss),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name
        SheetFieldLabel("NOM")
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            textStyle = TextStyle(fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
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
                            text = labels.placeholder,
                            style = TextStyle(fontSize = 15.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                    inner()
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Icon picker
        SheetFieldLabel("ICÔNE")
        Spacer(modifier = Modifier.height(6.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth().height(88.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(ENVELOPE_ICON_KEYS) { key ->
                val selected = key == selectedIconKey
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(if (selected) 1.5.dp else 1.dp, if (selected) primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
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

        Spacer(modifier = Modifier.height(16.dp))

        // Amount
        SheetFieldLabel(labels.amountLabel.uppercase())
        Spacer(modifier = Modifier.height(6.dp))
        AmountInputRow(value = amount, onValueChange = { amount = it }, primary = primary)

        // Optional goal (PERMANENT only)
        if (labels.showGoal) {
            Spacer(modifier = Modifier.height(16.dp))
            SheetFieldLabel("OBJECTIF TOTAL (OPTIONNEL)")
            Spacer(modifier = Modifier.height(6.dp))
            AmountInputRow(value = goal, onValueChange = { goal = it }, primary = primary, placeholder = "Ex. 3 000")
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
            ) {
                Text("Annuler", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
            }
            Button(
                onClick = { onSave(name.trim(), type.name, selectedIconKey, amount.toDoubleOrNull() ?: 0.0) },
                enabled = canSave,
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
            ) {
                Text("Créer l'enveloppe", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp))
            }
        }
    }
}

@Composable
private fun SheetFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun AmountInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    primary: androidx.compose.ui.graphics.Color,
    placeholder: String = "0",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            textStyle = TextStyle(fontFamily = GeistMono, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(primary),
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontFamily = GeistMono, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
                    )
                }
                inner()
            },
        )
        Text(
            text = "€",
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = GeistMono, fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Shown for SAVINGS and INVESTMENT — these are managed as accounts in Patrimoine. */
@Composable
private fun NewEnvelopePatrimoineRedirect(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 48.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.finColors.savings.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Lucide.Wallet,
                    contentDescription = null,
                    tint = MaterialTheme.finColors.savings,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ouvrir un compte",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Les comptes d'épargne et d'investissement se gèrent depuis Patrimoine. Le versement mensuel apparaîtra ensuite ici.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text("Aller dans Patrimoine →", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp))
                }
            }
        }
    }
}

