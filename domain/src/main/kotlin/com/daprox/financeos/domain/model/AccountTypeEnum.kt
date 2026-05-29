package com.daprox.financeos.domain.model

/**
 * Enum for account types in FinanceOS.
 *
 * Categorizes accounts by their purpose and liquidity profile.
 */
enum class AccountTypeEnum {
    /** Current/Checking account — immediate access to funds */
    COURANT,

    /** Savings account — funds reserved for savings goals */
    EPARGNE,

    /** Investment account — funds invested in securities (ETFs, stocks, etc.) */
    INVESTISSEMENT,
}
