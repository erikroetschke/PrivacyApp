package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for deleting locations older than a specified timestamp from the repository.
 *
 * @param repository The repository for managing locations.
 */
class DeleteLocationsOlderThanTimestamp(
    private val repository: LocationRepository
) {

    /**
     * Deletes locations older than the provided [timestamp] from the repository.
     *
     * @param timestamp The timestamp indicating the cutoff point for deletion.
     */
    suspend operator fun invoke(timestamp: Long) {
        repository.deleteLocationOlderThanTimestamp(timestamp)
    }
}