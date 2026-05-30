package com.daprox.financeos.presentation.accountform

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.daprox.financeos.domain.model.AccountTypeEnum
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.ErrorStateView
import com.daprox.financeos.presentation.core.designsystem.component.ShimmerBox
import com.daprox.financeos.presentation.core.designsystem.finColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Root composable for the account form screen.
 *
 * Bridges [AccountFormViewModel] and [AccountFormScreen], collecting state and observing
 * navigation events.
 *
 * @param accountId null = create mode; non-null = edit mode for that account
 * @param viewModel injected via Koin with parameters
 * @param onNavigateBack callback to navigate back after successful save or delete
 */
@Composable
fun AccountFormScreenRoot(
    accountId: String?,
    viewModel: AccountFormViewModel = koinViewModel(parameters = { parametersOf(accountId) }),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AccountFormUiEvent.NavigateBack -> onNavigateBack()
        }
    }

    AccountFormScreen(state = state, onAction = viewModel::onAction)
}

/**
 * Account form screen UI.
 *
 * Displays create or edit form with avatar preview, name, type selector (radio cards), balance,
 * optional cap field (EPARGNE only), and color picker. Shows loading skeleton and error states.
 * Save/Delete buttons in footer. Delete confirmation dialog in edit mode.
 *
 * @param state current form state (inputs, loading, errors)
 * @param onAction callback to dispatch actions to ViewModel
 */
