package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

sealed class DashboardEvent {

    data class ToggleTracking(val running: Boolean): DashboardEvent()

}
