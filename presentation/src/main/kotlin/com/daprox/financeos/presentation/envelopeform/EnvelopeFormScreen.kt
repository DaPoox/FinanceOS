package com.daprox.financeos.presentation.envelopeform

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.daprox.financeos.domain.model.EnvelopeTypeEnum
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.ENVELOPE_ICON_KEYS
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.ErrorStateView
import com.daprox.financeos.presentation.core.designsystem.component.ShimmerBox
import com.daprox.financeos.presentation.core.designsystem.iconKeyToImageVector
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Root composable for the envelope form screen.
 *
 * Bridges [EnvelopeFormViewModel] and [EnvelopeFormScreen], collecting state and observing
 * navigation events.
 *
 * @param envelopeId null = create mode; non-null = edit mode for that envelope
 * @param presetType serialized EnvelopeTypeEnum name to pre-select in create mode
 * @param viewModel injected via Koin with parameters
 * @param onNavigateBack callback to navigate back after successful save or delete
 */
@Composable
fun EnvelopeFormScreenRoot(
    envelopeId: String?,
    presetType: String?,
    viewModel: EnvelopeFormViewModel = koinViewModel(parameters = { parametersOf(envelopeId, presetType) }),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EnvelopeFormUiEvent.NavigateBack -> onNavigateBack()
        }
    }

    EnvelopeFormScreen(state = state, onAction = viewModel::onAction)
}

/**
 * Envelope form screen UI.
 *
 * Displays create or edit form with name, type selector (radio cards), icon grid, amount input,
 * and conditional fields (accumulated for PERMANENT, cap for SAVINGS). Shows loading skeleton and
 * error states. Save/Delete buttons in footer.
 *
 * @param state current form state (inputs, loading, errors)
 * @param onAction callback to dispatch actions to ViewModel
 */
@Composable
fun EnvelopeFormScreen(
    state: EnvelopeFormUiState,
    onAction: (EnvelopeFormUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val isEditMode = state.envelopeId != null

    if (state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(160.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp))
        }
        return
    }
    if (state.isError) {
        ErrorStateView(onRetry = { onAction(EnvelopeFormUiAction.OnBackClick) })
        return
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Top bar ────────────────────────────────────────────
            EnvelopeFormTopBar(
                title = if (isEditMode) state.name.ifBlank { "Enveloppe" } else "Nouvelle enveloppe",
                subtitle = if (isEditMode) "Modifier l'enveloppe" else "Crée une enveloppe de budget",
                onBack = { onAction(EnvelopeFormUiAction.OnBackClick) },
            )

            // ── Scrollable form ────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // 1. Name
                EnvFormField(label = "NOM DE L'ENVELOPPE") {
                    EnvTextInput(
                        value = state.name,
                        onValueChange = { onAction(EnvelopeFormUiAction.OnNameChanged(it)) },
                        placeholder = "Ex. Restos, Vacances Japon, Livret A…",
                    )
                }

                // 2. Type — radio cards
                EnvFormField(label = "TYPE") {
                    EnvelopeTypeCards(
                        selected = state.type,
                        onSelect = { onAction(EnvelopeFormUiAction.OnTypeSelected(it)) },
                    )
                }

                // 3. Icon grid
                EnvFormField(label = "ICÔNE") {
                    EnvelopeIconGrid(
                        selectedKey = state.iconKey,
                        onSelect = { onAction(EnvelopeFormUiAction.OnIconSelected(it)) },
                    )
                }

                // 4. Amount (label depends on type)
                EnvFormField(label = amountLabelFor(state.type).uppercase()) {
                    EnvAmountInput(
                        value = state.amount,
                        onValueChange = { onAction(EnvelopeFormUiAction.OnAmountChanged(it)) },
                    )
                }

                // 5. Accumulated — only for PERMANENT
                if (state.type == EnvelopeTypeEnum.PERMANENT) {
                    EnvFormField(
                        label = "CUMUL ACTUEL",
                        hint = "Combien tu as déjà mis de côté (mois précédents).",
                    ) {
                        EnvAmountInput(
                            value = state.accumulated,
                            onValueChange = { onAction(EnvelopeFormUiAction.OnAccumulatedChanged(it)) },
                            placeholder = "0",
                        )
                    }
                }

                // 6. Cap — only for SAVINGS
                if (state.type == EnvelopeTypeEnum.SAVINGS) {
                    EnvFormField(
                        label = "PLAFOND DU LIVRET",
                        hint = "Livret A : 22 950 € · LDDS : 12 000 €",
                    ) {
                        EnvAmountInput(
                            value = state.cap,
                            onValueChange = { onAction(EnvelopeFormUiAction.OnCapChanged(it)) },
                            placeholder = "22 950",
                        )
                    }
                }

                // 7. Edit-only: destructive delete button
                if (isEditMode) {
                    Surface(
                        onClick = { onAction(EnvelopeFormUiAction.OnDeleteClick) },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Lucide.Trash2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = "Supprimer l'enveloppe",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                ),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Sticky CTA ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 14.dp,
                        bottom = 18.dp + navBarPadding.calculateBottomPadding(),
                    ),
            ) {
                Button(
                    onClick = { onAction(EnvelopeFormUiAction.OnSaveClick) },
                    enabled = state.name.isNotBlank() && !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                ) {
                    Text(
                        text = if (isEditMode) "Enregistrer" else "Créer l'enveloppe",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }
        }
    }
}

