package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class DeleteLocationsOlderThanTimestamp(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(timestamp: Long) {
        return repository.deleteLocationOlderThanTimestamp(timestamp)
    }

}