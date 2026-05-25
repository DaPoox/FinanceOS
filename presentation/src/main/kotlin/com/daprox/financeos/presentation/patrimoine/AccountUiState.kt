package com.daprox.financeos.presentation.patrimoine

import androidx.compose.ui.graphics.Color

data class AccountUiState(
    val name: String,
    val type: AccountTypeEnum,
    val balance: Double,
    val cap: Double? = null,
    val color: Color,
)
