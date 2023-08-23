package com.example.privacyapp.feature_PrivacyDashboard.data.repositories

import android.provider.CallLog
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class FakeLocationRepository: LocationRepository {

    private val locations = mutableListOf<Location>()

    override fun getLocations(): Flow<List<Location>> {
        //not used anyway
        return TODO()
    }

    override suspend fun getLocationByTimestamp(timestamp: Long): Location? {
        for (location in locations) {
            if(location.timestamp == timestamp) {
                return location
            }
        }
        return null
    }

    override suspend fun getUsedLocationsByInterval(
        timestampStart: Long,
        timestampEnd: Long
    ): List<Location> {
        val list = mutableListOf<Location>()
        for (location in locations) {
            if(location.timestamp in timestampStart..timestampEnd) {
                list.add(location)
            }
        }
        return list
    }

    override suspend fun getLocationsWithLocationUsedIsNull(): List<Location> {
        val list = mutableListOf<Location>()
        for (location in locations) {
            if(location.locationUsed == null) {
                list.add(location)
            }
        }
        return list
    }

    override suspend fun getUsedAndNonProcessedLocations(): List<Location> {
        val list = mutableListOf<Location>()
        for (location in locations) {
            if(location.locationUsed == true && !location.processed) {
                list.add(location)
            }
        }
        return list
    }

    override suspend fun getUsedLocations(timestamp: Long): List<Location> {
        val list = mutableListOf<Location>()
        for (location in locations) {
            if(location.locationUsed == true) {
                list.add(location)
            }
        }
        return list
    }

    override suspend fun insertLocation(location: Location) {
        if(locations.find { it.timestamp == location.timestamp } != null){
            locations.remove(locations.find { it.timestamp == location.timestamp })
        }
        locations.add(location)
    }

    override suspend fun deletelocation(location: Location) {
        locations.remove(location)
    }

    override suspend fun deleteLocationOlderThanTimestamp(timestamp: Long) {
        //not needed for test cases
    }

}