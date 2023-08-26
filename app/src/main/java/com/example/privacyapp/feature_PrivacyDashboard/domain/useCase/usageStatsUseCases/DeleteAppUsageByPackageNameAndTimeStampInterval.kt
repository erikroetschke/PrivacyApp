package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

/**
 * A use case class for deleting app usage statistics within a specified time interval for a given package name.
 *
 * @param appUsageRepository The repository responsible for app usage data.
 */
class DeleteAppUsageByPackageNameAndTimeStampInterval(private val appUsageRepository: AppUsageRepository) {

    /**
     * Deletes app usage statistics within the specified time interval for the provided package name.
     *
     * @param packageName The package name of the app for which to delete usage statistics.
     * @param startTimestamp The start timestamp of the time interval.
     * @param endTimestamp The end timestamp of the time interval.
     */
    suspend operator fun invoke(packageName: String, startTimestamp: Long, endTimestamp: Long) {
        // Implementation details
        appUsageRepository.deleteAppUsageStatsByInterval(packageName, startTimestamp, endTimestamp)
    }
}
