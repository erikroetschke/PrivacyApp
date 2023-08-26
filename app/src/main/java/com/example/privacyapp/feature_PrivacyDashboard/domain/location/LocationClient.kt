package com.example.privacyapp.feature_PrivacyDashboard.domain.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing a client for receiving location updates.
 */
interface LocationClient {
    /**
     * Gets the location updates at the specified interval.
     *
     * @param interval The interval at which location updates are requested, in milliseconds.
     * @return A Flow emitting Location updates.
     * @throws LocationException If there is an issue with location retrieval.
     */
    fun getLocationUpdates(interval: Long): Flow<Location>

    /**
     * Custom exception class for handling location-related errors.
     *
     * @param message The error message describing the issue.
     */
    class LocationException(message: String) : Exception(message)
}