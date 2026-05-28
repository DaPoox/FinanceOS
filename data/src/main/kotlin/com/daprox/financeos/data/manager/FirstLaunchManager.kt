package com.daprox.financeos.data.manager

import android.content.SharedPreferences

/** Tracks whether the user has launched the app at least once. */
class FirstLaunchManager(private val prefs: SharedPreferences) {

    val isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

    fun markLaunched() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    private companion object {
        const val KEY_FIRST_LAUNCH = "is_first_launch"
    }
}
