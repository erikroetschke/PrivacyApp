package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.POIDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository

class POIRepositoryImpl(private val poiDao: POIDao): POIRepository {
    override suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI> {
        return poiDao.getPOIsSinceTimestamp(timestamp)
    }

    override suspend fun insertPOI(poi: POI) {
        return poiDao.insertPOI(poi)
    }

    override suspend fun deletePOI(poi: POI) {
        return poiDao.deletePOI(poi)
    }
}