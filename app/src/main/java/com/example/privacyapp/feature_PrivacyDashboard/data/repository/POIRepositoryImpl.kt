package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.POIDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of the POIRepository interface that interacts with the POIDao.
 *
 * @param poiDao The POIDao used for data operations.
 */
class POIRepositoryImpl(private val poiDao: POIDao) : POIRepository {

    /**
     * Retrieves a list of POIs since a specific timestamp.
     *
     * @param timestamp The timestamp.
     */
    override suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI> {
        return poiDao.getPOIsSinceTimestamp(timestamp)
    }

    /**
     * Retrieves a Flow of POIs since a specific timestamp.
     *
     * @param timestamp The timestamp.
     */
    override fun getPOIsSinceTimestampAsFlow(timestamp: Long): Flow<List<POI>> {
        return poiDao.getPOIsSinceTimestampAsFlow(timestamp)
    }

    /**
     * Inserts a POI into the database.
     *
     * @param poi The POI to be inserted.
     */
    override suspend fun insertPOI(poi: POI) {
        poiDao.insertPOI(poi)
    }

    /**
     * Deletes a POI from the database.
     *
     * @param poi The POI to be deleted.
     */
    override suspend fun deletePOI(poi: POI) {
        poiDao.deletePOI(poi)
    }

    /**
     * Deletes POIs older than a specific timestamp.
     *
     * @param timestamp The timestamp.
     */
    override suspend fun deletePOIOlderThanTimestamp(timestamp: Long) {
        poiDao.deletePOIOlderThanTimestamp(timestamp)
    }
}
