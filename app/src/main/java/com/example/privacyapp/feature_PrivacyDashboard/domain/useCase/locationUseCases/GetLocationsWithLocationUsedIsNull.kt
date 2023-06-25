package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetLocationsWithLocationUsedIsNull(
    private val repository: LocationRepository
) {

    suspend operator fun invoke(): List<Location> {
        return repository.getLocationsWithLocationUsedIsNull()
    }
}