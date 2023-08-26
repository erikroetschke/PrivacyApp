package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.AddLocation
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.DeleteLocationsOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocations
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocationsWithLocationUsedIsNull
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetUsedLocationsLastSinceTimestamp

/**
 * Encapsulates the use cases related to location data.
 *
 * @see GetLocations
 * @see AddLocation
 * @see GetLocationsWithLocationUsedIsNull
 * @see GetUsedLocationsLastSinceTimestamp
 * @see DeleteLocationsOlderThanTimestamp
 */
data class LocationUseCases(
    val getLocations : GetLocations,
    val addLocation: AddLocation,
    val getLocationsWithLocationUsedIsNull: GetLocationsWithLocationUsedIsNull,
    val getUsedLocationsLastSinceTimestamp: GetUsedLocationsLastSinceTimestamp,
    val deleteLocationsOlderThanTimestamp: DeleteLocationsOlderThanTimestamp
)
