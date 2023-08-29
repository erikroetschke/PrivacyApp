package com.example.privacyapp.feature_PrivacyDashboard.domain.util

data class AppPermissionFilter(
    val none: Boolean,
    val coarseLocation: Boolean,
    val fineLocation: Boolean,
    val backgroundLocation: Boolean
)
