package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.LocationDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class LocationRepositoryImpl(
    private val dao: LocationDao
):  LocationRepository{

    override fun getLocations(): Flow<List<Location>> {
        return dao.getLocations()
    }

    override suspend fun getLocationByTimestamp(timestamp: Long): Location? {
        return dao.getLocationByTimestamp(timestamp)
    }

    override suspend fun getUsedLocationsByInterval(
        timestampStart: Long,
        timestampEnd: Long
    ): List<Location> {
        return dao.getUsedLocationsByInterval(timestampStart, timestampEnd)
    }

    override suspend fun getLocationsWithLocationUsedIsNull(): List<Location> {
        return dao.getLocationsWithLocationUsedIsNull()
    }

    override suspend fun getUsedLocations(timestamp: Long): List<Location> {
        return dao.getUsedLocations(timestamp)
    }

    override suspend fun insertLocation(location: Location) {
        return dao.insertLocation(location)
    }

    override suspend fun deletelocation(location: Location) {
        return dao.deletelocation(location)
    }

    override suspend fun deleteLocationOlderThanTimestamp(timestamp: Long) {
        return dao.deleteLocationOlderThanTimestamp(timestamp)
    }
}