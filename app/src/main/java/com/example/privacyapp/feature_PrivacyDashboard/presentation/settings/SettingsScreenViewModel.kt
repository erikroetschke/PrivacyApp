package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferences: PreferencesManager
) : ViewModel() {


    //states
    //POI detction
    private val _pOILimit = mutableStateOf(6f)
    val pOILimit = _pOILimit

    private val _pOIRadius = mutableStateOf(200f)
    val pOIRadius = _pOIRadius

    private val _minPOITime = mutableStateOf(3f)
    val minPOITime = _minPOITime

    private val _dynamicLimit =  mutableStateOf(true)
    val dynamicLimit = _dynamicLimit

    //POI frequency
    private val _maxPOIOccurrence = mutableStateOf(2f)
    val maxPOIOccurrence = _maxPOIOccurrence

    //other
    private var _changed = mutableStateOf(false)
    val changed = _changed

    init {
        //get Settings
        _pOILimit.value = preferences.getSettingInt(PreferencesManager.POI_LIMIT).toFloat()
        _pOIRadius.value = preferences.getSettingInt(PreferencesManager.POI_RADIUS).toFloat()
        _minPOITime.value = preferences.getSettingInt(PreferencesManager.MIN_POI_TIME).toFloat()
        _maxPOIOccurrence.value =
            preferences.getSettingInt(PreferencesManager.MAX_POI_OCCURRENCE).toFloat()
        _dynamicLimit.value = preferences.getSettingBool(PreferencesManager.DYNAMIC_LIMIT)

    }

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
            }

            is SettingsScreenEvent.ChangePOIRadius -> {
                _pOIRadius.value = event.value
                _changed.value = true
            }

            is SettingsScreenEvent.ToggleDynamicLimit -> {
                _dynamicLimit.value = !_dynamicLimit.value
                _changed.value = true

            }

            is SettingsScreenEvent.RestoreSettings -> {

                _pOILimit.value = 6f
                _minPOITime.value = 3f
                _pOIRadius.value = 200f
                _maxPOIOccurrence.value = 4f
                _dynamicLimit.value = true

                preferences.setSettingInt(PreferencesManager.MAX_POI_OCCURRENCE, 6)
                preferences.setSettingInt(PreferencesManager.MIN_POI_TIME, 3)
                preferences.setSettingInt(PreferencesManager.POI_RADIUS, 200)
                preferences.setSettingInt(PreferencesManager.POI_LIMIT, 4)
                preferences.setSettingBool(PreferencesManager.DYNAMIC_LIMIT, true)

                _changed.value = false
            }

            is SettingsScreenEvent.SaveSettings -> {
                viewModelScope.launch {
                    preferences.setSettingInt(
                        PreferencesManager.MAX_POI_OCCURRENCE,
                        _maxPOIOccurrence.value.toInt()
                    )
                    preferences.setSettingInt(
                        PreferencesManager.MIN_POI_TIME,
                        _minPOITime.value.toInt()
                    )
                    preferences.setSettingInt(PreferencesManager.POI_RADIUS, _pOIRadius.value.toInt())
                    preferences.setSettingInt(
                        PreferencesManager.POI_LIMIT,
                        _pOILimit.value.toInt()
                    )
                    preferences.setSettingBool(
                        PreferencesManager.DYNAMIC_LIMIT,
                        _dynamicLimit.value
                    )
                }
                _changed.value = false
            }
        }
    }
}