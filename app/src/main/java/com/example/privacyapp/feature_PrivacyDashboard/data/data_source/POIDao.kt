package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing Point of Interest (POI) entities.
 */
@Dao
interface POIDao {

    /**
     * Retrieves a list of POIs since the specified timestamp.
     */
    @Query("SELECT * FROM poi WHERE timestamp >= :timestamp")
    suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI>

    /**
     * Retrieves a Flow of POIs since the specified timestamp.
     */
    @Query("SELECT * FROM poi WHERE timestamp >= :timestamp")
    fun getPOIsSinceTimestampAsFlow(timestamp: Long): Flow<List<POI>>

    /**
     * Inserts a POI entity, ignoring conflicts.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPOI(poi: POI)

    /**
     * Deletes a POI entity.
     */
    @Delete
    suspend fun deletePOI(poi: POI)

    /**
     * Deletes POIs older than the specified timestamp.
     */
    @Query("DELETE FROM poi WHERE timestamp < :timestamp")
    suspend fun deletePOIOlderThanTimestamp(timestamp: Long)
}