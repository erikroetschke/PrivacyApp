package com.example.privacyapp.feature_PrivacyDashboard.domain.util

/**
 * Data class representing the activity status counters for an app.
 * @property foregroundCounter The counter for the number of foreground activities.
 * @property backgroundCounter The counter for the number of background activities.
 * @property serviceCounter The counter for the number of active services.
 */
data class AppStatus(
    val foregroundCounter: Int,
    val backgroundCounter: Int,
    val serviceCounter: Int
)