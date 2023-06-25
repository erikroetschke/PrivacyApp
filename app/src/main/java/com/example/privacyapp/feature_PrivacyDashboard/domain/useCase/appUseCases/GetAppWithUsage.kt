package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppAndAppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository

class GetAppWithUsage(
    private val repository: AppRepository
) {
    suspend operator fun invoke(packageName: String): List<AppAndAppUsage> {
        return repository.getAppWithUsage(packageName)
    }
}