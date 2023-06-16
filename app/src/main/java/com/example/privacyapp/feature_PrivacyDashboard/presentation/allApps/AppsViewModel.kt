package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import android.content.pm.ChangedPackages
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appUseCases: AppUseCases
) : ViewModel(){

    private val _state = mutableStateOf(AppsState())
    val state: MutableState<AppsState> = _state

    private var getAppsJob: Job? = null

    init {
        //clear table

        viewModelScope.launch {
            appUseCases.deleteAllApps()
        }

            //get the apps
            val apps = appUseCases.initApps()
            //insert apps in db
            for (app in apps){
                viewModelScope.launch {
                    appUseCases.addApp(app)
                }
            }
        getAppsFromDB()
    }


    private fun getAppsFromDB() {
        getAppsJob?.cancel()
        getAppsJob = appUseCases.getApps().onEach { app ->
            _state.value = state.value.copy(
                apps = app
            )
        }
            .launchIn(viewModelScope)
    }

}