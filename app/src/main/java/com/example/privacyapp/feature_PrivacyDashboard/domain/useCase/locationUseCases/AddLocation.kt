package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository

/**
 * Use case for adding a new location to the repository.
 *
 * @param repository The repository for managing locations.
 */
class AddLocation(
    private val repository: LocationRepository
) {

    /**
     * Adds a new location to the repository.
     *
     * @param location The location to be added.
     */
    suspend operator fun invoke(location: Location) {
        repository.insertLocation(location)
    }
}