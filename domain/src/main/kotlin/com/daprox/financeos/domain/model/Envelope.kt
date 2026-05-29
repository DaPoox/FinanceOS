package com.daprox.financeos.domain.model

/**
 * Represents a spending or savings envelope (category).
 *
 * An envelope is a budget category used to allocate funds and track spending.
 * Envelopes can be archived (inactive) but retained in the system for historical
 * reference without affecting active budget allocations.
 *
 * @property id Unique identifier for the envelope
 * @property name Display name of the envelope (e.g., "Groceries", "Entertainment")
 * @property type Category type (expense, income, savings) from [EnvelopeTypeEnum]
 * @property iconKey Key to resolve the icon asset (matched in design system)
 * @property colorHex Hex color code for UI display
 * @property isActive Whether the envelope is currently active; inactive envelopes are archived
 */
data class Envelope(
    val id: String,
    val name: String,
    val type: EnvelopeTypeEnum,
    val iconKey: String,
    val colorHex: String,
    val isActive: Boolean = true,
)
