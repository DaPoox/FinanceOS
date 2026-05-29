package com.daprox.financeos.data.manager

import android.content.SharedPreferences

/**
 * Manages first-launch tracking using SharedPreferences.
 *
 * Tracks whether the user has opened the app at least once, enabling the app
 * to show onboarding or setup screens on first launch.
 *
 * @property prefs SharedPreferences instance for persistent storage
 */
class FirstLaunchManager(private val prefs: SharedPreferences) {

  /**
   * Checks whether this is the user's first app launch.
   *
   * @return true if the app has not been launched before, false otherwise
   */
  val isFirstLaunch: Boolean
    get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

  /**
   * Marks the app as launched, preventing first-launch flows on future launches.
   *
   * Sets the first-launch flag to false in SharedPreferences.
   */
  fun markLaunched() {
    prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
  }

  private companion object {
    const val KEY_FIRST_LAUNCH = "is_first_launch"
  }
}
