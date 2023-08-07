package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val preferences: PreferencesManager
) : ViewModel() {


    //states
    //POI detction
    private val _maxPOIPerDay = mutableStateOf(6f)
    val maxPOIPerDay = _maxPOIPerDay

    private val _pOIRadius = mutableStateOf(200f)
    val pOIRadius = _pOIRadius

    private val _minPOITime = mutableStateOf(3f)
    val minPOITime = _minPOITime

    //POI frequency
    private val _maxOccurrencePerDay = mutableStateOf(2f)
    val maxOccurrencePerDay = _maxOccurrencePerDay

    private val _maxOccurrencePerWeek = mutableStateOf(4f)
    val maxOccurrencePerWeek = _maxOccurrencePerWeek

    private val _maxOccurrencePerMonth = mutableStateOf(6f)
    val maxOccurrencePerMonth = _maxOccurrencePerMonth

    private var _changed = mutableStateOf(false)
    val changed = _changed

    init {
        //get Settings
        _maxPOIPerDay.value = preferences.getSetting(PreferencesManager.MAX_POI_PER_DAY).toFloat()
        _pOIRadius.value = preferences.getSetting(PreferencesManager.POI_RADIUS).toFloat()
        _minPOITime.value = preferences.getSetting(PreferencesManager.MIN_POI_TIME).toFloat()
        _maxOccurrencePerDay.value =
            preferences.getSetting(PreferencesManager.MAX_OCCURRENCE_PER_DAY).toFloat()
        _maxOccurrencePerWeek.value =
            preferences.getSetting(PreferencesManager.MAX_OCCURRENCE_PER_WEEK).toFloat()
        _maxOccurrencePerMonth.value =
            preferences.getSetting(PreferencesManager.MAX_OCCURRENCE_PER_MONTH).toFloat()

    }

    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.ChangeMaxPOIPerDay -> {
                _maxPOIPerDay.value = event.value
                _changed.value = true
            }

            is SettingsScreenEvent.ChangeMaxOccurrencePerDay -> {
                _maxOccurrencePerDay.value = event.value
                _changed.value = true
            }

            is SettingsScreenEvent.ChangeMaxOccurrencePerMonth -> {
                _maxOccurrencePerMonth.value = event.value
                _changed.value = true
            }

            is SettingsScreenEvent.ChangeMaxOccurrencePerWeek -> {
                _maxOccurrencePerWeek.value = event.value
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

            is SettingsScreenEvent.RestoreSettings -> {

                _maxPOIPerDay.value = 6f
                _minPOITime.value = 3f
                _pOIRadius.value = 200f
                _maxOccurrencePerDay.value = 2f
                _maxOccurrencePerMonth.value = 6f
                _maxOccurrencePerWeek.value = 4f

                preferences.setSetting(PreferencesManager.MAX_OCCURRENCE_PER_DAY, 6)
                preferences.setSetting(PreferencesManager.MIN_POI_TIME, 3)
                preferences.setSetting(PreferencesManager.POI_RADIUS, 200)
                preferences.setSetting(PreferencesManager.MAX_POI_PER_DAY, 2)
                preferences.setSetting(PreferencesManager.MAX_OCCURRENCE_PER_WEEK, 4)
                preferences.setSetting(PreferencesManager.MAX_OCCURRENCE_PER_MONTH, 6)

                _changed.value = false
            }

            is SettingsScreenEvent.SaveSettings -> {
                viewModelScope.launch {
                    preferences.setSetting(
                        PreferencesManager.MAX_OCCURRENCE_PER_DAY,
                        _maxOccurrencePerDay.value.toInt()
                    )
                    preferences.setSetting(
                        PreferencesManager.MIN_POI_TIME,
                        _minPOITime.value.toInt()
                    )
                    preferences.setSetting(PreferencesManager.POI_RADIUS, _pOIRadius.value.toInt())
                    preferences.setSetting(
                        PreferencesManager.MAX_POI_PER_DAY,
                        _maxPOIPerDay.value.toInt()
                    )
                    preferences.setSetting(
                        PreferencesManager.MAX_OCCURRENCE_PER_WEEK,
                        _maxOccurrencePerWeek.value.toInt()
                    )
                    preferences.setSetting(
                        PreferencesManager.MAX_OCCURRENCE_PER_MONTH,
                        _maxOccurrencePerMonth.value.toInt()
                    )
                }
                _changed.value = false
            }
        }
    }
}