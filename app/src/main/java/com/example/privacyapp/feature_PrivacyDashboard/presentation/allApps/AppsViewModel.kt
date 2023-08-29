package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppPermissionFilter
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing the state and business logic of the apps screen.
 *
 * This ViewModel handles interactions related to the apps screen, including sorting and managing
 * application data based on different criteria. It exposes the current state of the screen,
 * including the list of apps, ordering criteria, and visibility of order section.
 *
 * @param appUseCases The use case class providing access to application-related data and actions.
 */
@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appUseCases: AppUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AppsState())
    val state: MutableState<AppsState> = _state

    var cumulativeUsage = 0

    private var getAppsJob: Job? = null

    /**
     * Initializes the ViewModel by fetching and ordering apps data.
     *
     * This function is called during ViewModel initialization to retrieve the list of apps
     * and order them based on location usage in descending order.
     */
    init {
        //get apps
        getAppsFromDB(
            AppOrder.LocationUsage(OrderType.Descending), AppPermissionFilter(
                none = false,
                coarseLocation = false,
                fineLocation = false,
                backgroundLocation = false
            )
        )
    }

    /**
     * Handles events triggered by user interactions on the apps screen.
     *
     * This function processes user events such as changing the ordering criteria and toggling
     * the visibility of the order section.
     *
     * @param event The event representing the user interaction.
     */
    fun onEvent(event: AppsEvent) {
        when (event) {
            is AppsEvent.Order -> {
                if (state.value.appOrder::class == event.appOrder::class &&
                    state.value.appOrder.orderType == event.appOrder.orderType
                ) {
                    return
                } else {
                    _state.value = state.value.copy(
                        appOrder = event.appOrder
                    )
                    getAppsFromDB(event.appOrder, _state.value.appFilter)
                }
            }

            is AppsEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }

            is AppsEvent.Filter -> {
                _state.value = _state.value.copy(
                    appFilter = event.filter
                )
                getAppsFromDB(_state.value.appOrder, event.filter)
            }
        }
    }


    /**
     * Fetches and updates the list of apps based on the given app ordering criteria.
     *
     * This function retrieves the list of apps from the data source using the provided
     * app ordering criteria. It also calculates and updates the statistics related to location usage.
     *
     * @param appOrder The criteria for ordering the list of apps.
     */
    private fun getAppsFromDB(appOrder: AppOrder, appFilter: AppPermissionFilter) {
        getAppsJob?.cancel()
        getAppsJob = appUseCases.getApps(appOrder, appFilter).onEach { apps ->
            _state.value = state.value.copy(apps = apps)
            cumulativeUsage = _state.value.apps.sumOf { it.numberOfEstimatedRequests }
        }.launchIn(viewModelScope)
    }

}