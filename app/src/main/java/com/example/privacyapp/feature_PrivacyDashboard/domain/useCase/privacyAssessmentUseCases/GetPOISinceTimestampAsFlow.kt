package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import kotlinx.coroutines.flow.Flow

/**
 * The `GetPOISinceTimestampAsFlow` class encapsulates the use case of retrieving points of interest (POIs)
 * as a Flow since a specified timestamp.
 *
 * @param poiRepository Repository for POI data.
 */
class GetPOISinceTimestampAsFlow(
    private val poiRepository: POIRepository
) {

    /**
     * Retrieves points of interest (POIs) as a Flow since the provided timestamp.
     *
     * @param timestamp The timestamp since which POIs should be retrieved.
     * @return A Flow emitting a list of POIs corresponding to the specified timestamp.
     */
    operator fun invoke(timestamp: Long): Flow<List<POI>> {
        return poiRepository.getPOIsSinceTimestampAsFlow(timestamp)
    }
}