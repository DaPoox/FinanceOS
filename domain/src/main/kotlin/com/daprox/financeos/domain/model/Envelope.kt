package com.daprox.financeos.domain.model

data class Envelope(
    val id: String,
    val name: String,
    val type: EnvelopeTypeEnum,
    val iconKey: String,
    val colorHex: String,
    val isActive: Boolean = true,
)
