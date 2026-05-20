package com.daprox.financeos.core.extensions

import java.text.NumberFormat
import java.util.Locale

/** Formats a Long as a French-locale integer amount (e.g. 2 374). No € symbol. */
fun Long.frenchAmount(): String =
    NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 0
    }.format(this)

/** Convenience overload for Double — truncates to Long before formatting. */
fun Double.frenchAmount(): String = toLong().frenchAmount()
