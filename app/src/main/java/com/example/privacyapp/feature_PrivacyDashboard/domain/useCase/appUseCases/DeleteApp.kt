package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository

/**
 * Use case for deleting an app.
 *
 * @param repository The repository for managing apps.
 */
class DeleteApp(
    private val repository: AppRepository
) {

    /**
     * Deletes the specified app from the repository.
     *
     * @param app The app to be deleted.
     */
    suspend operator fun invoke(app: App) {
        return repository.deleteApp(app)
    }
}
