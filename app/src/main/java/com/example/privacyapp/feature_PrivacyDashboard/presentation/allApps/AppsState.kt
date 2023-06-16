package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App

data class AppsState(
    val apps: List<App> = emptyList()
)
