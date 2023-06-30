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
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsState
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AppDetailsViewModel @Inject constructor(
    private val appUseCases: AppUseCases,
    private val appUsageUseCases: AppUsageUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val appUsagePerHour: MutableList<Pair<Int, Int>> = mutableListOf()
    private val minutesFromCompleteHour = Calendar.getInstance().get(Calendar.MINUTE)
    private val timestamp24HoursAgoRoundedToCompleteHour = System.currentTimeMillis() - (minutesFromCompleteHour * 60 * 1000) - (1000 * 60 * 60 * 24)
    private val hourInMillis = 1000 * 60 * 60
    private var listAppUsages = mutableListOf<AppUsage>()

    private val _stateApp =
        mutableStateOf(
            App(
                "App not found",
                "App not found",
                false,
                false,
                false,
                0,
                false
            )
        )
    val stateApp: MutableState<App> = _stateApp

    private var currentApp: App =
        App(
            "App not found",
            "App not found",
            false,
            false,
            false,
            0,
            false
        )

    init {
        getApp()
        getAppUsage()
    }

    private fun getAppUsage() {
        viewModelScope.launch {
            savedStateHandle.get<String>("packageName")?.let {packageName ->
                listAppUsages = appUsageUseCases.getAppUsageSinceTimestamp(packageName, timestamp24HoursAgoRoundedToCompleteHour).sortedBy { it.timestamp }.toMutableList()
            }

            var endTimestamp = timestamp24HoursAgoRoundedToCompleteHour + hourInMillis
            var hour = 0
            var counter = 0
            while (listAppUsages.isNotEmpty() || hour < 24){
                if(listAppUsages.isEmpty()){
                    appUsagePerHour.add(Pair(hour, 0))
                    hour++
                }else if(listAppUsages[0].timestamp > endTimestamp){
                    appUsagePerHour.add(Pair(hour, counter))
                    hour ++
                    counter = 0
                    endTimestamp += hourInMillis
                } else {
                    counter ++
                    listAppUsages.removeAt(0)
                }
            }
        }
    }
    private fun getApp() {
        savedStateHandle.get<String>("packageName")?.let { packageName ->
            viewModelScope.launch {
                appUseCases.getApp(packageName)?.also { app ->
                    currentApp = app
                    _stateApp.value = stateApp.value.copy(
                        packageName = app.packageName,
                        appName = app.appName,
                        ACCESS_COARSE_LOCATION = app.ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION = app.ACCESS_FINE_LOCATION,
                        ACCESS_BACKGROUND_LOCATION = app.ACCESS_BACKGROUND_LOCATION,
                        numberOfEstimatedRequests = app.numberOfEstimatedRequests,
                        favorite = app.favorite
                    )
                }
            }
        }
    }

    fun onEvent(event: AppDetailsEvent) {
        when (event) {
            is AppDetailsEvent.Favor -> {
                viewModelScope.launch {
                    try {
                        val currentAppCopy = App(
                            packageName = currentApp.packageName,
                            appName = currentApp.appName,
                            ACCESS_COARSE_LOCATION = currentApp.ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION = currentApp.ACCESS_FINE_LOCATION,
                            ACCESS_BACKGROUND_LOCATION = currentApp.ACCESS_BACKGROUND_LOCATION,
                            numberOfEstimatedRequests = currentApp.numberOfEstimatedRequests,
                            favorite = !currentApp.favorite
                        )
                        appUseCases.addApp(
                            currentAppCopy
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        System.out.println("couldn´t update app")
                    }
                }
                _stateApp.value = stateApp.value.copy(
                    favorite = !_stateApp.value.favorite
                )
            }
        }
    }
}