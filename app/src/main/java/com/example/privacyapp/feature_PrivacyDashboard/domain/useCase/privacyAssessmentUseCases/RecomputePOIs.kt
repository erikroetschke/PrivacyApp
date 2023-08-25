package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import kotlinx.coroutines.delay

/**
 * A class responsible for recomputing Points of Interest (POIs) based on location data.
 *
 * @param poiRepository The repository for interacting with POIs.
 * @param locationRepository The repository for interacting with location data.
 */
class RecomputePOIs(
    private val poiRepository: POIRepository,
    private val locationRepository: LocationRepository
) {

    /**
     * Recomputes Points of Interest (POIs) based on location data.
     * This operation involves deleting existing POIs, resetting processed flags on location data,
     * and computing new POIs using the [UpdatePOIs] operation.
     *
     */
    suspend operator fun invoke(){
        //delete all existing pois
        poiRepository.deletePOIOlderThanTimestamp(System.currentTimeMillis())
        //set all location.processed to false
        val locations = locationRepository.getUsedLocations(0)
        for (location in locations) {
            locationRepository.insertLocation(location.copy(processed = false))
        }
        //compute pois
        UpdatePOIs(poiRepository, locationRepository).invoke()
    }
}