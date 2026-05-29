package com.daprox.financeos.presentation.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.X
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.finColors
import kotlinx.coroutines.delay

/**
 * Bottom sheet for quick expense entry.
 *
 * Provides a numeric keypad, envelope selector, note input, and confirmation flow. Caller
 * controls visibility via [sheetState]. Internal state (amount, selection, note) resets
 * automatically 250ms after sheet closes. Confirmation view displays amount + envelope name
 * for 700ms with optional checkmark animation before triggering [onSave].
 *
 * @param envelopes list of selectable budget envelopes
 * @param sheetState manages sheet visibility and drag state
 * @param onDismiss callback when sheet is dismissed (back button or outside tap)
 * @param onSave callback with validated amount, selected envelope, and optional note
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseSheet(
    envelopes: List<EnvelopeChipUiState>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (amount: Double, envelopeId: String, note: String) -> Unit,
) {
    var amount by remember { mutableStateOf("") }
    var selectedId by remember(envelopes) { mutableStateOf(envelopes.firstOrNull()?.id.orEmpty()) }
    var note by remember { mutableStateOf("") }
    var confirmed by remember { mutableStateOf(false) }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            delay(250)
            amount = ""
            note = ""
            confirmed = false
        }
    }

    LaunchedEffect(confirmed) {
        if (confirmed) {
            delay(700)
            val parsed = amount.replace(',', '.').toDoubleOrNull() ?: 0.0
            onSave(parsed, selectedId, note)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { GrabberHandle() },
        tonalElevation = 0.dp,
    ) {
        SheetContent(
            amount = amount,
            selectedId = selectedId,
            note = note,
            confirmed = confirmed,
            envelopes = envelopes,
            onPush = { k ->
                amount = when {
                    k == "," -> if (',' in amount) amount else (amount.ifEmpty { "0" }) + ","
                    amount == "0" -> k
                    else -> amount + k
                }
            },
            onDoublePush = {
                amount = if (amount.isEmpty() || amount == "0") "0" else amount + "00"
            },
            onBack = { if (amount.isNotEmpty()) amount = amount.dropLast(1) },
            onClear = { amount = "" },
            onSelectedIdChange = { selectedId = it },
            onNoteChange = { note = it },
            onValidate = { if (amount.isNotEmpty() && amount != "0") confirmed = true },
            onClose = onDismiss,
        )
    }
}

@Composable
private fun GrabberHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(38.dp)
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(2.dp)),
        )
    }
}

@Composable
private fun SheetContent(
    amount: String,
    selectedId: String,
    note: String,
    confirmed: Boolean,
    envelopes: List<EnvelopeChipUiState>,
    onPush: (String) -> Unit,
    onDoublePush: () -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onSelectedIdChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onValidate: () -> Unit,
    onClose: () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Nouvelle dépense",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Fermer",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Amount display / Confirmation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (confirmed) {
                ConfirmationView(
                    amount = amount,
                    envelopeName = envelopes.find { it.id == selectedId }?.name.orEmpty(),
                )
            } else {
                AmountDisplay(amount = amount)
            }
        }

        if (!confirmed) {
            // Envelope chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 14.dp),
            ) {
                items(envelopes, key = { it.id }) { chip ->
                    EnvelopeChip(
                        chip = chip,
                        selected = chip.id == selectedId,
                        onClick = { onSelectedIdChange(chip.id) },
                    )
                }
            }

            // Note field
            BasicTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (note.isEmpty()) {
                        Text(
                            text = "Note (optionnelle)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    innerTextField()
                },
            )

            // Numpad
            NumPad(
                amount = amount,
                onPush = onPush,
                onDoublePush = onDoublePush,
                onBack = onBack,
                onClear = onClear,
                onValidate = onValidate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
        }
    }
}

@Composable
private fun AmountDisplay(amount: String) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = amount.ifEmpty { "0" },
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.Medium,
                fontSize = 56.sp,
                letterSpacing = (-2).sp,
            ),
            color = if (amount.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "€",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = GeistMono,
                fontSize = 28.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ConfirmationView(amount: String, envelopeName: String) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.4f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        ) + fadeIn(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.finColors.positive.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Lucide.Check,
                    contentDescription = null,
                    tint = MaterialTheme.finColors.positive,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = "$amount € sur $envelopeName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun EnvelopeChip(
    chip: EnvelopeChipUiState,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                  else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.outline
    val contentColor = if (selected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = chip.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = chip.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
            color = contentColor,
        )
    }
}

@Composable
private fun NumPad(
    amount: String,
    onPush: (String) -> Unit,
    onDoublePush: () -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 3-column digit grid
        Column(
            modifier = Modifier.weight(3f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1", "2", "3").forEach { k ->
                    NumKey(k, Modifier.weight(1f).height(52.dp)) { onPush(k) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("4", "5", "6").forEach { k ->
                    NumKey(k, Modifier.weight(1f).height(52.dp)) { onPush(k) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("7", "8", "9").forEach { k ->
                    NumKey(k, Modifier.weight(1f).height(52.dp)) { onPush(k) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(Modifier.weight(1f))
                NumKey(",", Modifier.weight(1f).height(52.dp), accent = true) { onPush(",") }
                NumKey("0", Modifier.weight(1f).height(52.dp)) { onPush("0") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumKey("00", Modifier.weight(1f).height(52.dp), accent = true, onClick = onDoublePush)
                NumKey("C", Modifier.weight(1f).height(52.dp), accent = true, onClick = onClear)
                Spacer(Modifier.weight(1f))
            }
        }

        // Column 4: backspace (row 1) + validate key (rows 2-5)
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
        ) {
            NumKey(
                label = "⌫",
                modifier = Modifier.fillMaxWidth().height(52.dp),
                accent = true,
                onClick = onBack,
            )
            Spacer(Modifier.height(8.dp))
            ValidateKey(
                enabled = amount.isNotEmpty() && amount != "0",
                modifier = Modifier.fillMaxWidth().weight(1f),
                onClick = onValidate,
            )
        }
    }
}

@Composable
private fun NumKey(
    label: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bgColor = if (isPressed) MaterialTheme.colorScheme.surfaceContainer
                  else MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = GeistMono,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
            ),
            color = if (accent) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ValidateKey(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (enabled) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    val iconTint = if (enabled) MaterialTheme.colorScheme.onPrimary
                   else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Lucide.Check,
            contentDescription = "Valider",
            tint = iconTint,
            modifier = Modifier.size(24.dp),
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private val previewChips = listOf(
    EnvelopeChipUiState("groceries", "Courses", Lucide.ShoppingCart),
    EnvelopeChipUiState("restos", "Restos", Lucide.Utensils),
    EnvelopeChipUiState("transport", "Transport", Lucide.ShoppingCart),
)

@Preview(name = "Vide", showBackground = true, backgroundColor = 0xFF0F1420)
@Composable
private fun ExpenseSheetPreviewEmpty() {
    FinanceOSTheme {
        SheetContent(
            amount = "", selectedId = "groceries", note = "", confirmed = false,
            envelopes = previewChips,
            onPush = {}, onDoublePush = {}, onBack = {}, onClear = {},
            onSelectedIdChange = {}, onNoteChange = {}, onValidate = {}, onClose = {},
        )
    }
}

@Preview(name = "Montant saisi", showBackground = true, backgroundColor = 0xFF0F1420)
@Composable
private fun ExpenseSheetPreviewFilled() {
    FinanceOSTheme {
        SheetContent(
            amount = "24,50", selectedId = "restos", note = "", confirmed = false,
            envelopes = previewChips,
            onPush = {}, onDoublePush = {}, onBack = {}, onClear = {},
            onSelectedIdChange = {}, onNoteChange = {}, onValidate = {}, onClose = {},
        )
    }
}

@Preview(name = "Confirmé", showBackground = true, backgroundColor = 0xFF0F1420)
@Composable
private fun ExpenseSheetPreviewConfirmed() {
    FinanceOSTheme {
        SheetContent(
            amount = "24,50", selectedId = "groceries", note = "", confirmed = true,
            envelopes = previewChips,
            onPush = {}, onDoublePush = {}, onBack = {}, onClear = {},
            onSelectedIdChange = {}, onNoteChange = {}, onValidate = {}, onClose = {},
        )
    }
}
