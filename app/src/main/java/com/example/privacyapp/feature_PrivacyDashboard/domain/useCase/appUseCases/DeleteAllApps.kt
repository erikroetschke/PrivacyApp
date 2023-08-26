package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository

/**
 * Use case for deleting all apps.
 *
 * @param repository The repository for managing apps.
 */
class DeleteAllApps(
    private val repository: AppRepository
) {

    /**
     * Deletes all apps from the repository.
     */
    suspend operator fun invoke() {
        return repository.deleteAllApps()
    }
}
