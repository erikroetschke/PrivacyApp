package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetApps(
    private val repository: AppRepository
) {
    operator fun invoke(
        appOrder: AppOrder = AppOrder.Title(OrderType.Ascending)
    ): Flow<List<App>> {
        return when (appOrder.orderType) {
            is OrderType.Ascending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getApps().map { apps -> apps.sortedBy { it.appName.lowercase() } }
                    is AppOrder.LocationUsage -> repository.getApps().map { apps ->  apps.sortedBy { it.numberOfEstimatedRequests }}
                }
            }

            is OrderType.Descending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getApps().map { apps -> apps.sortedByDescending { it.appName.lowercase() }}
                    is AppOrder.LocationUsage -> repository.getApps().map { apps -> apps.sortedByDescending { it.numberOfEstimatedRequests } }
                }
            }
        }

    }
}