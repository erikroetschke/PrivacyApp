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
    suspend operator fun invoke(
        appOrder: AppOrder = AppOrder.Title(OrderType.Ascending)
    ): List<App> {
        return when (appOrder.orderType) {
            is OrderType.Ascending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getApps().sortedBy { it.appName.lowercase() }
                    is AppOrder.LocationUsage -> repository.getApps()
                        .sortedBy { it.estimatedLocationRequestFrequency }
                }
            }

            is OrderType.Descending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getApps()
                        .sortedByDescending { it.appName.lowercase() }

                    is AppOrder.LocationUsage -> repository.getApps()
                        .sortedByDescending { it.estimatedLocationRequestFrequency }
                }
            }
        }

    }
}