package com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails

/**
 * Sealed class representing events that can be triggered within the App Details screen.
 */
sealed class AppDetailsEvent {
    /**
     * Event indicating that the favorability status of the app should be toggled.
     */
    object Favor: AppDetailsEvent()

    /**
     * Event indicating that the active status of the app should be toggled.
     */
    object ToggleActive: AppDetailsEvent()
}
