package com.example.privacyapp.feature_PrivacyDashboard.domain.repository


import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import kotlinx.coroutines.flow.Flow

interface POIRepository {
    /**
     * Retrieves a list of Points of Interest (POIs) since a specified timestamp.
     *
     * @param timestamp The start timestamp.
     * @return The list of POIs since the timestamp.
     */
    suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI>

    /**
     * Retrieves a Flow of lists of POIs since a specified timestamp.
     *
     * @param timestamp The start timestamp.
     * @return A Flow emitting lists of POIs since the timestamp.
     */
    fun getPOIsSinceTimestampAsFlow(timestamp: Long): Flow<List<POI>>

    /**
     * Inserts a Point of Interest (POI) into the repository.
     *
     * @param poi The POI to insert.
     */
    suspend fun insertPOI(poi: POI)

    /**
     * Deletes a Point of Interest (POI) from the repository.
     *
     * @param poi The POI to delete.
     */
    suspend fun deletePOI(poi: POI)

    /**
     * Deletes POIs older than a specified timestamp.
     *
     * @param timestamp The timestamp to compare against.
     */
    suspend fun deletePOIOlderThanTimestamp(timestamp: Long)
}