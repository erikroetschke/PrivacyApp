package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getLocations(): Flow<List<Location>>

    @Query("SELECT * FROM location WHERE timestamp = :timestamp")
    suspend fun getLocationByTimestamp(timestamp: Long): Location?

    @Query("SELECT * FROM location WHERE locationUsed = 1 AND timestamp >= :timestampStart AND timestamp <= :timestampEnd")
    fun getUsedLocationsByInterval(timestampStart: Long, timestampEnd: Long): List<Location>

    @Query("SELECT * FROM location WHERE locationUsed IS NULL")
    suspend fun getLocationsWithLocationUsedIsNull (): List<Location>

    @Query("SELECT * FROM location WHERE locationUsed = 1 AND timestamp >= :timestamp")
    suspend fun getUsedLocations (timestamp: Long): List<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Delete
    suspend fun deletelocation(location: Location)
}