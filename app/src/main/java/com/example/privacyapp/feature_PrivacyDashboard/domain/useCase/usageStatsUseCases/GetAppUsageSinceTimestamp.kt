package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

class GetAppUsageSinceTimestamp(
    private val appUsageRepository: AppUsageRepository
) {

    suspend operator fun invoke(packageName: String, timestamp: Long): List<AppUsage> {
        return appUsageRepository.getAppUsageStatsSinceTimestamp(packageName, timestamp)
    }
}