package com.example.privacyapp.feature_PrivacyDashboard.domain.repository


import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import kotlinx.coroutines.flow.Flow

interface POIRepository {

    suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI>
    fun getPOIsSinceTimestampAsFlow(timestamp: Long): Flow<List<POI>>

    suspend fun insertPOI(poi: POI)

    suspend fun deletePOI(poi: POI)

    suspend fun deletePOIOlderThanTimestamp(timestamp: Long)
}