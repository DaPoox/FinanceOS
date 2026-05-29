package com.daprox.financeos.presentation.dashboard.component.envelopeminigrid

/**
 * Envelope budget type for classification and UI styling.
 * FIXED: recurring (e.g., rent). VARIABLE: spending envelope. MONTHLY: monthly reset.
 * PERMANENT: always-on. SAVINGS: savings goal. INVESTMENT: investment account.
 */
enum class EnvelopeTypeEnum { FIXED, VARIABLE, MONTHLY, PERMANENT, SAVINGS, INVESTMENT }
