package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.AddApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.DeleteAllApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.DeleteApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetApp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetAppsSuspend
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.GetFavoriteApps
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases.InitApps

/**
 * Encapsulates the use cases related to app management.
 *
 * @see GetApps
 * @see AddApp
 * @see DeleteAllApps
 * @see InitApps
 * @see GetApp
 * @see GetFavoriteApps
 * @see DeleteApp
 * @see GetAppsSuspend
 */
data class AppUseCases(
    val getApps: GetApps,
    val addApp: AddApp,
    val deleteAllApps: DeleteAllApps,
    val initApps: InitApps,
    val getApp: GetApp,
    val getFavoriteApps: GetFavoriteApps,
    val deleteApp: DeleteApp,
    val getAppsSuspend: GetAppsSuspend
)
