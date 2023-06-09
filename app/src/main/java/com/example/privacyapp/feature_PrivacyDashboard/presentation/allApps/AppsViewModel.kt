package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.util.LOCATION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appUseCases: AppUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AppsState())
    val state: MutableState<AppsState> = _state

    var maxLocationUsage = 0

    init {
        //get apps
        getAppsFromDB(AppOrder.LocationUsage(OrderType.Descending))
    }

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
                    getAppsFromDB(event.appOrder)
                }
            }

            is AppsEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }


    private fun getAppsFromDB(appOrder: AppOrder) {
        viewModelScope.launch {
            _state.value = state.value.copy(apps = appUseCases.getApps(appOrder))
            //get the Maximum location usage to scale accordingly
            maxLocationUsage = _state.value.apps.maxOf { it.numberOfEstimatedRequests }

        }
    }

}