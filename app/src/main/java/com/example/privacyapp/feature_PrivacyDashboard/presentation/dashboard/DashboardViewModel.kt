package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val appUseCases: AppUseCases,
    private val privacyAssessmentUseCases: PrivacyAssessmentUseCases,
    private val locationUseCases: LocationUseCases,
    private val appUsageUseCases: AppUsageUseCases
) : ViewModel() {

    //states
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    private val _trackingActive = mutableStateOf(false)
    val trackingActive: MutableState<Boolean> = _trackingActive

    private val _metricSectionExpanded = mutableStateOf(false)
    val metricSectionExpanded = _metricSectionExpanded

    private val _metricDropdownSelected = mutableStateOf(Metric.StopDetection)
    val metricDropdownSelected = _metricDropdownSelected

    private val _metricIntervalDropdownSelected = mutableStateOf(MetricInterval.DAY)
    val metricIntervalDropdownSelected = _metricIntervalDropdownSelected

    private val _privacyLeakData = mutableStateOf(emptyList<Pair<Int, Double>>())
    val privacyLeakData = _privacyLeakData

    private val _top5Apps = mutableStateOf(mutableListOf<App>())
    val top5Apps = _top5Apps

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    var maxLocationUsage = 0

    init {
        _trackingActive.value = ApplicationProvider.application.isServiceRunning(LocationService::class.java)
        viewModelScope.launch {
            loadPrivacyLeakData()
            _top5Apps.value = appUseCases.getApps(AppOrder.LocationUsage(OrderType.Descending)).take(5).filter { it.numberOfEstimatedRequests > 0 }.toMutableList()
            maxLocationUsage = if(_top5Apps.value.isNotEmpty()){
                _top5Apps.value.maxOf { it.numberOfEstimatedRequests }
            }else {
                0
            }
        }
    }

    private suspend fun loadPrivacyLeakData() {


                _privacyLeakData.value = privacyAssessmentUseCases.doAssessment(
                    _metricDropdownSelected.value,
                    _metricIntervalDropdownSelected.value
                )
    }

    private suspend fun updateUsageStats() {
        //create UsageStats and update location
        //get locationData which isnt processed into UsageStats yet
        val locationsWithLocationUsedIsNull = locationUseCases.getLocationsWithLocationUsedIsNull()
        if (locationsWithLocationUsedIsNull.isNotEmpty()) {
            //get usageStats into db
            appUsageUseCases.computeUsage(locationsWithLocationUsedIsNull)
        }

        //update Apps in db with number of location Requests in th last 24 hours
        appUsageUseCases.updateAppUsageLast24Hours()
    }


    @Suppress("DEPRECATION") // Deprecated for third party Services.
    private fun Context.isServiceRunning(service: Class<LocationService>) =
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == service.name }


    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        mainActivity: MainActivity
    ) {
        var isGranted = false
        if(ContextCompat.checkSelfPermission(mainActivity, permission)
            == PackageManager.PERMISSION_GRANTED) {
            isGranted = true
        }

        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.ToggleTracking -> {
                _trackingActive.value = event.running
            }

            is DashboardEvent.ToggleMetricDropDown -> {
                _metricSectionExpanded.value = !_metricSectionExpanded.value
            }

            is DashboardEvent.ChangeMetricDropdown -> {
                _metricDropdownSelected.value = event.metric
                _metricIntervalDropdownSelected.value = event.metricInterval

                viewModelScope.launch(Dispatchers.IO) {
                    _isLoading.value = true
                    loadPrivacyLeakData()
                    _isLoading.value = false
                }
            }

            DashboardEvent.RefreshData -> {
                viewModelScope.launch {
                    _isLoading.value = true
                    updateUsageStats()
                    loadPrivacyLeakData()
                    _top5Apps.value = appUseCases.getApps(AppOrder.LocationUsage(OrderType.Descending)).take(5).filter { it.numberOfEstimatedRequests > 0 }.toMutableList()
                    maxLocationUsage = if(_top5Apps.value.isNotEmpty()){
                        _top5Apps.value.maxOf { it.numberOfEstimatedRequests }
                    }else {
                        0
                    }
                    _isLoading.value = false
                }
            }
        }
    }

}

/* for test
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
 */