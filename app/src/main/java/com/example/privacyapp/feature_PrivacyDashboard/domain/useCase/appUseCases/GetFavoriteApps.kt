package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Use case for retrieving favorite apps with sorting based on the number of estimated requests.
 *
 * @param repository The repository for managing apps.
 */
class GetFavoriteApps(private val repository: AppRepository) {

    /**
     * Retrieves a flow of favorite apps sorted by the number of estimated requests.
     *
     * @return A flow of favorite apps sorted by number of estimated requests.
     */
    operator fun invoke(): Flow<List<App>> {
        return repository.getFavoriteApps().map { apps -> apps.sortedBy { it.numberOfEstimatedRequests }
        }
    }
}