package com.example.privacyapp.feature_PrivacyDashboard.domain.util

/**
 * Represents a usage event with timestamp, event type, and associated package name.
 *
 * @param timeStamp The timestamp of the usage event.
 * @param eventType The type of the usage event.
 * @param packageName The package name associated with the usage event.
 */
data class UsageEvent(
    val timeStamp: Long,
    val eventType: Int,
    val packageName: String
)
