package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

class GetApps(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<List<App>> {
        return repository.getApps()
    }
}