package com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsEvent
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing the state and business logic of the Favorites screen.
 *
 * @param appUseCases The use cases related to app operations.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val appUseCases: AppUseCases
) : ViewModel(){

    private val _state = mutableStateOf(AppsState())
    val state: MutableState<AppsState> = _state

    private var getAppsJob: Job? = null

    var cumulativeUsage = 0

    /**
     * Initializes the ViewModel by fetching favorite apps from the database.
     */
    init {
        getAppsFromDB()
    }


    /**
     * Fetches favorite apps from the database and updates the state accordingly.
     */
    private fun getAppsFromDB() {
        getAppsJob?.cancel()
        getAppsJob = appUseCases.getApps(AppOrder.LocationUsage(OrderType.Descending)).onEach { apps ->
            cumulativeUsage = if (apps.isEmpty()){
                0
            }else {
                apps.sumOf { it.numberOfEstimatedRequests }
            }
            _state.value = state.value.copy(
                apps = apps.filter { it.favorite }
            )
        }
            .launchIn(viewModelScope)
    }
}