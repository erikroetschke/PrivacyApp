package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.POWER_SERVICE
import android.content.pm.PackageManager
import android.os.PowerManager
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
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel class for managing dashboard-related data and events.
 *
 * @property appUseCases Use cases for interacting with app data.
 * @property privacyAssessmentUseCases Use cases for performing privacy assessments.
 * @property locationUseCases Use cases for managing location-related data.
 * @property appUsageUseCases Use cases for managing app usage data.
 */
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

    private val _selectedMetrics = mutableStateListOf(Metric.StopDetection)
    val selectedMetrics = _selectedMetrics

    private val _metricInterval = mutableStateOf(MetricInterval.DAY)
    val metricInterval = _metricInterval

    private val _privacyLeakDataAbsolut = mutableStateListOf<Pair<Int, Double>>()
    val privacyLeakDataAbsolut = _privacyLeakDataAbsolut

    private val _privacyLeakDataScore = mutableStateListOf<Pair<Int, Double>>()
    val privacyLeakDataScore = _privacyLeakDataScore

    private val _top5Apps = mutableStateListOf<App>()
    val top5Apps = _top5Apps

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    private val _energySaverDialogVisible = mutableStateOf(false)
    val energySaverDialogVisible = _energySaverDialogVisible

    private val _infoDialogVisible = mutableStateOf(false)
    val infoDialogVisible = _infoDialogVisible

    var cumulativeUsage = 0

    private var privacyAssessmentJob: Job? = null
    private var getTop5AppsJob: Job? = null

    init {
        _trackingActive.value =
            ApplicationProvider.application.isServiceRunning(LocationService::class.java)
        loadPrivacyLeakData(true)
        getTop5()


        val packageName = ApplicationProvider.application.packageName
        val powerManger =
            ApplicationProvider.application.getSystemService(POWER_SERVICE) as PowerManager
        if (!powerManger.isIgnoringBatteryOptimizations(packageName)) {
            _energySaverDialogVisible.value = true
        }

    }

    private fun getTop5() {
        getTop5AppsJob?.cancel()
        getTop5AppsJob =
            appUseCases.getApps(AppOrder.LocationUsage(OrderType.Descending)).onEach { apps ->
                _top5Apps.clear()
                cumulativeUsage = if (apps.isNotEmpty()) {
                    apps.sumOf { it.numberOfEstimatedRequests }
                } else {
                    0
                }
                val appTop5 = apps.take(5)
                    .filter { it.numberOfEstimatedRequests > 0 }
                _top5Apps.addAll(appTop5)

            }.launchIn(viewModelScope)
    }

    private fun loadPrivacyLeakData(alsoRefreshPOIs: Boolean) {
        _privacyLeakDataAbsolut.clear()
        _privacyLeakDataScore.clear()
        privacyAssessmentJob?.cancel()


        privacyAssessmentJob = viewModelScope.launch(Dispatchers.IO) {
            var type = MetricType.ABSOLUT
            for (i in 0 until 2) {
                if (i == 1) {
                    type = MetricType.SCORE
                }
                withContext(Dispatchers.Main) {
                    _isLoading.value = true
                }

                if (alsoRefreshPOIs) {
                    privacyAssessmentUseCases.updatePOIs()
                }

                var result = mutableListOf<Pair<Int, Double>>()
                for ((index, metric) in _selectedMetrics.toList().withIndex()) {

                    if (index == 0) {
                        result = privacyAssessmentUseCases.doAssessment(
                            metric,
                            _metricInterval.value,
                            type
                        ).toMutableList()
                    } else {
                        val otherMetric = privacyAssessmentUseCases.doAssessment(
                            metric,
                            _metricInterval.value,
                            type
                        ).toMutableList()
                        //add other metrics to the first
                        for ((index, point) in otherMetric.withIndex()) {
                            result[index] =
                                Pair(result[index].first, result[index].second + point.second)
                        }
                    }
                }
                ensureActive()//ensure job is still active before updating the state
                if (_selectedMetrics.toList().size == 1 || type == MetricType.ABSOLUT) {
                    if (type == MetricType.ABSOLUT) {
                        _privacyLeakDataAbsolut.addAll(result)
                    } else {
                        _privacyLeakDataScore.addAll(result)
                    }
                } else {
                    //type is Score and there were mutiple metrics selected so avaerage the metrics, to maintain value range [0,1]
                    _privacyLeakDataScore.addAll(result.map { pair ->
                        Pair(
                            pair.first,
                            pair.second / _selectedMetrics.toList().size
                        )
                    })
                }

                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }

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


    /**
     * Dismisses the permission dialog from the queue.
     */
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    /**
     * Handles the result of a permission request and updates the visibility of permission dialogs.
     *
     * @param permission The permission being checked.
     * @param mainActivity The reference to the MainActivity.
     */
    fun onPermissionResult(
        permission: String,
        mainActivity: MainActivity
    ) {
        var isGranted = false
        if (ContextCompat.checkSelfPermission(mainActivity, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            isGranted = true
        }

        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    /**
     * Handles various events triggered in the dashboard.
     *
     * @param event The dashboard event to handle.
     */
    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.ToggleTracking -> {
                _trackingActive.value = event.running
            }

            is DashboardEvent.ToggleMetricDropDown -> {
                _metricSectionExpanded.value = !_metricSectionExpanded.value
            }

            is DashboardEvent.ChangeMetric -> {
                if (_selectedMetrics.contains(event.metric)) {
                    if (_selectedMetrics.size > 1) {
                        _selectedMetrics.remove(event.metric)
                    }
                } else {
                    _selectedMetrics.add(event.metric)
                }
                loadPrivacyLeakData(false)
            }

            is DashboardEvent.RefreshData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _isLoading.value = true
                    val usageStatsDeferred = async { updateUsageStats() }
                    usageStatsDeferred.await() // Wait for updateUsageStats() to complete
                    loadPrivacyLeakData(true)
                    //getTop5()
                    _isLoading.value = false
                }
            }

            is DashboardEvent.ChangeMetricInterval -> {
                _metricInterval.value = event.metricInterval
                loadPrivacyLeakData(false)
            }

            is DashboardEvent.DismissEnergyDialog -> {
                _energySaverDialogVisible.value = false
            }

            DashboardEvent.ToggleInfoDialog -> {
                _infoDialogVisible.value = !_infoDialogVisible.value
            }
        }
    }

}