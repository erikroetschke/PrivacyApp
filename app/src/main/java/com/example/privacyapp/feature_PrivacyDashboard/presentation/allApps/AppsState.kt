package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType

data class AppsState(
    val apps: List<App> = emptyList(),
    val appOrder: AppOrder = AppOrder.LocationUsage(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)
