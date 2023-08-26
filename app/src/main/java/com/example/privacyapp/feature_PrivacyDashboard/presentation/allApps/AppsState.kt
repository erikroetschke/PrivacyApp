package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType

/**
 * Represents the state of the apps screen, including the list of apps, app ordering, and the visibility
 * status of the order section.
 *
 * @param apps The list of apps to be displayed.
 * @param appOrder The order in which apps are sorted.
 * @param isOrderSectionVisible Indicates whether the order section is visible or not.
 */
data class AppsState(
    val apps: List<App> = emptyList(),
    val appOrder: AppOrder = AppOrder.LocationUsage(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)
