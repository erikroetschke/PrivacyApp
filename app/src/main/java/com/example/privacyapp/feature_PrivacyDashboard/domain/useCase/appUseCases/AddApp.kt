package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for adding an app.
 *
 * @param repository The repository for managing apps.
 */
class AddApp(
    private val repository: AppRepository
) {

    /**
     * Adds an app to the repository.
     *
     * @param app The app to be added.
     */
    suspend operator fun invoke(app: App) {
        return repository.insertApp(app)
    }
}