package com.daprox.financeos.presentation.dashboard

import com.daprox.financeos.presentation.core.designsystem.component.DashboardTab

sealed interface DashboardUiAction {
    data class OnTabSelected(val tab: DashboardTab) : DashboardUiAction
}
