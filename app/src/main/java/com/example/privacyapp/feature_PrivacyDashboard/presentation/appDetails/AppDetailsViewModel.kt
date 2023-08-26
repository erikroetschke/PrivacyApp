package com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for the App Details screen. This ViewModel is responsible for managing the state and handling
 * interactions related to the App Details screen.
 *
 * @param appUseCases The use cases related to app operations.
 * @param appUsageUseCases The use cases related to app usage operations.
 * @param savedStateHandle The handle to saved state information.
 */
@HiltViewModel
class AppDetailsViewModel @Inject constructor(
    private val appUseCases: AppUseCases,
    private val appUsageUseCases: AppUsageUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // State for the diagram data
    private val _stateDiagramData = mutableStateOf(emptyList<Pair<Int, Int>>())
    val stateDiagramData = _stateDiagramData

    //other properties
    private val minutesToCompleteHour = 60 - Calendar.getInstance().get(Calendar.MINUTE)
    private val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private val timestamp24HoursAgoRoundedToCompleteHour =
        System.currentTimeMillis() + (minutesToCompleteHour * 60 * 1000) - (1000 * 60 * 60 * 24)
    private val hourInMillis = 1000 * 60 * 60
    var diagramMaxScale = 0

    //state App
    private val _stateApp =
        mutableStateOf(
            App(
                "App not found",
                "App not found",
                false,
                false,
                false,
                0,
                false,
                true
            )
        )
    val stateApp: MutableState<App> = _stateApp


    /**
     * Initialize the ViewModel by fetching app details and usage data.
     */
    init {
        getApp()
        getAppUsage()
    }

    private fun getAppUsage() {
        viewModelScope.launch {
            savedStateHandle.get<String>("packageName")?.let { packageName ->
                val listAppUsages = appUsageUseCases.getAppUsageSinceTimestamp(
                    packageName,
                    timestamp24HoursAgoRoundedToCompleteHour
                ).sortedBy { it.timestamp }.toMutableList()
                _stateDiagramData.value = computeAppUsageData(listAppUsages)
            }
            val mostImpactfulApp = appUseCases.getAppsSuspend(AppOrder.LocationUsage(OrderType.Descending))[0]
            val appUsagesForScale = appUsageUseCases.getAppUsageSinceTimestamp(mostImpactfulApp.packageName, timestamp24HoursAgoRoundedToCompleteHour).sortedBy { it.timestamp }.toMutableList()
            diagramMaxScale = computeAppUsageData(appUsagesForScale).maxOf { it.second } //might not be the absolut max over all apps, but iterating over all apps would be too much
            //the case that an other app has a higher consumption will be handeled in the Diagram composable
            //as there is no y axis anyway, it isnt so impartant anyway, its more to give to user a feeling how much on app compares to another
        }
    }
    private fun computeAppUsageData(listAppUsages: MutableList<AppUsage>): MutableList<Pair<Int, Int>> {
        val data = mutableListOf<Pair<Int, Int>>()

        var endTimestamp = timestamp24HoursAgoRoundedToCompleteHour + hourInMillis
        var hour = (currentHour + 1 % 24)
        var counter = 0
        while (listAppUsages.isNotEmpty()) {
            if (listAppUsages[0].timestamp > endTimestamp) {
                data.add(Pair(hour, counter))
                hour = (hour + 1) % 24
                counter = 0
                endTimestamp += hourInMillis
            } else {
                counter++
                listAppUsages.removeAt(0)
            }
        }
        //add the one from the last iteration
        data.add(Pair(hour, counter))
        hour = (hour + 1) % 24
        if (data.size < 24) {
            //fill the rest with 0
            for (i in data.size..23) {
                data.add(Pair(hour, 0))
                hour = (hour + 1) % 24
            }
        }
        return data
    }

    private fun getApp() {
        savedStateHandle.get<String>("packageName")?.let { packageName ->
            viewModelScope.launch {
                appUseCases.getApp(packageName)?.also { app ->
                    _stateApp.value = app
                }
            }
        }
    }

    /**
     * Function to handle events triggered in the AppDetails screen.
     *
     * @param event The event to be handled.
     */
    fun onEvent(event: AppDetailsEvent) {
        when (event) {
            is AppDetailsEvent.Favor -> {
                viewModelScope.launch {
                    try {
                        val currentAppCopy = App(
                            packageName = _stateApp.value.packageName,
                            appName = _stateApp.value.appName,
                            ACCESS_COARSE_LOCATION = _stateApp.value.ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION = _stateApp.value.ACCESS_FINE_LOCATION,
                            ACCESS_BACKGROUND_LOCATION = _stateApp.value.ACCESS_BACKGROUND_LOCATION,
                            numberOfEstimatedRequests = _stateApp.value.numberOfEstimatedRequests,
                            favorite = !_stateApp.value.favorite,
                            active = _stateApp.value.active
                        )
                        appUseCases.addApp(
                            currentAppCopy
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("couldn´t update app")
                    }
                }
                _stateApp.value = stateApp.value.copy(
                    favorite = !_stateApp.value.favorite
                )
            }

            AppDetailsEvent.ToggleActive -> {
                viewModelScope.launch {
                    try {
                        val currentAppCopy = App(
                            packageName = _stateApp.value.packageName,
                            appName = _stateApp.value.appName,
                            ACCESS_COARSE_LOCATION = _stateApp.value.ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION = _stateApp.value.ACCESS_FINE_LOCATION,
                            ACCESS_BACKGROUND_LOCATION = _stateApp.value.ACCESS_BACKGROUND_LOCATION,
                            numberOfEstimatedRequests = _stateApp.value.numberOfEstimatedRequests,
                            favorite = _stateApp.value.favorite,
                            active = !_stateApp.value.active
                        )
                        appUseCases.addApp(
                            currentAppCopy
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("couldn´t update app")
                    }
                }
                _stateApp.value = stateApp.value.copy(
                    active = !_stateApp.value.active
                )
            }
        }
    }
}