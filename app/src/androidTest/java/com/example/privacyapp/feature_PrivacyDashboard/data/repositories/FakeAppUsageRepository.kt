package com.example.privacyapp.feature_PrivacyDashboard.data.repositories

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

class FakeAppUsageRepository: AppUsageRepository {

    private val appUsages = mutableListOf<AppUsage>()

    override suspend fun getAppUsageStats(): List<AppUsage> {
        return appUsages
    }

    override suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage? {
        for (usage in appUsages) {
            if(usage.packageName == packageName && usage.timestamp == timestamp) {
                return usage
            }
        }
        return null
    }

    override suspend fun getAppUsageStatsByInterval(
        packageName: String,
        startInterval: Long,
        endInterval: Long
    ): List<AppUsage> {
        return appUsages.filter { it.packageName == packageName && it.timestamp in startInterval..endInterval }
    }

    override suspend fun deleteAppUsageStatsByInterval(
        packageName: String,
        startInterval: Long,
        endInterval: Long
    ) {
        appUsages.removeAll { it.packageName == packageName && it.timestamp in startInterval..endInterval }
    }

    override suspend fun insertAppUsage(appUsage: AppUsage) {
        appUsages.add(appUsage)
    }

    override suspend fun getAppUsageStatsSinceTimestamp(
        packageName: String,
        startInterval: Long
    ): List<AppUsage> {
        return appUsages.filter { it.packageName == packageName && it.timestamp >= startInterval }
    }

    override suspend fun deleteAppUsageOlderThanTimestamp(timestamp: Long) {
        appUsages.removeAll { it.timestamp < timestamp }
    }
}