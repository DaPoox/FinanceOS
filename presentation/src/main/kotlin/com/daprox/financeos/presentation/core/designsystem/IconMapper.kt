package com.daprox.financeos.presentation.core.designsystem

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.Circle
import com.composables.icons.lucide.Gift
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PiggyBank
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wallet

fun iconKeyToImageVector(key: String): ImageVector = when (key) {
    "house" -> Lucide.House
    "shopping_cart" -> Lucide.ShoppingCart
    "utensils" -> Lucide.Utensils
    "plane" -> Lucide.Plane
    "trending_up" -> Lucide.TrendingUp
    "wallet" -> Lucide.Wallet
    "chart_bar" -> Lucide.ChartBar
    "piggy_bank" -> Lucide.PiggyBank
    "car" -> Lucide.Car
    "gift" -> Lucide.Gift
    else -> Lucide.Circle
}
