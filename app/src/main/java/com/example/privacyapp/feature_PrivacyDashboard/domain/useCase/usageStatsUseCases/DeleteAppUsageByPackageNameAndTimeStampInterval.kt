package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

class DeleteAppUsageByPackageNameAndTimeStampInterval(
    private val appUsageRepository: AppUsageRepository
) {

    suspend operator fun invoke(packageName: String, startTimestamp: Long, endTimestamp: Long) {
        appUsageRepository.deleteAppUsageStatsByInterval(packageName, startTimestamp, endTimestamp)
    }
}