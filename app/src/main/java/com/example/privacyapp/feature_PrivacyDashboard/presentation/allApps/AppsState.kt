package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppPermissionFilter
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType

/**
 * Represents the state of the apps screen, including the list of apps, app ordering and filtering, and the visibility
 * status of the order section.
 *
 * @param apps The list of apps to be displayed.
 * @param appOrder The order in which apps are sorted.
 * @param appFilter The filter to be applied to the list of apps.
 * @param isOrderSectionVisible Indicates whether the order section is visible or not.
 */
data class AppsState(
    val apps: List<App> = emptyList(),
    val appOrder: AppOrder = AppOrder.LocationUsage(OrderType.Descending),
    val appFilter: AppPermissionFilter = AppPermissionFilter(
        none = false,
        coarseLocation = false,
        fineLocation = false,
        backgroundLocation = false
    ),
    val isOrderSectionVisible: Boolean = false
)
