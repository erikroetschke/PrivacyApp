package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

sealed class SettingsScreenEvent {

    data class ChangeMaxPOIPerDay(val value: Float) :SettingsScreenEvent()

    data class ChangeLocationTrackingInterval(val value: Float): SettingsScreenEvent()

    data class ChangePOIRadius(val value: Int):SettingsScreenEvent()

    data class ChangeMinPOITime(val value: Int):SettingsScreenEvent()

    data class ChangeMaxPOIOccurrence(val value: Float):SettingsScreenEvent()

    data class TriggerInfoDialog(val pref: String): SettingsScreenEvent()


    object SaveSettings: SettingsScreenEvent()

    object RestoreSettings: SettingsScreenEvent()

    object ToggleDynamicLimit: SettingsScreenEvent()

    object ToggleCoarseLocationIsRelevant: SettingsScreenEvent()

    object ToggleValuesSaved : SettingsScreenEvent()

    object ToggleTrackingIntervalChanged : SettingsScreenEvent()

    object TogglePOISettingsChanged : SettingsScreenEvent()

    object RecomputePOIsWithNewParameters : SettingsScreenEvent()
}