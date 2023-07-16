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

    override fun getLocationsbyIntervall(
        timestampStart: Long,
        timestampEnd: Long
    ): Flow<List<Location>> {
        return dao.getLocationsbyIntervall(timestampStart, timestampEnd)
    }

    override suspend fun getLocationsWithLocationUsedIsNull(): List<Location> {
        return dao.getLocationsWithLocationUsedIsNull()
    }

    override suspend fun getUsedAndUnprocessedLocations(timestamp: Long): List<Location> {
        return dao.getUsedAndUnprocessedLocations(timestamp)
    }

    override suspend fun insertLocation(location: Location) {
        return dao.insertLocation(location)
    }

    override suspend fun deletelocation(location: Location) {
        return dao.deletelocation(location)
    }
}