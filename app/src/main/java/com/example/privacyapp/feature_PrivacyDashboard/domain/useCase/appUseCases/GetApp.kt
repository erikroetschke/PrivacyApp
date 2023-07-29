package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository

class GetApp(
    private val repository: AppRepository
) {
    suspend operator fun invoke(packageName: String): App? {
        return repository.getAppByName(packageName)
    }
}