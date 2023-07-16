package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import java.util.Calendar

class GetUsedAndUnprocessedLocationsLast24Hours(
    private val repository: LocationRepository
) {

    /**
     *
     * Example: current time: 02.01.23, 14.35 -> returns from 01.01.23,15.00 to 02.01.23,14.35
     * @returns list of locations which were used from at least one app and are in the last 24hours, but rounded up to a full hour
     */
    suspend operator fun invoke(): List<Location> {
        val minutesToCompleteHour = 60 - Calendar.getInstance().get(Calendar.MINUTE)
        val timestamp24HoursAgoRoundedToCompleteHour = System.currentTimeMillis() + (minutesToCompleteHour * 60 * 1000) - (1000 * 60 * 60 * 24)
        return repository.getUsedAndUnprocessedLocations(timestamp24HoursAgoRoundedToCompleteHour)
    }
}