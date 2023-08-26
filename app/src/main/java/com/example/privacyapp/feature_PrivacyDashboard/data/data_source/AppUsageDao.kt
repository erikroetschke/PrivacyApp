package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage

/**
 * Data Access Object (DAO) for managing operations related to the 'appusage' table in the database.
 */
@Dao
interface AppUsageDao {

    /**
     * Retrieves a list of all app usage statistics from the 'appusage' table.
     *
     * @return A list of all app usage statistics.
     */
    @Query("SELECT * FROM appusage")
    suspend fun getAppUsageStats(): List<AppUsage>

    /**
     * Retrieves an app usage statistic by package name and timestamp from the 'appusage' table.
     *
     * @param packageName The package name of the app.
     * @param timestamp The timestamp of the app usage statistic.
     * @return The app usage statistic with the specified package name and timestamp, or null if not found.
     */
    @Query("SELECT * FROM appusage WHERE packageName =:packageName AND timestamp =:timestamp")
    suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage?

    /**
     * Retrieves a list of app usage statistics by package name and timestamp interval from the 'appusage' table.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @param endInterval The end timestamp of the interval.
     * @return A list of app usage statistics within the specified interval.
     */
    @Query("SELECT * FROM appusage WHERE packageName =:packageName AND timestamp BETWEEN :startInterval AND :endInterval")
    suspend fun getAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long): List<AppUsage>

    /**
     * Deletes app usage statistics by package name and timestamp interval from the 'appusage' table.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @param endInterval The end timestamp of the interval.
     */
    @Query("DELETE FROM appusage WHERE packageName =:packageName AND timestamp BETWEEN :startInterval AND :endInterval")
    suspend fun deleteAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long)

    /**
     * Deletes app usage statistics older than a specified timestamp from the 'appusage' table.
     *
     * @param timestamp The timestamp used for comparison.
     */
    @Query("DELETE FROM appusage WHERE timestamp < :timestamp")
    suspend fun deleteAppUsageOlderThanTimestamp(timestamp: Long)

    /**
     * Inserts or updates an app usage statistic into the 'appusage' table.
     *
     * @param appUsage The app usage statistic to be inserted or updated.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsage(appUsage: AppUsage)

    /**
     * Retrieves a list of app usage statistics by package name and start timestamp interval from the 'appusage' table.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @return A list of app usage statistics with timestamps greater than or equal to the start interval.
     */
    @Query("SELECT * FROM appusage WHERE packageName =:packageName AND timestamp >= :startInterval")
    suspend fun getAppUsageStatsSinceTimestamp(packageName: String, startInterval: Long): List<AppUsage>
}