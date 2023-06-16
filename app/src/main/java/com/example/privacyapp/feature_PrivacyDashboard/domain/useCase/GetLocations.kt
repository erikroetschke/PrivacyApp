package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetLocations(
    private val repository: LocationRepository
) {

    operator fun invoke(): Flow<List<Location>> {
        return repository.getLocations()
    }
}