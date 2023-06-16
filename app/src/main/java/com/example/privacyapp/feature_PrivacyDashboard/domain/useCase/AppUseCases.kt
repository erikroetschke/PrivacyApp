package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

data class AppUseCases(
    val getApps: GetApps,
    val addApp: AddApp,
    val deleteAllApps: DeleteAllApps,
    val initApps: InitApps
)
