package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import kotlinx.coroutines.flow.Flow

interface AppUsageRepository {
    /**
     * Retrieves a list of all app usage statistics.
     */
    suspend fun getAppUsageStats(): List<AppUsage>

    /**
     * Retrieves an app usage statistic by package name and timestamp.
     *
     * @param packageName The package name of the app.
     * @param timestamp The timestamp of the app usage.
     * @return The retrieved app usage statistic, or null if not found.
     */
    suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage?

    /**
     * Retrieves a list of app usage statistics within a specified time interval.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @param endInterval The end timestamp of the interval.
     * @return The list of app usage statistics within the interval.
     */
    suspend fun getAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long): List<AppUsage>

    /**
     * Deletes app usage statistics within a specified time interval.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @param endInterval The end timestamp of the interval.
     */
    suspend fun deleteAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long)

    /**
     * Inserts an app usage statistic into the repository.
     *
     * @param appUsage The app usage statistic to insert.
     */
    suspend fun insertAppUsage(appUsage: AppUsage)

    /**
     * Retrieves a list of app usage statistics since a specified timestamp.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp.
     * @return The list of app usage statistics since the timestamp.
     */
    suspend fun getAppUsageStatsSinceTimestamp(packageName: String, startInterval: Long): List<AppUsage>

    /**
     * Deletes app usage statistics older than a specified timestamp.
     *
     * @param timestamp The timestamp to compare against.
     */
    suspend fun deleteAppUsageOlderThanTimestamp(timestamp: Long)
}
