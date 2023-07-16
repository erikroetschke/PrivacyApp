package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
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
        _trackingActive.value =
            ApplicationProvider.application.isServiceRunning(LocationService::class.java)
        val locations = listOf(
            Location(54.5200066, 13.404954, System.currentTimeMillis(), false),
            Location(52.5200067, 13.404954, System.currentTimeMillis() - 1000 * 60 * 1, false),
            Location(52.5200066, 13.404954, System.currentTimeMillis() - 1000 * 60 * 2, false),
            Location(52.5200065, 13.404954, System.currentTimeMillis() - 1000 * 60 * 4, false),
            Location(52.5200067, 13.404954, System.currentTimeMillis() - 1000 * 60 * 8, false),
            Location(52.5200066, 13.404954, System.currentTimeMillis() - 1000 * 60 * 9, false),
            Location(54.5200066, 13.404954, System.currentTimeMillis() - 1000 * 60 * 15, false)
        )
        privacyAssessmentUseCases.stopDetection(locations)
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