// ── Sub-composables ──────────────────────────────────────────────────────────

@Composable
private fun EnvelopeFormTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            onClick = onBack,
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Icon(
                imageVector = Lucide.ArrowLeft,
                contentDescription = "Retour",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp),
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private data class EnvelopeTypeOption(
    val type: EnvelopeTypeEnum,
    val label: String,
    val description: String,
    val swatchHex: String,
)

private val ENVELOPE_TYPE_OPTIONS = listOf(
    EnvelopeTypeOption(EnvelopeTypeEnum.FIXED, "Fixe", "Même montant chaque mois — loyer, abonnements.", "#4a5568"),
    EnvelopeTypeOption(EnvelopeTypeEnum.VARIABLE, "Variable", "Budget ajustable — courses, restos, transport.", "#e8eef5"),
    EnvelopeTypeOption(EnvelopeTypeEnum.MONTHLY, "Du mois", "One-shot pour ce mois seulement — voyage, déménagement.", "#fb923c"),
    EnvelopeTypeOption(EnvelopeTypeEnum.PERMANENT, "Permanente", "Accumule mois après mois — vacances, cadeaux, projets.", "#22c55e"),
    EnvelopeTypeOption(EnvelopeTypeEnum.SAVINGS, "Épargne", "Livret réglementé avec plafond — Livret A, LDDS.", "#7eb8f7"),
    EnvelopeTypeOption(EnvelopeTypeEnum.INVESTMENT, "Investissement", "Portefeuille marché — PEA, CTO.", "#a78bfa"),
)

@Composable
private fun EnvelopeTypeCards(
    selected: EnvelopeTypeEnum,
    onSelect: (EnvelopeTypeEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ENVELOPE_TYPE_OPTIONS.forEach { option ->
            val active = option.type == selected
            val swatchColor = runCatching {
                Color(android.graphics.Color.parseColor(option.swatchHex))
            }.getOrElse { MaterialTheme.colorScheme.surfaceVariant }

            Surface(
                onClick = { onSelect(option.type) },
                shape = RoundedCornerShape(14.dp),
                color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    if (active) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Color swatch
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(swatchColor, RoundedCornerShape(100.dp)),
                    )
                    // Labels
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            ),
                            color = if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = option.description,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // Radio indicator
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(
                                width = if (active) 0.dp else 1.5.dp,
                                color = if (active) Color.Transparent
                                else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(100.dp),
                            )
                            .background(
                                color = if (active) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                                shape = RoundedCornerShape(100.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (active) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnvelopeIconGrid(
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Fixed-height grid (8 cols × 2 rows = 16 icons) — no scroll needed
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp), // 2 rows × 40dp + 1 gap × 8dp
        userScrollEnabled = false,
    ) {
        items(ENVELOPE_ICON_KEYS) { key ->
            val active = key == selectedKey
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(
                        color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .border(
                        1.dp,
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp),
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(key) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = iconKeyToImageVector(key),
                    contentDescription = key,
                    tint = if (active) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun EnvAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "0",
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            textStyle = TextStyle(
                fontFamily = GeistMono,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            fontFamily = GeistMono,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        ),
                    )
                }
                inner()
            },
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "€",
            style = MaterialTheme.typography.titleSmall.copy(fontFamily = GeistMono),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EnvTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        decorationBox = { inner ->
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                }
                inner()
            }
        },
    )
}

@Composable
private fun EnvFormField(
    label: String,
    hint: String? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
        hint?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    }
}

private fun amountLabelFor(type: EnvelopeTypeEnum): String = when (type) {
    EnvelopeTypeEnum.FIXED -> "Montant mensuel"
    EnvelopeTypeEnum.VARIABLE -> "Budget mensuel"
    EnvelopeTypeEnum.MONTHLY -> "Montant à allouer"
    EnvelopeTypeEnum.PERMANENT -> "Contribution mensuelle"
    EnvelopeTypeEnum.SAVINGS -> "Versement mensuel"
    EnvelopeTypeEnum.INVESTMENT -> "Versement mensuel"
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeFormCreatePreview() {
    FinanceOSTheme {
        EnvelopeFormScreen(state = EnvelopeFormUiState(), onAction = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun EnvelopeFormEditPreview() {
    FinanceOSTheme {
        EnvelopeFormScreen(
            state = EnvelopeFormUiState(
                envelopeId = "restos",
                name = "Restos & cafés",
                type = EnvelopeTypeEnum.VARIABLE,
                iconKey = "utensils",
                amount = "180",
            ),
            onAction = {},
        )
    }
}
