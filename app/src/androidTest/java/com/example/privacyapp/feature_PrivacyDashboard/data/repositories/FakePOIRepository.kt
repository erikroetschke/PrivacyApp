package com.example.privacyapp.feature_PrivacyDashboard.data.repositories

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import kotlinx.coroutines.flow.Flow

class FakePOIRepository: POIRepository {

    private val pois = mutableListOf<POI>()

    override suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI> {
        val list = mutableListOf<POI>()
        for (poi in pois) {
            if(poi.timestamp >= timestamp) {
                list.add(poi)
            }
        }
        return list
    }

    override fun getPOIsSinceTimestampAsFlow(timestamp: Long): Flow<List<POI>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPOI(poi: POI) {
        pois.add(poi)
    }

    override suspend fun deletePOI(poi: POI) {
        pois.remove(poi)
    }

    override suspend fun deletePOIOlderThanTimestamp(timestamp: Long) {
        //not needed for tests
    }
}