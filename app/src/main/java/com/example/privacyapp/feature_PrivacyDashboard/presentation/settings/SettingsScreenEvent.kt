package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

/**
 * Sealed class representing events that can occur within the settings screen.
 */
sealed class SettingsScreenEvent {

    /**
     * Event to change the maximum number of Points of Interest (POIs) per day.
     *
     * @param value The new value for the maximum POIs per day.
     */
    data class ChangeMaxPOIPerDay(val value: Float) : SettingsScreenEvent()

    /**
     * Event to change the location tracking interval.
     *
     * @param value The new value for the location tracking interval.
     */
    data class ChangeLocationTrackingInterval(val value: Float): SettingsScreenEvent()

    /**
     * Event to change the POI radius.
     *
     * @param value The new value for the POI radius.
     */
    data class ChangePOIRadius(val value: Int): SettingsScreenEvent()

    /**
     * Event to change the minimum POI time.
     *
     * @param value The new value for the minimum POI time.
     */
    data class ChangeMinPOITime(val value: Int): SettingsScreenEvent()

    /**
     * Event to change the maximum POI occurrence value.
     *
     * @param value The new value for the maximum POI occurrence.
     */
    data class ChangeMaxPOIOccurrence(val value: Float): SettingsScreenEvent()

    /**
     * Event to trigger an info dialog for a specific preference.
     *
     * @param pref The preference for which the info dialog is triggered.
     */
    data class TriggerInfoDialog(val pref: String): SettingsScreenEvent()

    /**
     * Event to save the current settings.
     */
    object SaveSettings: SettingsScreenEvent()

    /**
     * Event to restore the settings to their default values.
     */
    object RestoreSettings: SettingsScreenEvent()

    /**
     * Event to toggle dynamic limit for POIs.
     */
    object ToggleDynamicLimit: SettingsScreenEvent()

    /**
     * Event to toggle the relevance of coarse location.
     */
    object ToggleCoarseLocationIsRelevant: SettingsScreenEvent()

    /**
     * Event to toggle the flag indicating that values have been saved.
     */
    object ToggleValuesSaved : SettingsScreenEvent()

    /**
     * Event to toggle the flag indicating that tracking interval has changed.
     */
    object ToggleTrackingIntervalChanged : SettingsScreenEvent()

    /**
     * Event to toggle the flag indicating that POI settings have changed.
     */
    object TogglePOISettingsChanged : SettingsScreenEvent()

    /**
     * Event to trigger the recomputation of POIs with new parameters.
     */
    object RecomputePOIsWithNewParameters : SettingsScreenEvent()
}