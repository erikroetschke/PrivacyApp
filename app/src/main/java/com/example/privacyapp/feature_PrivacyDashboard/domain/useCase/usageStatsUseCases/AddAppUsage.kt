package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

/**
 * The `AddAppUsage` class encapsulates the use case of adding an application usage entry to the repository.
 *
 * @param appUsageRepository Repository for application usage data.
 */
class AddAppUsage(
    private val appUsageRepository: AppUsageRepository
) {

    /**
     * Adds an application usage entry to the repository.
     *
     * @param appUsage The application usage entry to be added.
     */
    suspend operator fun invoke(appUsage: AppUsage) {
        appUsageRepository.insertAppUsage(appUsage)
    }
}
