package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.AddApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.DeleteAllApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.DeleteApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetFavoriteApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.InitApps

data class AppUseCases(
    val getApps: GetApps,
    val addApp: AddApp,
    val deleteAllApps: DeleteAllApps,
    val initApps: InitApps,
    val getApp: GetApp,
    val getFavoriteApps: GetFavoriteApps,
    val deleteApp: DeleteApp
)
