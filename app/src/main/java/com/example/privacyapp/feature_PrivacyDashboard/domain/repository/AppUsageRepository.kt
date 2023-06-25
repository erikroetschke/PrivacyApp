package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import kotlinx.coroutines.flow.Flow

interface AppUsageRepository {

    suspend fun getAppUsageStats(): List<AppUsage>

    suspend fun getAppUsageStat(packageName: String, timestamp: Long): AppUsage?

    suspend fun getAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long): List<AppUsage>

    suspend fun deleteAppUsageStatsByInterval(packageName: String, startInterval: Long, endInterval: Long)

    suspend fun insertAppUsage(appUsage: AppUsage)

}