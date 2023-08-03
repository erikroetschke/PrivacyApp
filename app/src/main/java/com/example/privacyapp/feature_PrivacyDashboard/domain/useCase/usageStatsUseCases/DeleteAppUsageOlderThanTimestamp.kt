package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

class DeleteAppUsageOlderThanTimestamp(
    private val appUsageRepository: AppUsageRepository
) {

    suspend operator fun invoke(timestamp: Long) {
        return appUsageRepository.deleteAppUsageOlderThanTimestamp(timestamp)
    }
}