package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Use case for retrieving apps with optional sorting order based on different criteria.
 *
 * @param repository The repository for managing apps.
 */
class GetApps(
    private val repository: AppRepository
) {

    /**
     * Retrieves a flow of apps with an optional sorting order based on the provided [appOrder].
     *
     * @param appOrder The ordering criteria for the retrieved apps.
     * @return A flow of sorted apps based on the specified criteria.
     */
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