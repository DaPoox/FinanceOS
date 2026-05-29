package com.daprox.financeos

import androidx.lifecycle.ViewModel
import com.daprox.financeos.data.manager.FirstLaunchManager

/** App-level ViewModel. Tracks first-launch state and marks it as handled. */
class AppViewModel(
  private val firstLaunchManager: FirstLaunchManager,
) : ViewModel() {

  /** Whether this is the user's first launch of the app. Read from SharedPrefs. */
  val isFirstLaunch: Boolean = firstLaunchManager.isFirstLaunch

  /** Mark first launch as handled so it will not trigger again on subsequent launches. */
  fun onLaunchHandled() = firstLaunchManager.markLaunched()
}
