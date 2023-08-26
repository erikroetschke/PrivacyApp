package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

/**
 * A use case class for retrieving app usage statistics since a specified timestamp.
 *
 * @param appUsageRepository The repository responsible for app usage data.
 */
class GetAppUsageSinceTimestamp(private val appUsageRepository: AppUsageRepository) {

    /**
     * Retrieves a list of app usage statistics recorded since the provided timestamp for the given package name.
     *
     * @param packageName The package name of the app for which to retrieve usage statistics.
     * @param timestamp The timestamp indicating the starting point for data retrieval.
     * @return A list of app usage statistics.
     */
    suspend operator fun invoke(packageName: String, timestamp: Long): List<AppUsage> {
        // Implementation details
        return appUsageRepository.getAppUsageStatsSinceTimestamp(packageName, timestamp)
    }
}