package com.daprox.financeos.presentation.allocation

/**
 * Enum for template selection in Allocation Step 2.
 *
 * Defines the available allocation templates a user can choose from:
 * - [PREVIOUS]: Copy allocations from the previous month (recommended)
 * - [PAST]: Copy allocations from a custom past month
 * - [DEFAULT]: Use the user's saved default template
 * - [SCRATCH]: Start from scratch with all amounts at zero
 */
enum class TemplateTypeEnum { PREVIOUS, PAST, DEFAULT, SCRATCH }
