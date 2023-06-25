package com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppDetailsViewModel @Inject constructor(
    private val appUseCases: AppUseCases,
    private val savedStateHandle: SavedStateHandle
):ViewModel() {

    private var AppPackageName: Int? = null

    private val _state = mutableStateOf(App("App not found", "App not found", false, false, false, 0, false))
    val state: MutableState<App> = _state

    private var currentApp: App = App("App not found", "App not found", false, false, false, 0, false)

    init {
        getApp()
    }

    private fun getApp() {
        savedStateHandle.get<String>("packageName")?.let { packageName ->
            viewModelScope.launch {
                appUseCases.getApp(packageName)?.also { app ->
                    currentApp = app
                    _state.value = state.value.copy(
                        packageName = app.packageName,
                        appName = app.appName,
                        ACCESS_COARSE_LOCATION = app.ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION = app.ACCESS_FINE_LOCATION,
                        ACCESS_BACKGROUND_LOCATION = app.ACCESS_BACKGROUND_LOCATION,
                        estimatedLocationRequestFrequency = app.estimatedLocationRequestFrequency,
                        favorite = app.favorite
                    )
                }
            }
        }
    }

    fun onEvent(event: AppDetailsEvent) {
        when(event) {
            is AppDetailsEvent.Favor -> {
                viewModelScope.launch {
                    try {
                        val currentAppCopy = App(
                        packageName = currentApp.packageName,
                        appName = currentApp.appName,
                        ACCESS_COARSE_LOCATION = currentApp.ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION = currentApp.ACCESS_FINE_LOCATION,
                        ACCESS_BACKGROUND_LOCATION = currentApp.ACCESS_BACKGROUND_LOCATION,
                        estimatedLocationRequestFrequency = currentApp.estimatedLocationRequestFrequency,
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
                _state.value = state.value.copy(
                    favorite = !_state.value.favorite
                )
            }
        }
    }
}