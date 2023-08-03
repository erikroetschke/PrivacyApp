package com.example.privacyapp.feature_PrivacyDashboard.presentation.map

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val privacyAssessmentUseCases: PrivacyAssessmentUseCases
    ) :ViewModel() {

    //state
    private val _pois = mutableStateListOf<POI>()
    val pois = _pois

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            _pois.addAll(privacyAssessmentUseCases.extractPOIsLast24h())
            _isLoading.value = false
        }
    }
}

