package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppPermissionFilter

/**
 * A sealed class representing events that can occur related to app ordering and interactions.
 */
sealed class AppsEvent {
    /**
     * Represents an event to change the order of apps based on a specific order type.
     *
     * @param appOrder The order type for apps.
     */
    data class Order(val appOrder: AppOrder): AppsEvent()

    /**
     * Represents an event to change the filter of apps.
     *
     * @param filter The Filter for apps.
     */
    data class Filter(val filter: AppPermissionFilter): AppsEvent()

    /**
     * Represents an event to toggle the visibility of the order section.
     */
    object ToggleOrderSection: AppsEvent()
}

