package com.daprox.financeos.core

/**
 * Abstracts string representation so ViewModels never depend on Android Context.
 * Resolved in Composables via UiText.asString(context).
 */
sealed class UiText {
  /**
   * For localized strings — pass R.string.xxx (plain Int to keep :core Android-free).
   *
   * @param id String resource ID from R.string.
   */
  data class StringResource(val id: Int) : UiText()

  /**
   * For dynamic content: server messages, computed values, exception messages.
   *
   * @param value The string to display.
   */
  data class Dynamic(val value: String) : UiText()
}
