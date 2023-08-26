package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp

/**
 * Data Access Object (DAO) for managing Location entities.
 */
@Dao
interface LocationDao {

    /**
     * Returns a Flow of all locations.
     */
    @Query("SELECT * FROM location")
    fun getLocations(): Flow<List<Location>>

    /**
     * Retrieves a location based on its timestamp.
     */
    @Query("SELECT * FROM location WHERE timestamp = :timestamp")
    suspend fun getLocationByTimestamp(timestamp: Long): Location?

    /**
     * Returns a list of used locations within the specified timestamp interval.
     */
    @Query("SELECT * FROM location WHERE locationUsed = 1 AND timestamp >= :timestampStart AND timestamp <= :timestampEnd")
    fun getUsedLocationsByInterval(timestampStart: Long, timestampEnd: Long): List<Location>

    /**
     * Retrieves a list of locations with locationUsed set to null.
     */
    @Query("SELECT * FROM location WHERE locationUsed IS NULL")
    suspend fun getLocationsWithLocationUsedIsNull(): List<Location>

    /**
     * Retrieves a list of used locations since the specified timestamp.
     */
    @Query("SELECT * FROM location WHERE locationUsed = 1 AND timestamp >= :timestamp")
    suspend fun getUsedLocations(timestamp: Long): List<Location>

    /**
     * Inserts or replaces a location entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    /**
     * Deletes a location entity.
     */
    @Delete
    suspend fun deletelocation(location: Location)

    /**
     * Deletes locations older than the specified timestamp.
     */
    @Query("DELETE FROM location WHERE timestamp < :timestamp")
    suspend fun deleteLocationOlderThanTimestamp(timestamp: Long)

    /**
     * Retrieves a list of used and non-processed locations.
     */
    @Query("SELECT * FROM location WHERE locationUsed = 1 AND processed = 0")
    suspend fun getUsedAndNonProcessedLocations(): List<Location>
}