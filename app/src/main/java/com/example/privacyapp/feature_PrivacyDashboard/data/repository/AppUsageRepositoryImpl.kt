package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppUsageDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

class AppUsageRepositoryImpl(
    private val appUsageDao: AppUsageDao
): AppUsageRepository {
    override suspend fun getAppUsageStats(): List<AppUsage> {
        return appUsageDao.getAppUsageStats()
    }

    override suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage? {
        return appUsageDao.getAppUsageStat(packageName, timestamp)
    }

    override suspend fun getAppUsageStatsByInterval(
        packageName: String,
        startInterval: Long,
        endInterval: Long
    ): List<AppUsage> {
        return appUsageDao.getAppUsageStatsByInterval(packageName, startInterval, endInterval)
    }

    override suspend fun deleteAppUsageStatsByInterval(
        packageName: String,
        startInterval: Long,
        endInterval: Long
    ) {
        return appUsageDao.deleteAppUsageStatsByInterval(packageName, startInterval, endInterval)
    }

    override suspend fun insertAppUsage(appUsage: AppUsage) {
        return appUsageDao.insertAppUsage(appUsage)
    }
}