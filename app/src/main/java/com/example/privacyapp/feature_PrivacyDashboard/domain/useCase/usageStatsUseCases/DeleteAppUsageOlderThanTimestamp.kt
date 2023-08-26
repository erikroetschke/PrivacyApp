package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

/**
 * A use case class for deleting app usage statistics older than a specified timestamp.
 *
 * @param appUsageRepository The repository responsible for app usage data.
 */
class DeleteAppUsageOlderThanTimestamp(private val appUsageRepository: AppUsageRepository) {

    /**
     * Deletes app usage statistics that are older than the provided timestamp.
     *
     * @param timestamp The timestamp indicating the threshold for deletion.
     */
    suspend operator fun invoke(timestamp: Long) {
        // Implementation details
        appUsageRepository.deleteAppUsageOlderThanTimestamp(timestamp)
    }
}