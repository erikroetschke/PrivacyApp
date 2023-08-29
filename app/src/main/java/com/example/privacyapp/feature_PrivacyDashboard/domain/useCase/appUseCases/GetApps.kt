package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppPermissionFilter
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
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
        appOrder: AppOrder = AppOrder.Title(OrderType.Ascending),
        appFilter: AppPermissionFilter = AppPermissionFilter(false, false, false, false)
    ): Flow<List<App>> {
        return filter(when (appOrder.orderType) {
            is OrderType.Ascending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getApps()
                        .map { apps -> apps.sortedBy { it.appName.lowercase() } }

                    is AppOrder.LocationUsage -> repository.getApps()
                        .map { apps -> apps.sortedBy { it.numberOfEstimatedRequests } }
                }
            }

            is OrderType.Descending -> {
                when (appOrder) {
                    is AppOrder.Title -> repository.getApps()
                        .map { apps -> apps.sortedByDescending { it.appName.lowercase() } }

                    is AppOrder.LocationUsage -> repository.getApps()
                        .map { apps -> apps.sortedByDescending { it.numberOfEstimatedRequests } }
                }
            }
        }, appFilter)

    }

    private fun filter(apps: Flow<List<App>>, appFilter: AppPermissionFilter): Flow<List<App>> {
        var res = apps
        if (appFilter.none) {
            res = apps.map { appList -> appList.filter { !it.ACCESS_COARSE_LOCATION && !it.ACCESS_FINE_LOCATION && !it.ACCESS_BACKGROUND_LOCATION } }
        }
        if (appFilter.coarseLocation) {
            res = apps.map { appList -> appList.filter { it.ACCESS_COARSE_LOCATION && !it.ACCESS_FINE_LOCATION && !it.ACCESS_BACKGROUND_LOCATION} }
        }
        if (appFilter.fineLocation) {
            res = apps.map { appList -> appList.filter { it.ACCESS_FINE_LOCATION && !it.ACCESS_BACKGROUND_LOCATION} }
        }
        if (appFilter.backgroundLocation) {
            res =apps.map { appList -> appList.filter { it.ACCESS_BACKGROUND_LOCATION } }
        }
        return res
    }
}