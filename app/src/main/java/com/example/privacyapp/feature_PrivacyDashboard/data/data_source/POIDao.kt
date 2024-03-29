package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import kotlinx.coroutines.flow.Flow

@Dao
interface POIDao {

    @Query("SELECT * FROM poi WHERE timestamp >= :timestamp")
    suspend fun getPOIsSinceTimestamp(timestamp: Long): List<POI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOI(poi: POI)

    @Delete
    suspend fun deletePOI(poi: POI)
}