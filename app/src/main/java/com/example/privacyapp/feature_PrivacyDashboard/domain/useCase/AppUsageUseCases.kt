package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.DeleteLocationsOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.AddAppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.ComputeUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.DeleteAppUsageByPackageNameAndTimeStampInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.DeleteAppUsageOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.GetAppUsageSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.UpdateAppUsageLast24Hours

/**
 * Encapsulates the use cases related to app usage.
 *
 * @see ComputeUsage
 * @see UpdateAppUsageLast24Hours
 * @see GetAppUsageSinceTimestamp
 * @see DeleteAppUsageOlderThanTimestamp
 * @see AddAppUsage
 * @see DeleteAppUsageByPackageNameAndTimeStampInterval
 */
data class AppUsageUseCases(
    val computeUsage: ComputeUsage,
    val updateAppUsageLast24Hours: UpdateAppUsageLast24Hours,
    val getAppUsageSinceTimestamp: GetAppUsageSinceTimestamp,
    val deleteAppUsageOlderThanTimestamp: DeleteAppUsageOlderThanTimestamp,
    val addAppUsage: AddAppUsage,
    val deleteAppUsageByPackageNameAndTimeStampInterval: DeleteAppUsageByPackageNameAndTimeStampInterval
)
