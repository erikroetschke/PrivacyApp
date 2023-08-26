package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.LocationDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of the LocationRepository interface that interacts with the LocationDao.
 *
 * @param dao The LocationDao used for data operations.
 */
class LocationRepositoryImpl(private val dao: LocationDao) : LocationRepository {

    /**
     * Retrieves a Flow of all locations.
     */
    override fun getLocations(): Flow<List<Location>> {
        return dao.getLocations()
    }

    /**
     * Retrieves a location by timestamp.
     *
     * @param timestamp The timestamp of the location.
     */
    override suspend fun getLocationByTimestamp(timestamp: Long): Location? {
        return dao.getLocationByTimestamp(timestamp)
    }

    /**
     * Retrieves a list of used locations within a specific interval.
     *
     * @param timestampStart The start timestamp of the interval.
     * @param timestampEnd The end timestamp of the interval.
     */
    override suspend fun getUsedLocationsByInterval(
        timestampStart: Long,
        timestampEnd: Long
    ): List<Location> {
        return dao.getUsedLocationsByInterval(timestampStart, timestampEnd)
    }

    /**
     * Retrieves a list of locations with locationUsed value as null.
     */
    override suspend fun getLocationsWithLocationUsedIsNull(): List<Location> {
        return dao.getLocationsWithLocationUsedIsNull()
    }

    /**
     * Retrieves a list of used and non-processed locations.
     */
    override suspend fun getUsedAndNonProcessedLocations(): List<Location> {
        return dao.getUsedAndNonProcessedLocations()
    }

    /**
     * Retrieves a list of used locations since a specific timestamp.
     *
     * @param timestamp The start timestamp.
     */
    override suspend fun getUsedLocations(timestamp: Long): List<Location> {
        return dao.getUsedLocations(timestamp)
    }

    /**
     * Inserts a location into the database.
     *
     * @param location The location to be inserted.
     */
    override suspend fun insertLocation(location: Location) {
        dao.insertLocation(location)
    }

    /**
     * Deletes a location from the database.
     *
     * @param location The location to be deleted.
     */
    override suspend fun deletelocation(location: Location) {
        dao.deletelocation(location)
    }

    /**
     * Deletes locations older than a specific timestamp.
     *
     * @param timestamp The timestamp.
     */
    override suspend fun deleteLocationOlderThanTimestamp(timestamp: Long) {
        dao.deleteLocationOlderThanTimestamp(timestamp)
    }
}
