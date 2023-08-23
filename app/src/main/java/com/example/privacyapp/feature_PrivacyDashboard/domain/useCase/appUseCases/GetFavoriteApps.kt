package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoriteApps(private val repository: AppRepository) {

    operator fun invoke(): Flow<List<App>> {
        return repository.getFavoriteApps().map { apps -> apps.sortedBy { it.numberOfEstimatedRequests }
        }
    }
}