package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Returns a Flow of lists of all locations.
     */
    fun getLocations(): Flow<List<Location>>

    /**
     * Retrieves a location by its timestamp.
     *
     * @param timestamp The timestamp of the location.
     * @return The retrieved location, or null if not found.
     */
    suspend fun getLocationByTimestamp(timestamp: Long): Location?

    /**
     * Retrieves a list of used locations within a specified time interval.
     *
     * @param timestampStart The start timestamp of the interval.
     * @param timestampEnd The end timestamp of the interval.
     * @return The list of used locations within the interval.
     */
    suspend fun getUsedLocationsByInterval(timestampStart: Long, timestampEnd: Long): List<Location>

    /**
     * Retrieves a list of locations with `locationUsed` field set to null.
     *
     * @return The list of locations with `locationUsed` field null.
     */
    suspend fun getLocationsWithLocationUsedIsNull(): List<Location>

    /**
     * Retrieves a list of used and non-processed locations.
     *
     * @return The list of used and non-processed locations.
     */
    suspend fun getUsedAndNonProcessedLocations(): List<Location>

    /**
     * Retrieves a list of used locations since a specified timestamp.
     *
     * @param timestamp The start timestamp.
     * @return The list of used locations since the timestamp.
     */
    suspend fun getUsedLocations(timestamp: Long): List<Location>

    /**
     * Inserts a location into the repository.
     *
     * @param location The location to insert.
     */
    suspend fun insertLocation(location: Location)

    /**
     * Deletes a location from the repository.
     *
     * @param location The location to delete.
     */
    suspend fun deletelocation(location: Location)

    /**
     * Deletes locations older than a specified timestamp.
     *
     * @param timestamp The timestamp to compare against.
     */
    suspend fun deleteLocationOlderThanTimestamp(timestamp: Long)
}