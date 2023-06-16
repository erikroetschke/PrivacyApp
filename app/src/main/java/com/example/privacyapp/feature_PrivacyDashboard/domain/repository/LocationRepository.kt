package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocations(): Flow<List<Location>>

    suspend fun getLocationByTimestamp(timestamp: Long): Location?

    fun getLocationsbyIntervall(timestampStart: Long, timestampEnd: Long): Flow<List<Location>>

    suspend fun insertLocation(location: Location)

    suspend fun deletelocation(location: Location)
}