@Composable
fun AccountFormScreen(
    state: AccountFormUiState,
    onAction: (AccountFormUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val isEditMode = state.accountId != null

    if (state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(60.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp))
        }
        return
    }
    if (state.isError) {
        ErrorStateView(onRetry = { onAction(AccountFormUiAction.OnBackClick) })
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Top bar ────────────────────────────────────────────
            AccountFormTopBar(
                title = if (isEditMode) "Modifier le compte" else "Nouveau compte",
                onBack = { onAction(AccountFormUiAction.OnBackClick) },
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

                // Avatar preview
                AvatarPreview(
                    initials = state.name.trim()
                        .replace(Regex("[^A-Za-zÀ-ÿ0-9 ]"), "")
                        .take(2)
                        .uppercase()
                        .ifEmpty { "·" },
                    colorHex = state.colorHex,
                    typeLabel = accountTypeLabel(state.type),
                )

                // 1. Name
                AccountFormField(label = "NOM DU COMPTE") {
                    AccountTextInput(
                        value = state.name,
                        onValueChange = { onAction(AccountFormUiAction.OnNameChanged(it)) },
                        placeholder = "Ex. Livret A, Boursorama, PEA…",
                    )
                }

                // 2. Type segmented (3-way)
                AccountFormField(
                    label = "TYPE",
                    hint = accountTypeHint(state.type),
                ) {
                    AccountTypeSelector(
                        selected = state.type,
                        onSelect = { onAction(AccountFormUiAction.OnTypeSelected(it)) },
                    )
                }

                // 3. Balance — large mono input
                AccountFormField(label = "SOLDE ACTUEL") {
                    LargeAmountInput(
                        value = state.balance,
                        onValueChange = { onAction(AccountFormUiAction.OnBalanceChanged(it)) },
                        placeholder = "0",
                    )
                }

                // 4. Cap — only for Épargne
                if (state.type == AccountTypeEnum.EPARGNE) {
                    AccountFormField(
                        label = "PLAFOND DU LIVRET",
                        hint = "Plafond légal Livret A : 22 950 € · LDDS : 12 000 €",
                    ) {
                        AccountAmountInput(
                            value = state.cap,
                            onValueChange = { onAction(AccountFormUiAction.OnCapChanged(it)) },
                            placeholder = "22 950",
                        )
                    }
                }

                // 5. Color picker
                AccountFormField(label = "COULEUR DE L'AVATAR") {
                    ColorPicker(
                        selected = state.colorHex,
                        onSelect = { onAction(AccountFormUiAction.OnColorSelected(it)) },
                    )
                }

                // 6. Edit-only: destructive delete button
                if (isEditMode) {
                    Surface(
                        onClick = { onAction(AccountFormUiAction.OnDeleteClick) },
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
                                text = "Supprimer le compte",
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
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
                    )
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 14.dp,
                        bottom = 18.dp + navBarPadding.calculateBottomPadding(),
                    ),
            ) {
                Button(
                    onClick = { onAction(AccountFormUiAction.OnSaveClick) },
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
                        text = if (isEditMode) "Enregistrer" else "Ajouter le compte",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onAction(AccountFormUiAction.OnDeleteDismissed) },
            title = { Text("Supprimer le compte ?") },
            text = { Text("Cette action est irréversible. Le compte et son historique seront supprimés.") },
            confirmButton = {
                TextButton(onClick = { onAction(AccountFormUiAction.OnDeleteConfirmed) }) {
                    Text(
                        text = "Supprimer",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(AccountFormUiAction.OnDeleteDismissed) }) {
                    Text("Annuler")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    }
}

// ── Sub-composables ──────────────────────────────────────────────────────────

@Composable
private fun AccountFormTopBar(
    title: String,
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
                text = "Suivi manuel — aucune connexion bancaire",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AvatarPreview(
    initials: String,
    colorHex: String,
    typeLabel: String,
    modifier: Modifier = Modifier,
) {
    val color = runCatching { Color(android.graphics.Color.parseColor(colorHex)) }
        .getOrElse { MaterialTheme.colorScheme.surfaceVariant }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = GeistMono,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                    letterSpacing = (-0.5).sp,
                ),
                color = color,
            )
        }
        Text(
            text = typeLabel.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 11.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AccountTypeSelector(
    selected: AccountTypeEnum,
    onSelect: (AccountTypeEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        AccountTypeEnum.COURANT to "Courant",
        AccountTypeEnum.EPARGNE to "Épargne",
        AccountTypeEnum.INVESTISSEMENT to "Invest.",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { (type, label) ->
            val active = type == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (active) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelect(type) }
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                    color = if (active) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ColorPicker(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ACCOUNT_COLOR_OPTIONS.forEach { hex ->
            val active = hex == selected
            val color = runCatching { Color(android.graphics.Color.parseColor(hex)) }
                .getOrElse { Color.Gray }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onSelect(hex) }
                    .then(
                        if (active) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (active) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LargeAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            textStyle = TextStyle(
                fontFamily = GeistMono,
                fontWeight = FontWeight.Medium,
                fontSize = 32.sp,
                letterSpacing = (-1).sp,
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
                            fontSize = 32.sp,
                            letterSpacing = (-1).sp,
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
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = GeistMono,
                fontSize = 20.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AccountAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
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
private fun AccountTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
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
private fun AccountFormField(
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

private fun accountTypeLabel(type: AccountTypeEnum): String = when (type) {
    AccountTypeEnum.COURANT -> "Compte courant"
    AccountTypeEnum.EPARGNE -> "Épargne"
    AccountTypeEnum.INVESTISSEMENT -> "Investissement"
}

private fun accountTypeHint(type: AccountTypeEnum): String = when (type) {
    AccountTypeEnum.COURANT -> "Compte du quotidien — Boursorama, BNP…"
    AccountTypeEnum.EPARGNE -> "Livret réglementé — Livret A, LDDS…"
    AccountTypeEnum.INVESTISSEMENT -> "Portefeuille marché — PEA, CTO…"
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun AccountFormCreatePreview() {
    FinanceOSTheme {
        AccountFormScreen(
            state = AccountFormUiState(),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun AccountFormEditPreview() {
    FinanceOSTheme {
        AccountFormScreen(
            state = AccountFormUiState(
                accountId = "livreta",
                name = "Livret A",
                type = AccountTypeEnum.EPARGNE,
                balance = "18400",
                cap = "22950",
                colorHex = "#7eb8f7",
            ),
            onAction = {},
        )
    }
}
