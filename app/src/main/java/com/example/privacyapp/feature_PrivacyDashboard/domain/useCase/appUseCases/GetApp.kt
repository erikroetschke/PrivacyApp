package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository

/**
 * Use case for retrieving an app by package name.
 *
 * @param repository The repository for managing apps.
 */
class GetApp(
    private val repository: AppRepository
) {

    /**
     * Retrieves an app with the specified package name from the repository.
     *
     * @param packageName The package name of the app to retrieve.
     * @return The retrieved app, or null if not found.
     */
    suspend operator fun invoke(packageName: String): App? {
        return repository.getAppByName(packageName)
    }
}