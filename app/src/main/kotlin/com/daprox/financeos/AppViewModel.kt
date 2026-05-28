package com.daprox.financeos

import androidx.lifecycle.ViewModel
import com.daprox.financeos.data.manager.FirstLaunchManager

/** App-level ViewModel. Tracks first-launch state and marks it as handled. */
class AppViewModel(
    private val firstLaunchManager: FirstLaunchManager,
) : ViewModel() {

    val isFirstLaunch: Boolean = firstLaunchManager.isFirstLaunch

    fun onLaunchHandled() = firstLaunchManager.markLaunched()
}
