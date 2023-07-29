package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

class AddApp(
    private val repository: AppRepository
) {

    suspend operator fun invoke(app: App) {
        return repository.insertApp(app)
    }
}