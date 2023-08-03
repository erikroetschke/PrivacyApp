package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAppsSuspend(
    private val repository: AppRepository
) {
    suspend operator fun invoke(
        appOrder: AppOrder = AppOrder.Title(OrderType.Ascending)
    ): List<App> {
        return when (appOrder.orderType) {
            is OrderType.Ascending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getAppsSuspend()
                        .sortedBy { it.appName.lowercase() }

                    is AppOrder.LocationUsage -> repository.getAppsSuspend()
                        .sortedBy { it.numberOfEstimatedRequests }
                }
            }

            is OrderType.Descending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getAppsSuspend()
                        .sortedByDescending { it.appName.lowercase() }

                    is AppOrder.LocationUsage -> repository.getAppsSuspend()
                        .sortedByDescending { it.numberOfEstimatedRequests }
                }
            }
        }
    }
}