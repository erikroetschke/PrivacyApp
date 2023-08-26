package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all stored locations from the repository.
 *
 * @param repository The repository for managing locations.
 */
class GetLocations(
    private val repository: LocationRepository
) {

    /**
     * Retrieves a flow of all stored locations from the repository.
     *
     * @return A flow of locations.
     */
    operator fun invoke(): Flow<List<Location>> {
        return repository.getLocations()
    }
}