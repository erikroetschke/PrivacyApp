package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import kotlinx.coroutines.flow.Flow

class GetPOISinceTimestampAsFlow(
    private val poiRepository: POIRepository
) {

    operator fun invoke(timestamp: Long): Flow<List<POI>> {
        return poiRepository.getPOIsSinceTimestampAsFlow(timestamp)
    }
}