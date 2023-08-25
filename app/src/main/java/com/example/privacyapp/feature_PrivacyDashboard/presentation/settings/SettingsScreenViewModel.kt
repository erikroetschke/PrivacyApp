package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

import android.app.ActivityManager
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * ViewModel class for the settings screen, responsible for managing UI states and events related to settings.
 *
 * @param preferences The manager for interacting with user preferences.
 * @param privacyAssessmentUseCases Use cases related to privacy assessment operations.
 */
@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferences: PreferencesManager,
    private val privacyAssessmentUseCases: PrivacyAssessmentUseCases
) : ViewModel() {

    //states
    //general
    private val _isCoarseLocationRelevant = mutableStateOf(false)
    val isCoarseLocationRelevant = _isCoarseLocationRelevant

    private val _locationTrackingInterval = mutableStateOf(45f)
    val locationTrackingInterval = _locationTrackingInterval

    //POI detction
    private val _pOILimit = mutableStateOf(6f)
    val pOILimit = _pOILimit

    private val _pOIRadius = mutableStateOf(200)
    val pOIRadius = _pOIRadius

    private val _minPOITime = mutableStateOf(3)
    val minPOITime = _minPOITime

    private val _dynamicLimit = mutableStateOf(true)
    val dynamicLimit = _dynamicLimit

    //POI frequency
    private val _maxPOIOccurrence = mutableStateOf(2f)
    val maxPOIOccurrence = _maxPOIOccurrence

    //value changes
    private val _changed = mutableStateOf(false)
    val changed = _changed

    private val _trackingIntervalChanged = mutableStateOf(false)
    val trackingIntervalChanged = _trackingIntervalChanged

    private val _pOISettingsChanged = mutableStateOf(false)
    val pOISettingsChanged = _pOISettingsChanged

    private val _valuesSaved = mutableStateOf(false)
    val valuesSaved = _valuesSaved

    //InfoDialog
    private val _infoDialogState = mutableStateOf(SettingsInfoDialogState())
    val infoDialogState = _infoDialogState

    //loading
    private val _loading = mutableStateOf(false)
    val loading = _loading

    init {
        //get Settings
        _pOILimit.value = preferences.getSettingInt(PreferencesManager.POI_LIMIT).toFloat()
        _pOIRadius.value = preferences.getSettingInt(PreferencesManager.POI_RADIUS)
        _minPOITime.value = preferences.getSettingInt(PreferencesManager.MIN_POI_TIME)
        _maxPOIOccurrence.value =
            preferences.getSettingInt(PreferencesManager.MAX_POI_OCCURRENCE).toFloat()
        _dynamicLimit.value = preferences.getSettingBool(PreferencesManager.DYNAMIC_LIMIT)
        _locationTrackingInterval.value = preferences.getSettingInt(PreferencesManager.LOCATION_TRACKING_INTERVAL).toFloat()
        _isCoarseLocationRelevant.value = preferences.getSettingBool(PreferencesManager.IS_COARSE_LOCATION_RELEVANT)
    }

    /**
     * Handles events triggered by UI interactions.
     *
     * @param event The event triggered by the UI interaction.
     */
    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.ChangeMaxPOIPerDay -> {
                _pOILimit.value = event.value
                _changed.value = true
            }

            is SettingsScreenEvent.ChangeMaxPOIOccurrence -> {
                _maxPOIOccurrence.value = event.value
                _changed.value = true
            }


            is SettingsScreenEvent.ChangeMinPOITime -> {
                _minPOITime.value = event.value
                _changed.value = true
                _pOISettingsChanged.value = true
            }

            is SettingsScreenEvent.ChangePOIRadius -> {
                _pOIRadius.value = event.value
                _changed.value = true
                _pOISettingsChanged.value = true
            }

            is SettingsScreenEvent.ToggleDynamicLimit -> {
                _dynamicLimit.value = !_dynamicLimit.value
                _changed.value = true
            }

            is SettingsScreenEvent.ChangeLocationTrackingInterval -> {
                _locationTrackingInterval.value = event.value
                _changed.value = true
                _trackingIntervalChanged.value = true
            }

            is SettingsScreenEvent.ToggleCoarseLocationIsRelevant -> {
                _isCoarseLocationRelevant.value = !_isCoarseLocationRelevant.value
                _changed.value = true
            }

            is SettingsScreenEvent.RestoreSettings -> {

                _pOILimit.value = 6f
                _minPOITime.value = 3
                _pOIRadius.value = 200
                _maxPOIOccurrence.value = 4f
                _dynamicLimit.value = true
                _isCoarseLocationRelevant.value = false
                _locationTrackingInterval.value = 45f

                preferences.setSettingInt(PreferencesManager.MAX_POI_OCCURRENCE, 6)
                preferences.setSettingInt(PreferencesManager.MIN_POI_TIME, 3)
                preferences.setSettingInt(PreferencesManager.POI_RADIUS, 200)
                preferences.setSettingInt(PreferencesManager.POI_LIMIT, 4)
                preferences.setSettingBool(PreferencesManager.DYNAMIC_LIMIT, true)
                preferences.setSettingInt(PreferencesManager.LOCATION_TRACKING_INTERVAL, 45)
                preferences.setSettingBool(PreferencesManager.IS_COARSE_LOCATION_RELEVANT, false)

                _changed.value = false
            }

            is SettingsScreenEvent.SaveSettings -> {
                viewModelScope.launch {
                    preferences.setSettingInt(
                        PreferencesManager.MAX_POI_OCCURRENCE,
                        _maxPOIOccurrence.value.roundToInt()
                    )
                    preferences.setSettingInt(
                        PreferencesManager.MIN_POI_TIME,
                        _minPOITime.value
                    )
                    preferences.setSettingInt(PreferencesManager.POI_RADIUS, _pOIRadius.value)
                    preferences.setSettingInt(
                        PreferencesManager.POI_LIMIT,
                        _pOILimit.value.roundToInt()
                    )
                    preferences.setSettingBool(
                        PreferencesManager.DYNAMIC_LIMIT,
                        _dynamicLimit.value
                    )
                    preferences.setSettingBool(
                        PreferencesManager.IS_COARSE_LOCATION_RELEVANT,
                        _isCoarseLocationRelevant.value
                    )
                    preferences.setSettingInt(
                        PreferencesManager.LOCATION_TRACKING_INTERVAL,
                        _locationTrackingInterval.value.roundToInt()
                    )
                }
                _changed.value = false
                _valuesSaved.value = true
            }

            is SettingsScreenEvent.TriggerInfoDialog -> {
                when (event.pref) {
                    PreferencesManager.IS_COARSE_LOCATION_RELEVANT -> {_infoDialogState.value = _infoDialogState.value.copy(coarseLocationRelevantInfoDialogVisible = !_infoDialogState.value.coarseLocationRelevantInfoDialogVisible)}
                    PreferencesManager.MIN_POI_TIME -> {_infoDialogState.value = _infoDialogState.value.copy(minPOITimeInfoDialogVisible = !_infoDialogState.value.minPOITimeInfoDialogVisible)}
                    PreferencesManager.LOCATION_TRACKING_INTERVAL -> {_infoDialogState.value = _infoDialogState.value.copy(locationTrackingIntervalInfoDialogVisible = !_infoDialogState.value.locationTrackingIntervalInfoDialogVisible)}
                    PreferencesManager.DYNAMIC_LIMIT -> {_infoDialogState.value = _infoDialogState.value.copy(dynamicLimitInfoDialogVisible = !_infoDialogState.value.dynamicLimitInfoDialogVisible)}
                    PreferencesManager.MAX_POI_OCCURRENCE -> {_infoDialogState.value = _infoDialogState.value.copy(maxPOIOccurrenceInfoDialogVisible = !_infoDialogState.value.maxPOIOccurrenceInfoDialogVisible)}
                    PreferencesManager.POI_RADIUS -> {_infoDialogState.value = _infoDialogState.value.copy(pOIRadiusInfoDialogVisible = !_infoDialogState.value.pOIRadiusInfoDialogVisible)}
                    PreferencesManager.POI_LIMIT -> {_infoDialogState.value = _infoDialogState.value.copy(pOILimitInfoDialogVisible = !_infoDialogState.value.pOILimitInfoDialogVisible)}
                }
            }

            SettingsScreenEvent.ToggleValuesSaved -> {
                _valuesSaved.value = !_valuesSaved.value
            }

            SettingsScreenEvent.TogglePOISettingsChanged -> {
                _pOISettingsChanged.value = !_pOISettingsChanged.value
            }
            SettingsScreenEvent.ToggleTrackingIntervalChanged -> {
                _trackingIntervalChanged.value = !_trackingIntervalChanged.value
            }

            SettingsScreenEvent.RecomputePOIsWithNewParameters -> {
                viewModelScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main){
                        _loading.value = true
                    }
                    privacyAssessmentUseCases.recomputePOIs()
                    withContext(Dispatchers.Main){
                        _loading.value = false
                    }
                }
            }
        }
    }
}