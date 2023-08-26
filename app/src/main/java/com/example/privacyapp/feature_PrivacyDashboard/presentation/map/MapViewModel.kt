package com.example.privacyapp.feature_PrivacyDashboard.presentation.map

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * ViewModel class responsible for managing the state and business logic of the Map screen.
 *
 * @param privacyAssessmentUseCases The use cases related to privacy assessment operations.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val privacyAssessmentUseCases: PrivacyAssessmentUseCases
) : ViewModel() {

    // State for the list of Points of Interest (POIs)
    private val _pois = mutableStateListOf<POI>()
    val pois = _pois

    // State for tracking loading status
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    // State for the selected metric interval
    private val _metricInterval = mutableStateOf(MetricInterval.DAY)
    val metricInterval = _metricInterval

    /**
     * Initializes the ViewModel by fetching POIs based on the selected metric interval.
     */
    init {
        getPOi(getStartTimestamp(_metricInterval.value))
    }

    /**
     * Handles the change of the metric interval and fetches corresponding POIs.
     */
    fun onMetricIntervalChange(metricInterval: MetricInterval) {
        _metricInterval.value = metricInterval
        getPOi((getStartTimestamp(_metricInterval.value)))
    }

    /**
     * Fetches Points of Interest (POIs) based on the provided timestamp.
     *
     * @param timestamp The timestamp to fetch POIs since.
     */
    private fun getPOi(timestamp: Long) {

        //viewModelScope.launch {
        //_isLoading.value = true
        privacyAssessmentUseCases.getPOISinceTimestampAsFlow(timestamp).onEach { pois ->
            _pois.clear()
            _pois.addAll(pois)
        }.launchIn(viewModelScope)
        //_isLoading.value = false
        // }
    }


    /**
     * returns the timestamp where the pois should start, depending on the metricInterval
     * @param metricInterval metricInterval
     * @return unix epoche timestamp
     */
    private fun getStartTimestamp(metricInterval: MetricInterval): Long {
        //utils find start timestamp
        val currentTime = System.currentTimeMillis()
        val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val minutesFromCompleteHour = timeDate.minute * 60 * 1000
        val minutesToCompleteHour = (60 - timeDate.minute) * 60 * 1000
        val secondsFromCompleteMinute = timeDate.second * 1000
        val hoursToCompleteDay = (24 - timeDate.hour) * 60 * 60 * 1000

        return when (metricInterval) {
            MetricInterval.DAY -> {
                //start Timestamp for the Interval current time - 24h + minutesToCompleteHour - seconds
                currentTime - (1000 * 60 * 60 * 24) + minutesToCompleteHour - secondsFromCompleteMinute
            }

            MetricInterval.WEEK -> {
                //start Timestamp current time - 7days + hoursToCompleteDay - hoursOfDay - minutes - seconds
                currentTime - (1000 * 60 * 60 * 24 * 7) + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
            }

            MetricInterval.MONTH -> {
                //timestamp current time - 1month + hoursToCompleteDay - hoursOfDay - minutes - seconds
                ChronoUnit.MILLIS.between(
                    Instant.EPOCH,
                    Instant.now().atZone(ZoneId.systemDefault()).minusMonths(1)
                ) + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
            }

        }
    }
}

