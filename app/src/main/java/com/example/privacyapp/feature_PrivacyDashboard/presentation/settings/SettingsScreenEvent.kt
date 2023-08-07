package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

sealed class SettingsScreenEvent {

    data class ChangeMaxPOIPerDay(val value: Float) :SettingsScreenEvent()

    data class ChangePOIRadius(val value: Float):SettingsScreenEvent()

    data class ChangeMinPOITime(val value: Float):SettingsScreenEvent()

    data class ChangeMaxOccurrencePerDay(val value: Float):SettingsScreenEvent()

    data class ChangeMaxOccurrencePerWeek(val value: Float):SettingsScreenEvent()

    data class ChangeMaxOccurrencePerMonth(val value: Float):SettingsScreenEvent()

    object SaveSettings: SettingsScreenEvent()

    object RestoreSettings: SettingsScreenEvent()
}