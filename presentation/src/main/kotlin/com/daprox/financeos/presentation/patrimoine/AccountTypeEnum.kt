package com.daprox.financeos.presentation.patrimoine

/**
 * Presentation-layer account type enumeration.
 *
 * Mirrors domain model account types for UI display and filtering.
 */
enum class AccountTypeEnum {
  /** Checking account (compte courant) — day-to-day spending. */
  COURANT,

  /** Savings account (livret) — regulated savings with interest. */
  EPARGNE,

  /** Investment account (brokerage) — stocks, ETFs, funds. */
  INVESTISSEMENT,
}
