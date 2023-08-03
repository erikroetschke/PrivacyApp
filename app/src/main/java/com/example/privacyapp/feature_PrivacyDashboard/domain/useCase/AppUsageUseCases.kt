package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.locationUseCases.DeleteLocationsOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.ComputeUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.DeleteAppUsageOlderThanTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.GetAppUsageSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases.UpdateAppUsageLast24Hours

data class AppUsageUseCases(
    val computeUsage: ComputeUsage,
    val updateAppUsageLast24Hours: UpdateAppUsageLast24Hours,
    val getAppUsageSinceTimestamp: GetAppUsageSinceTimestamp,
    val deleteAppUsageOlderThanTimestamp: DeleteAppUsageOlderThanTimestamp
)
