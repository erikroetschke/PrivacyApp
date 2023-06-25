package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder

sealed class AppsEvent {
    data class Order(val appOrder: AppOrder): AppsEvent()
    object ToggleOrderSection: AppsEvent()
}
