package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage

@Dao
interface AppUsageDao {

    @Query("SELECT * FROM appusage")
    suspend fun getAppUsageStats(): List<AppUsage>

    @Query("SELECT * FROM appusage WHERE packageName =:packageName AND timestamp =:timestamp")
    suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage?

    @Query("SELECT * FROM appusage WHERE packageName =:packageName AND timestamp BETWEEN :startInterval AND :endInterval")
    suspend fun getAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long): List<AppUsage>

    @Query("DELETE FROM appusage WHERE packageName =:packageName AND timestamp BETWEEN :startInterval AND :endInterval")
    suspend fun deleteAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsage(appUsage: AppUsage)
}