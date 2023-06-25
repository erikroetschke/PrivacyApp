package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository

class AddLocation(
    private val repository: LocationRepository
) {

    suspend operator fun invoke(location: Location) {
        repository.insertLocation(location)
    }
}