package com.daprox.financeos.presentation.allocation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.daprox.financeos.core.extensions.frenchAmount
import com.daprox.financeos.presentation.allocation.newenvelope.NewEnvelopeSheet
import com.daprox.financeos.presentation.core.ObserveAsEvents
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import com.daprox.financeos.presentation.core.designsystem.GeistMono
import com.daprox.financeos.presentation.core.designsystem.component.ErrorStateView
import com.daprox.financeos.presentation.core.designsystem.component.ShimmerBox
import com.daprox.financeos.presentation.core.designsystem.finColors
import com.daprox.financeos.presentation.dashboard.component.envelopeminigrid.EnvelopeTypeEnum
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

/**
 * Returns the appropriate add button label for a given envelope type.
 *
 * @param type The envelope type to get the label for
 * @return The localized add button label, or null if the type doesn't support quick creation
 */
private fun addButtonLabel(type: EnvelopeTypeEnum): String = when (type) {
    EnvelopeTypeEnum.FIXED -> "+ Nouvelle charge fixe"
    EnvelopeTypeEnum.VARIABLE -> "+ Ajouter une catégorie"
    EnvelopeTypeEnum.MONTHLY -> "+ Nouvelle dépense du mois"
    EnvelopeTypeEnum.PERMANENT -> "+ Nouvel objectif"
    EnvelopeTypeEnum.SAVINGS -> "+ Nouveau livret"
    EnvelopeTypeEnum.INVESTMENT -> "+ Nouveau placement"
    else -> "+ Nouvelle enveloppe"
}

private fun groupLabel(type: EnvelopeTypeEnum): String = when (type) {
    EnvelopeTypeEnum.FIXED -> "Fixes"
    EnvelopeTypeEnum.VARIABLE -> "Variables"
    EnvelopeTypeEnum.MONTHLY -> "Du mois"
    EnvelopeTypeEnum.PERMANENT -> "Permanentes"
    EnvelopeTypeEnum.SAVINGS -> "Épargne"
    EnvelopeTypeEnum.INVESTMENT -> "Investissement"
    else -> type.name
}

private val ALL_GROUP_TYPES = listOf(
    EnvelopeTypeEnum.FIXED,
    EnvelopeTypeEnum.VARIABLE,
    EnvelopeTypeEnum.MONTHLY,
    EnvelopeTypeEnum.PERMANENT,
    EnvelopeTypeEnum.SAVINGS,
    EnvelopeTypeEnum.INVESTMENT,
)

/**
 * Root composable for the Allocation screen.
 *
 * Manages ViewModel injection, event observation, and navigation callbacks.
 * Displays the [AllocationScreen] with collected state.
 *
 * @param viewModel The [AllocationViewModel] providing state and action handling
 * @param onNavigateBack Callback invoked when navigation back is requested
 */
@Composable
fun AllocationScreenRoot(
    viewModel: AllocationViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AllocationUiEvent.NavigateBack -> onNavigateBack()
            is AllocationUiEvent.ShowError -> Unit
        }
    }

    AllocationScreen(state = state, onAction = viewModel::onAction)
}

