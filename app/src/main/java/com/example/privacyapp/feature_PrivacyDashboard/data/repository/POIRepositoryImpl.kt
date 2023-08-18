package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.POIDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import kotlinx.coroutines.flow.Flow

class POIRepositoryImpl(private val poiDao: POIDao): POIRepository {
    override suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI> {
        return poiDao.getPOIsSinceTimestamp(timestamp)
    }

    override fun getPOIsSinceTimestampAsFlow(timestamp: Long): Flow<List<POI>> {
        return poiDao.getPOIsSinceTimestampAsFlow(timestamp)
    }

    override suspend fun insertPOI(poi: POI) {
        return poiDao.insertPOI(poi)
    }

    override suspend fun deletePOI(poi: POI) {
        return poiDao.deletePOI(poi)
    }

    override suspend fun deletePOIOlderThanTimestamp(timestamp: Long) {
        return poiDao.deletePOIOlderThanTimestamp(timestamp)
    }
}