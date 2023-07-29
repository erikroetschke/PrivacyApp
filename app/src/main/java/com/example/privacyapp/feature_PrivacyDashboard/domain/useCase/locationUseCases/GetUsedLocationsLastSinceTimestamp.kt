package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import java.util.Calendar

class GetUsedLocationsLastSinceTimestamp(
    private val repository: LocationRepository
) {

    suspend operator fun invoke(timestamp: Long): List<Location> {
        return repository.getUsedLocations(timestamp)
    }
}