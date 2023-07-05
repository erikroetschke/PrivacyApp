package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetNumberOfPOI
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val privacyAssessmentUseCases: PrivacyAssessmentUseCases
) : ViewModel() {
    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    private val _trackingActive = mutableStateOf(false)
    val trackingActive: MutableState<Boolean> = _trackingActive

    init {
        _trackingActive.value = ApplicationProvider.application.isServiceRunning(LocationService::class.java)
        val locations = listOf(Location(52.5200066, 13.404954, 123123, false, false), Location(52.5200066, 13.404954, 123123, false, false))
        //privacyAssessmentUseCases.getNumberOfPOI(locations)
        val py = Python.getInstance()
        val modulePoint = py.getModule("script")
        modulePoint.get("num")?.toInt()
    }

    @Suppress("DEPRECATION") // Deprecated for third party Services.
    private fun <T> Context.isServiceRunning(service: Class<T>) =
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == service.name }


    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.ToggleTracking -> {
                _trackingActive.value = event.running
            }
        }
    }

}