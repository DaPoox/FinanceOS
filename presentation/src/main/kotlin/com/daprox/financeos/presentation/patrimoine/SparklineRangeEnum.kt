package com.daprox.financeos.presentation.patrimoine

/**
 * Chart range selector for the patrimoine sparkline.
 *
 * Determines which time period's data to display in the net worth trend chart.
 */
enum class SparklineRangeEnum {
  /** Last 6 months. */
  M6,

  /** Last 12 months. */
  M12,

  /** Last 3 years. */
  Y3,
}
