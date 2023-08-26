package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppUsageDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

/**
 * Implementation of the AppUsageRepository interface that interacts with the AppUsageDao.
 *
 * @param appUsageDao The AppUsageDao used for data operations.
 */
class AppUsageRepositoryImpl(private val appUsageDao: AppUsageDao) : AppUsageRepository {

    /**
     * Retrieves a list of all app usage statistics.
     */
    override suspend fun getAppUsageStats(): List<AppUsage> {
        return appUsageDao.getAppUsageStats()
    }

    /**
     * Retrieves an app usage statistic for a specific package name and timestamp.
     *
     * @param packageName The package name of the app.
     * @param timestamp The timestamp of the app usage.
     */
    override suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage? {
        return appUsageDao.getAppUsageStat(packageName, timestamp)
    }

    /**
     * Retrieves a list of app usage statistics within a specific interval.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @param endInterval The end timestamp of the interval.
     */
    override suspend fun getAppUsageStatsByInterval(
        packageName: String,
        startInterval: Long,
        endInterval: Long
    ): List<AppUsage> {
        return appUsageDao.getAppUsageStatsByInterval(packageName, startInterval, endInterval)
    }

    /**
     * Deletes app usage statistics within a specific interval.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp of the interval.
     * @param endInterval The end timestamp of the interval.
     */
    override suspend fun deleteAppUsageStatsByInterval(
        packageName: String,
        startInterval: Long,
        endInterval: Long
    ) {
        appUsageDao.deleteAppUsageStatsByInterval(packageName, startInterval, endInterval)
    }

    /**
     * Inserts an app usage statistic into the database.
     *
     * @param appUsage The app usage statistic to be inserted.
     */
    override suspend fun insertAppUsage(appUsage: AppUsage) {
        appUsageDao.insertAppUsage(appUsage)
    }

    /**
     * Retrieves a list of app usage statistics since a specific timestamp.
     *
     * @param packageName The package name of the app.
     * @param startInterval The start timestamp.
     */
    override suspend fun getAppUsageStatsSinceTimestamp(
        packageName: String,
        startInterval: Long
    ): List<AppUsage> {
        return appUsageDao.getAppUsageStatsSinceTimestamp(packageName, startInterval)
    }

    /**
     * Deletes app usage statistics older than a specific timestamp.
     *
     * @param timestamp The timestamp.
     */
    override suspend fun deleteAppUsageOlderThanTimestamp(timestamp: Long) {
        appUsageDao.deleteAppUsageOlderThanTimestamp(timestamp)
    }
}
