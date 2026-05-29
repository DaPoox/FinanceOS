package com.daprox.financeos.domain.model

/**
 * Enum for envelope (budget category) types.
 *
 * Categorizes envelopes by spending pattern and lifecycle:
 * - FIXED and VARIABLE describe expense patterns (predictable vs. fluctuating)
 * - MONTHLY and PERMANENT describe lifecycle (recurring monthly vs. always active)
 * - SAVINGS and INVESTMENT describe purpose (accumulation vs. growth)
 */
enum class EnvelopeTypeEnum {
    /** Fixed expense envelope — predictable, consistent monthly spending */
    FIXED,

    /** Variable expense envelope — unpredictable, fluctuating monthly spending */
    VARIABLE,

    /** Monthly budget envelope — resets at the start of each month */
    MONTHLY,

    /** Permanent budget envelope — carries balance across months */
    PERMANENT,

    /** Savings envelope — accumulates funds for future goals */
    SAVINGS,

    /** Investment envelope — funds allocated to investments (stocks, ETFs, crypto) */
    INVESTMENT,
}
