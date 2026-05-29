package com.daprox.financeos.presentation.patrimoine

import androidx.compose.ui.graphics.Color

/**
 * UI model for an account row in the patrimoine screen.
 *
 * Represents a single account with its type, balance, optional cap, and display color.
 *
 * @property id unique account identifier
 * @property name account display name
 * @property type account classification (COURANT, EPARGNE, INVESTISSEMENT)
 * @property balance current account balance
 * @property cap maximum balance (EPARGNE only, e.g. Livret A max)
 * @property color background color for avatar
 */
data class AccountUiState(
    val id: String,
    val name: String,
    val type: AccountTypeEnum,
    val balance: Double,
    val cap: Double? = null,
    val color: Color,
)
