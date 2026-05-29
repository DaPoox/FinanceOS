package com.daprox.financeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.daprox.financeos.navigation.AppNavGraph
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme
import org.koin.androidx.compose.koinViewModel

/** Main activity. Entry point for Compose UI. Sets up edge-to-edge display and initializes first-launch flag. */
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val appViewModel: AppViewModel = koinViewModel()
      FinanceOSTheme {
        LaunchedEffect(Unit) {
          // Mark first launch as handled so it does not trigger again.
          if (appViewModel.isFirstLaunch) appViewModel.onLaunchHandled()
        }
        AppNavGraph()
      }
    }
  }
}
