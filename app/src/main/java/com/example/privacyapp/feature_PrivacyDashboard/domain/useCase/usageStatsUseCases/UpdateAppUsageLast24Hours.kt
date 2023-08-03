package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository

class UpdateAppUsageLast24Hours(
    private val appUsageRepository: AppUsageRepository,
    private val appRepository: AppRepository
) {

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
                    app.active
                )
            )

        }
    }
}