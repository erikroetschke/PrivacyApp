package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.AddLocation
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.DeleteLocationsOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocations
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetLocationsWithLocationUsedIsNull
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.GetUsedLocationsLastSinceTimestamp

data class LocationUseCases(
    val getLocations : GetLocations,
    val addLocation: AddLocation,
    val getLocationsWithLocationUsedIsNull: GetLocationsWithLocationUsedIsNull,
    val getUsedLocationsLastSinceTimestamp: GetUsedLocationsLastSinceTimestamp,
    val deleteLocationsOlderThanTimestamp: DeleteLocationsOlderThanTimestamp
)
