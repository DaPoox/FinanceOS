package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Bus
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.Circle
import com.composables.icons.lucide.Dumbbell
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.HeartPulse
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PiggyBank
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.ShoppingBag
import com.composables.icons.lucide.ShoppingBasket
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.Smartphone
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Umbrella
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.Zap

fun iconKeyToImageVector(key: String): ImageVector = when (key) {
    "house"            -> Lucide.House
    "car"              -> Lucide.Car
    "zap"              -> Lucide.Zap
    "shopping_basket"  -> Lucide.ShoppingBasket
    "shopping_bag"     -> Lucide.ShoppingBag
    "shopping_cart"    -> Lucide.ShoppingCart
    "utensils"         -> Lucide.Utensils
    "bus"              -> Lucide.Bus
    "plane"            -> Lucide.Plane
    "heart_pulse"      -> Lucide.HeartPulse
    "dumbbell"         -> Lucide.Dumbbell
    "smartphone"       -> Lucide.Smartphone
    "shield"           -> Lucide.Shield
    "umbrella"         -> Lucide.Umbrella
    "gift"             -> Lucide.Gift
    "piggy_bank"       -> Lucide.PiggyBank
    "trending_up"      -> Lucide.TrendingUp
    "wallet"           -> Lucide.Wallet
    "chart_bar"        -> Lucide.ChartBar
    else               -> Lucide.Circle
}

/** All envelope icon options shown in EnvelopeFormScreen's icon picker. */
val ENVELOPE_ICON_KEYS = listOf(
    "house", "car", "zap", "shopping_basket",
    "utensils", "shopping_bag", "bus", "plane",
    "heart_pulse", "dumbbell", "smartphone", "shield",
    "umbrella", "gift", "piggy_bank", "trending_up",
)
