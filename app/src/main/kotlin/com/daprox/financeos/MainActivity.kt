package com.daprox.financeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.daprox.financeos.navigation.AppNavGraph
import com.daprox.financeos.presentation.core.designsystem.FinanceOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceOSTheme {
                AppNavGraph()
            }
        }
    }
}
