package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving locations where the "locationUsed" property is null from the repository.
 *
 * @param repository The repository for managing locations.
 */
class GetLocationsWithLocationUsedIsNull(
    private val repository: LocationRepository
) {

    /**
     * Retrieves a list of locations where the "locationUsed" property is null from the repository.
     *
     * @return A list of locations.
     */
    suspend operator fun invoke(): List<Location> {
        return repository.getLocationsWithLocationUsedIsNull()
    }
}