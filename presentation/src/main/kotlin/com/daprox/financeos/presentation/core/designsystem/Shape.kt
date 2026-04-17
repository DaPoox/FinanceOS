package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Shape scale. The design spec calls for maximum, pill-shaped roundedness —
// the full scale leans toward fully rounded corners. ExtraSmall is the only
// "conservative" step; every larger size pushes toward pill territory.

internal val FinanceOSShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // chips, tooltips, small badges
    small      = RoundedCornerShape(12.dp),  // input fields, small cards
    medium     = RoundedCornerShape(16.dp),  // cards, dialogs, bottom sheets
    large      = RoundedCornerShape(24.dp),  // modals, drawers
    extraLarge = RoundedCornerShape(32.dp),  // full-bleed containers, FABs
)
