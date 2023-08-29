package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

/**
 * A use case class for updating app usage statistics for the last 24 hours.
 *
 * @param appUsageRepository The repository responsible for app usage data.
 * @param appRepository The repository responsible for app data.
 */
class UpdateAppUsageLast24Hours(
    private val appUsageRepository: AppUsageRepository,
    private val appRepository: AppRepository
) {

    /**
     * Updates app usage statistics for the last 24 hours and inserts or updates app data accordingly.
     */
    suspend operator fun invoke() {
        val appList = appRepository.getAppsSuspend()
        //getLast24Hours
        val timestamp24HoursAgo = System.currentTimeMillis() - 1000 * 60 * 60 * 24
        for (app in appList) {
            //val numberOfForegroundIntervals = appUsageRepository.getAppUsageStatsSinceTimestamp(app.packageName, timestamp24HoursAgo).filter { it.foreground }.size
            //val numberOfBackgroundIntervals = appUsageRepository.getAppUsageStatsSinceTimestamp(app.packageName, timestamp24HoursAgo).filter { it.background }.size
            val numberOfEstimatedRequests = appUsageRepository.getAppUsageStatsSinceTimestamp(
                app.packageName,
                timestamp24HoursAgo
            ).size
            appRepository.insertApp(
                App(
                    app.packageName,
                    app.appName,
                    app.ACCESS_COARSE_LOCATION,
                    app.ACCESS_FINE_LOCATION,
                    app.ACCESS_BACKGROUND_LOCATION,
                    numberOfEstimatedRequests,
                    app.favorite,
                    app.active,
                    app.requestedACCESS_COARSE_LOCATION,
                    app.requestedACCESS_FINE_LOCATION,
                    app.requestedACCESS_BACKGROUND_LOCATION,
                    app.preinstalled
                )
            )

        }
    }
}