/**
 * Main Allocation screen composable.
 *
 * A three-step wizard for monthly budget allocation:
 * - Step 0: Income input
 * - Step 1: Template selection (Previous month, Past month, Default, From scratch)
 * - Step 2: Envelope amount adjustment with swipe-to-delete undo support
 *
 * Displays loading skeleton, error state, or the current step's content.
 * Shows undo snackbar when an envelope is swipe-deleted.
 * Renders the new envelope bottom sheet when triggered.
 *
 * @param state The current [AllocationUiState]
 * @param onAction Callback to dispatch [AllocationUiAction]s
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationScreen(
    state: AllocationUiState,
    onAction: (AllocationUiAction) -> Unit,
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show undo snackbar whenever an envelope is swipe-deleted
    LaunchedEffect(state.lastRemovedEnvelope) {
        val removed = state.lastRemovedEnvelope ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "\"${removed.name}\" supprimé",
            actionLabel = "Annuler",
            duration = SnackbarDuration.Short,
        )
        when (result) {
            SnackbarResult.ActionPerformed -> onAction(AllocationUiAction.OnEnvelopeRestored)
            SnackbarResult.Dismissed -> onAction(AllocationUiAction.OnClearRemovedEnvelope)
        }
    }

    // 2-step flow for first month (income → crée), 3-step for recurring (income → template → ajuste)
    val steps = if (state.isFirstMonth) listOf("income", "adjust") else listOf("income", "template", "adjust")
    val currentStepName = steps.getOrElse(state.step) { "income" }
    val totalSteps = steps.size
    val isAdjustStep = currentStepName == "adjust"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AllocationTopBar(
                monthLabel = state.monthLabel,
                step = state.step,
                totalSteps = totalSteps,
                onBack = { onAction(AllocationUiAction.OnBack) },
            )
        },
        bottomBar = {
            AllocationFooter(
                isAdjustStep = isAdjustStep,
                remaining = state.remaining,
                incomeBlank = state.income.isBlank(),
                onNext = { onAction(AllocationUiAction.OnNext) },
                navBarPadding = navBarPadding.calculateBottomPadding(),
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> AllocationScreenSkeleton(modifier = Modifier.padding(innerPadding))
            state.isError -> ErrorStateView(
                onRetry = { onAction(AllocationUiAction.OnRetry) },
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            )
            else -> Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                when (currentStepName) {
                    "income" -> StepIncome(
                        income = state.income,
                        isFirstMonth = state.isFirstMonth,
                        stepIndex = state.step,
                        totalSteps = totalSteps,
                        onIncomeChanged = { onAction(AllocationUiAction.OnIncomeChanged(it)) },
                    )
                    "template" -> StepTemplate(
                        selected = state.selectedTemplate,
                        stepIndex = state.step,
                        totalSteps = totalSteps,
                        onSelect = { onAction(AllocationUiAction.OnTemplateSelected(it)) },
                    )
                    "adjust" -> StepAdjust(
                        groups = state.groups,
                        isFirstMonth = state.isFirstMonth,
                        stepIndex = state.step,
                        totalSteps = totalSteps,
                        onAmountChanged = { id, amt -> onAction(AllocationUiAction.OnEnvelopeAmountChanged(id, amt)) },
                        onEnvelopeDeleted = { env -> onAction(AllocationUiAction.OnEnvelopeDeleted(env)) },
                        onAddEnvelopeClick = { typeKey -> onAction(AllocationUiAction.OnAddEnvelopeClick(typeKey)) },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // New envelope bottom sheet — rendered above Scaffold
    if (state.isNewEnvelopeSheetVisible) {
        NewEnvelopeSheet(
            presetTypeKey = state.newEnvelopePresetType,
            onDismiss = { onAction(AllocationUiAction.OnNewEnvelopeDismiss) },
            onSave = { name, typeKey, iconKey, amount ->
                onAction(AllocationUiAction.OnNewEnvelopeSaved(name, typeKey, iconKey, amount))
            },
        )
    }
}

/**
 * Loading skeleton placeholder for the Allocation screen.
 *
 * Displays shimmer boxes while data is being loaded.
 *
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun AllocationScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp))
        repeat(3) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(56.dp))
        }
    }
}

/**
 * Top bar for the Allocation screen.
 *
 * Displays the back button, screen title with month label, and step indicator (3 animated dots).
 *
 * @param monthLabel The localized month label (e.g., "Mai 2026")
 * @param step The current step (0–2)
 * @param onBack Callback invoked when the back button is clicked
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun AllocationTopBar(
    monthLabel: String,
    step: Int,
    totalSteps: Int,
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

        StepIndicator(step = step, total = totalSteps)
    }
}

/**
 * Animated step indicator with 3 dots.
 *
 * The current step's dot expands; completed steps maintain width; future steps remain compact.
 *
 * @param step The current step (0–2)
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun StepIndicator(step: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (0 until total).forEach { i ->
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

/**
 * Step 1: Income input screen.
 *
 * Allows users to enter their monthly income with a large text field, quick suggestion buttons,
 * and a decorative gradient underline.
 *
 * @param income The current income value as a string
 * @param onIncomeChanged Callback invoked when income is updated; filters to digits only
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun StepIncome(
    income: String,
    isFirstMonth: Boolean,
    stepIndex: Int,
    totalSteps: Int,
    onIncomeChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val suggestions = listOf("4000" to "4 000", "4200" to "4 200", "4500" to "4 500", "5000" to "5 000")

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Étape ${stepIndex + 1} sur $totalSteps",
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

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (isFirstMonth)
                "Tu créeras tes premières enveloppes à l'étape suivante.\nAucune donnée n'est pré-remplie — tu pars de zéro."
            else if (totalSteps == 3)
                "Tu choisiras ta base d'allocation à l'étape suivante.\nTu pourras ensuite ajuster chaque enveloppe."
            else
                "Ton allocation est pré-remplie depuis le mois précédent.\nTu pourras ajuster ou retirer des enveloppes à l'étape suivante.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Step 2: Template selection screen.
 *
 * Allows users to choose a starting point for their allocation:
 * - Previous month (recommended)
 * - A past month (custom selection)
 * - Default template (user's saved default)
 * - From scratch
 *
 * @param selected The currently selected [TemplateTypeEnum]
 * @param onSelect Callback invoked when a template is selected
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun StepTemplate(
    selected: TemplateTypeEnum,
    stepIndex: Int,
    totalSteps: Int,
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
        TemplateOption(TemplateTypeEnum.PREVIOUS, "Mois précédent", "Mois précédent · recommandé", "Conseillé"),
        TemplateOption(TemplateTypeEnum.PAST, "Un mois passé", "Choisir n'importe quel mois"),
        TemplateOption(TemplateTypeEnum.DEFAULT, "Template par défaut", "Ta config sauvegardée"),
        TemplateOption(TemplateTypeEnum.SCRATCH, "From scratch", "Tout à zéro"),
    )

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Étape ${stepIndex + 1} sur $totalSteps",
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

/**
 * Step 3: Envelope adjustment screen.
 *
 * Displays envelopes grouped by type with collapsible sections. Users can:
 * - Edit envelope amounts
 * - Swipe left to delete (with undo snackbar support)
 * - Add new envelopes to supported types
 * - Toggle "Expand All / Collapse All"
 *
 * @param groups The list of [AllocationEnvelopeGroup]s to display
 * @param onAmountChanged Callback for envelope amount changes; parameters are envelope ID and new amount
 * @param onEnvelopeDeleted Callback invoked when an envelope is swipe-deleted
 * @param onAddEnvelopeClick Callback invoked when the add envelope button is clicked; parameter is envelope type key
 * @param modifier Optional modifier for layout customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepAdjust(
    groups: List<AllocationEnvelopeGroup>,
    isFirstMonth: Boolean,
    stepIndex: Int,
    totalSteps: Int,
    onAmountChanged: (String, String) -> Unit,
    onEnvelopeDeleted: (AllocationEnvelopeUiState) -> Unit,
    onAddEnvelopeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Build a flat map from type → envelopes for fast lookup
    val envelopesByType: Map<EnvelopeTypeEnum, List<AllocationEnvelopeUiState>> =
        groups.flatMap { it.envelopes }.groupBy { it.type }

    // First month → all groups open so every AddEnvelopeRow is visible.
    // Recurring → only "Du mois" open by default.
    val expandedMap = remember {
        mutableStateMapOf<EnvelopeTypeEnum, Boolean>().apply {
            ALL_GROUP_TYPES.forEach { t -> put(t, isFirstMonth || t == EnvelopeTypeEnum.MONTHLY) }
        }
    }
    val allExpanded = ALL_GROUP_TYPES.all { expandedMap[it] == true }

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Étape ${stepIndex + 1} sur $totalSteps",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = if (isFirstMonth) "Crée tes enveloppes" else "Ajuste tes enveloppes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (allExpanded) "Tout replier" else "Tout déplier",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { ALL_GROUP_TYPES.forEach { expandedMap[it] = !allExpanded } }
                    .padding(start = 8.dp, bottom = 2.dp),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isFirstMonth)
                "Ajoute tes premières enveloppes pour répartir ton revenu. Commence par tes charges fixes (loyer, abonnements)."
            else
                "Ajuste les montants ou retire une enveloppe de ce mois.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        ALL_GROUP_TYPES.forEach { type ->
            val envelopes = envelopesByType[type] ?: emptyList()
            val expanded = expandedMap[type] ?: false
            val groupTotal = envelopes.sumOf { it.amount.toLongOrNull() ?: 0L }

            Spacer(modifier = Modifier.height(18.dp))

            CollapsableGroupHeader(
                label = groupLabel(type),
                count = envelopes.size,
                total = groupTotal,
                expanded = expanded,
                onToggle = { expandedMap[type] = !expanded },
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)),
                exit = shrinkVertically(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                    fadeOut(animationSpec = tween(280, easing = FastOutSlowInEasing)),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    envelopes.forEach { envelope ->
                        SwipeableAdjustRow(
                            envelope = envelope,
                            onAmountChanged = { onAmountChanged(envelope.id, it) },
                            onDelete = { onEnvelopeDeleted(envelope) },
                        )
                    }
                    AddGroupButton(
                        label = addButtonLabel(type),
                        onClick = { onAddEnvelopeClick(type.name) },
                    )
                }
            }
        }
    }
}

/**
 * AdjustRow wrapped in SwipeToDismissBox. Swiping left reveals a red delete background.
 * The item is removed via [onDelete]; the snackbar in AllocationScreen offers undo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableAdjustRow(
    envelope: AllocationEnvelopeUiState,
    onAmountChanged: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            // Return false so the item snaps back — the list re-renders without it from state
            false
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            // Red delete indicator shown during left swipe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(18.dp),
                )
            }
        },
    ) {
        AdjustRow(
            envelope = envelope,
            onAmountChanged = onAmountChanged,
            onDelete = onDelete,
        )
    }
}

/**
 * Header for a collapsible envelope group.
 *
 * Displays the group label, envelope count, total amount, and an animated chevron icon.
 * Clickable to toggle the group's expanded state.
 *
 * @param label The group label (e.g., "VARIABLES")
 * @param count Number of envelopes in this group
 * @param total Sum of allocated amounts for all envelopes in the group
 * @param expanded Whether the group is currently expanded
 * @param onToggle Callback invoked when the header is clicked
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun CollapsableGroupHeader(
    label: String,
    count: Int,
    total: Long,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(300),
        label = "chevron_$label",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(14.dp)
                .rotate(rotation),
        )
        Text(
            text = "${label.uppercase()} · $count",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${total.frenchAmount()} €",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = GeistMono,
                fontSize = 13.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Button to add a new envelope to a group.
 *
 * Displayed at the bottom of an expanded envelope group.
 * Features a dashed border and the appropriate label for the envelope type.
 *
 * @param label The button label (e.g., "+ Ajouter une catégorie")
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun AddGroupButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val borderColor = primary.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
                    ),
                )
            }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = primary,
        )
    }
}

/**
 * A single envelope row in the adjustment step.
 *
 * Displays the envelope icon, name, allocated amount input field, and a delete button.
 * The row is contained in a surface with border.
 *
 * @param envelope The [AllocationEnvelopeUiState] to display
 * @param onAmountChanged Callback invoked when the amount field is updated
 * @param onDelete Callback invoked when the delete button is clicked
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun AdjustRow(
    envelope: AllocationEnvelopeUiState,
    onAmountChanged: (String) -> Unit,
    onDelete: () -> Unit,
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

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = Lucide.Trash2,
                contentDescription = "Supprimer",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

/**
 * Bottom footer bar for the Allocation screen.
 *
 * On Step 3, displays "Non alloué" (unallocated amount) with color coding:
 * - Green for surplus (allocated < income)
 * - Orange for warning (allocated ≈ income)
 * - Red for deficit (allocated > income)
 *
 * Always shows the primary action button ("Continuer" or "Valider l'allocation").
 *
 * @param step The current step (0–2)
 * @param remaining The remaining (unallocated) amount
 * @param incomeBlank Whether the income field is empty (disables the button on Step 0)
 * @param onNext Callback invoked when the main action button is clicked
 * @param navBarPadding Padding to accommodate the system navigation bar
 * @param modifier Optional modifier for layout customization
 */
@Composable
private fun AllocationFooter(
    isAdjustStep: Boolean,
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
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp),
    ) {
        if (isAdjustStep) {
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
            enabled = if (isAdjustStep) remaining <= 0.0 else !incomeBlank,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        ) {
            Text(
                text = if (!isAdjustStep) "Continuer" else "Valider l'allocation",
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
            state = AllocationUiState(isLoading = false, step = 0, income = "4200"),
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
                isLoading = false,
                step = 2,
                income = "4200",
                groups = groups,
                remaining = 49.0,
            ),
            onAction = {},
        )
    }
}
