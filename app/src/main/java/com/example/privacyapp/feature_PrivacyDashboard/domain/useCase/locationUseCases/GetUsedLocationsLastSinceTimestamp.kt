package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import java.util.Calendar

/**
 * Use case for retrieving used locations since a specified timestamp from the repository.
 *
 * @param repository The repository for managing locations.
 */
class GetUsedLocationsLastSinceTimestamp(
    private val repository: LocationRepository
) {

    /**
     * Retrieves a list of used locations since the provided [timestamp] from the repository.
     *
     * @param timestamp The timestamp indicating the starting point for retrieving used locations.
     * @return A list of used locations.
     */
    suspend operator fun invoke(timestamp: Long): List<Location> {
        return repository.getUsedLocations(timestamp)
    }
}