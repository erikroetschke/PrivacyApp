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

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val appUseCases: AppUseCases
) : ViewModel(){

    private val _state = mutableStateOf(AppsState())
    val state: MutableState<AppsState> = _state

    private var getAppsJob: Job? = null

    var maxLocationUsage = 0

    init {
        getAppsFromDB()
    }


    private fun getAppsFromDB() {
        getAppsJob?.cancel()
        getAppsJob = appUseCases.getFavoriteApps().onEach { apps ->
            _state.value = state.value.copy(
                apps = apps
            )
            maxLocationUsage = apps.maxOf { it.numberOfEstimatedRequests }
        }
            .launchIn(viewModelScope)
    }

}