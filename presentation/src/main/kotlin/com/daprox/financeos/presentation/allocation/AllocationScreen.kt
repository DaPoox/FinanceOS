package com.daprox.financeos.presentation.allocation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.finColors
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun AllocationScreenRoot(
    viewModel: AllocationViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AllocationUiEvent.NavigateBack -> onNavigateBack()
        }
    }

    AllocationScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun AllocationScreen(
    state: AllocationUiState,
    onAction: (AllocationUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AllocationTopBar(
                monthLabel = state.monthLabel,
                step = state.step,
                onBack = { onAction(AllocationUiAction.OnBack) },
            )
        },
        bottomBar = {
            AllocationFooter(
                step = state.step,
                remaining = state.remaining,
                incomeBlank = state.income.isBlank(),
                onNext = { onAction(AllocationUiAction.OnNext) },
                navBarPadding = navBarPadding.calculateBottomPadding(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            when (state.step) {
                0 -> StepIncome(
                    income = state.income,
                    onIncomeChanged = { onAction(AllocationUiAction.OnIncomeChanged(it)) },
                )
                1 -> StepTemplate(
                    selected = state.selectedTemplate,
                    onSelect = { onAction(AllocationUiAction.OnTemplateSelected(it)) },
                )
                2 -> StepAdjust(
                    groups = state.groups,
                    onAmountChanged = { id, amt -> onAction(AllocationUiAction.OnEnvelopeAmountChanged(id, amt)) },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AllocationTopBar(
    monthLabel: String,
    step: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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

        Text(
            text = "Allouer $monthLabel",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            textAlign = TextAlign.Center,
        )

        StepIndicator(step = step)
    }
}

@Composable
private fun StepIndicator(step: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (0..2).forEach { i ->
            val w by animateDpAsState(
                targetValue = if (i == step) 20.dp else 6.dp,
                animationSpec = tween(durationMillis = 240),
                label = "step_dot_$i",
            )
            Box(
                modifier = Modifier
                    .width(w)
                    .height(6.dp)
                    .background(
                        color = if (i <= step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(100.dp),
                    ),
            )
        }
    }
}

@Composable
private fun StepIncome(
    income: String,
    onIncomeChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val suggestions = listOf("4000" to "4 000", "4200" to "4 200", "4500" to "4 500", "5000" to "5 000")

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Étape 1 sur 3",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Quel est ton revenu ce mois ?",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                BasicTextField(
                    value = income,
                    onValueChange = { onIncomeChanged(it.filter { c -> c.isDigit() }) },
                    textStyle = TextStyle(
                        fontFamily = GeistMono,
                        fontWeight = FontWeight.Medium,
                        fontSize = 56.sp,
                        letterSpacing = (-2).sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(primary),
                    singleLine = true,
                    modifier = Modifier.width(220.dp),
                )
                Text(
                    text = " €",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = GeistMono,
                        fontSize = 28.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(160.dp)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, primary, Color.Transparent),
                        )
                    ),
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            suggestions.forEach { (value, label) ->
                val active = value == income
                Surface(
                    onClick = { onIncomeChanged(value) },
                    shape = RoundedCornerShape(100.dp),
                    color = if (active) primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (active) primary else MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    Text(
                        text = "$label €",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = GeistMono,
                            fontSize = 13.sp,
                        ),
                        color = if (active) primary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepTemplate(
    selected: TemplateTypeEnum,
    onSelect: (TemplateTypeEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    data class TemplateOption(
        val type: TemplateTypeEnum,
        val title: String,
        val subtitle: String,
        val tag: String? = null,
    )

    val options = listOf(
        TemplateOption(TemplateTypeEnum.PREVIOUS, "Mois précédent", "Avril 2026 · recommandé", "Conseillé"),
        TemplateOption(TemplateTypeEnum.PAST, "Un mois passé", "Choisir n'importe quel mois"),
        TemplateOption(TemplateTypeEnum.DEFAULT, "Template par défaut", "Ta config sauvegardée"),
        TemplateOption(TemplateTypeEnum.SCRATCH, "From scratch", "Tout à zéro"),
    )

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Étape 2 sur 3",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Sur quelle base partir ?",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val active = selected == option.type
                val primary = MaterialTheme.colorScheme.primary

                Surface(
                    onClick = { onSelect(option.type) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (active) primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (active) primary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (active) primary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (active) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(primary, CircleShape)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                option.tag?.let { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = primary.copy(alpha = 0.12f),
                                    ) {
                                        Text(
                                            text = tag,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = primary,
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepAdjust(
    groups: List<AllocationEnvelopeGroup>,
    onAmountChanged: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Étape 3 sur 3",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ajuste tes enveloppes",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        groups.forEach { group ->
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.label.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                group.envelopes.forEach { envelope ->
                    AdjustRow(
                        envelope = envelope,
                        onAmountChanged = { onAmountChanged(envelope.id, it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AdjustRow(
    envelope: AllocationEnvelopeUiState,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconTint = when (envelope.type) {
        EnvelopeTypeEnum.SAVINGS -> MaterialTheme.finColors.savings
        EnvelopeTypeEnum.INVESTMENT -> MaterialTheme.finColors.investment
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = envelope.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp),
            )
        }

        Text(
            text = envelope.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )

        BasicTextField(
            value = envelope.amount,
            onValueChange = { onAmountChanged(it.filter { c -> c.isDigit() }) },
            textStyle = TextStyle(
                fontFamily = GeistMono,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            modifier = Modifier
                .width(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )

        Text(
            text = "€",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = GeistMono,
                fontSize = 13.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AllocationFooter(
    step: Int,
    remaining: Double,
    incomeBlank: Boolean,
    onNext: () -> Unit,
    navBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val remainingColor = when {
        remaining < 0 -> MaterialTheme.colorScheme.error
        remaining <= 50 -> MaterialTheme.finColors.warning
        else -> MaterialTheme.finColors.positive
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .drawBehind {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp + navBarPadding),
    ) {
        if (step == 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Non alloué",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${if (remaining < 0) "−" else ""}${abs(remaining).toLong().frenchAmount()} €",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = GeistMono,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    ),
                    color = remainingColor,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onNext,
            enabled = !(step == 0 && incomeBlank),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = if (step < 2) "Continuer" else "Valider l'allocation",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.3.sp,
                ),
            )
        }
    }
}

@Preview(name = "Step 1 — Revenu", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewStep1() {
    FinanceOSTheme {
        AllocationScreen(
            state = AllocationUiState(step = 0, income = "4200"),
            onAction = {},
        )
    }
}

@Preview(name = "Step 2 — Template", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewStep2() {
    FinanceOSTheme {
        AllocationScreen(
            state = AllocationUiState(step = 1, selectedTemplate = TemplateTypeEnum.PREVIOUS),
            onAction = {},
        )
    }
}

@Preview(name = "Step 3 — Ajustement", showBackground = true, backgroundColor = 0xFF090C12)
@Composable
private fun PreviewStep3() {
    val groups = listOf(
        AllocationEnvelopeGroup(
            label = "Fixes",
            envelopes = listOf(
                AllocationEnvelopeUiState("loyer", "Loyer", Lucide.House, EnvelopeTypeEnum.FIXED, "900"),
            ),
        ),
        AllocationEnvelopeGroup(
            label = "Variables",
            envelopes = listOf(
                AllocationEnvelopeUiState("courses", "Courses", Lucide.ShoppingCart, EnvelopeTypeEnum.VARIABLE, "420"),
                AllocationEnvelopeUiState("restos", "Restos", Lucide.Utensils, EnvelopeTypeEnum.VARIABLE, "120"),
            ),
        ),
        AllocationEnvelopeGroup(
            label = "Du mois",
            envelopes = listOf(
                AllocationEnvelopeUiState("voyage", "Voyage été", Lucide.Plane, EnvelopeTypeEnum.MONTHLY, "400"),
            ),
        ),
        AllocationEnvelopeGroup(
            label = "Permanentes",
            envelopes = listOf(
                AllocationEnvelopeUiState("fonds", "Fonds urgence", Lucide.TrendingUp, EnvelopeTypeEnum.PERMANENT, "200"),
            ),
        ),
        AllocationEnvelopeGroup(
            label = "Épargne",
            envelopes = listOf(
                AllocationEnvelopeUiState("epargne", "Épargne", Lucide.Wallet, EnvelopeTypeEnum.SAVINGS, "500"),
            ),
        ),
        AllocationEnvelopeGroup(
            label = "Investissement",
            envelopes = listOf(
                AllocationEnvelopeUiState("etf", "ETF World", Lucide.ChartBar, EnvelopeTypeEnum.INVESTMENT, "300"),
            ),
        ),
    )
    FinanceOSTheme {
        AllocationScreen(
            state = AllocationUiState(
                step = 2,
                income = "4200",
                groups = groups,
                remaining = 49.0,
            ),
            onAction = {},
        )
    }
}
