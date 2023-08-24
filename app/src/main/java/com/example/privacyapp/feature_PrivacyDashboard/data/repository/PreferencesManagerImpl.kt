package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import android.content.Context
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

/**
 * Class handles Shared preferences for Settings
 */
class PreferencesManagerImpl(context: Context) : PreferencesManager {

    private val sharedPref = context.getSharedPreferences(
        "PrivacyApp.Settings", Context.MODE_PRIVATE)

    /**
     * Set an integer setting value in the shared preferences.
     *
     * @param key The key for the setting (use constants from [PreferencesManager]).
     * @param value The integer value to be set.
     */
    override  fun setSettingInt(key: String, value: Int) {
        with (sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    /**
     * Get an integer setting value from the shared preferences.
     *
     * @param key The key for the setting (use constants from [PreferencesManager]).
     * @return The integer value of the setting, or the default value if not found.
     */
    override fun getSettingInt(key: String): Int {
        return sharedPref.getInt(key, getDefaultValuesInt(key))
    }

    /**
     * Set a boolean setting value in the shared preferences.
     *
     * @param key The key for the setting (use constants from [PreferencesManager]).
     * @param value The boolean value to be set.
     */
    override fun setSettingBool(key: String, value: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    /**
     * Get a boolean setting value from the shared preferences.
     *
     * @param key The key for the setting (use constants from [PreferencesManager]).
     * @return The boolean value of the setting, or the default value if not found.
     */
    override fun getSettingBool(key: String): Boolean {
        return sharedPref.getBoolean(key, getDefaultValuesBool(key))
    }

    /**
     * Get the default integer value for the specified setting key.
     *
     * @param key The key for the setting.
     * @return The default integer value for the setting.
     * @throws NoDefaultSettingsDefinedForMetric If no default value is defined for the metric setting.
     */
    private fun getDefaultValuesInt(key: String): Int {
        return when (key) {
            "maxPOIPerDay" -> 6
            "pOIRadius" -> 200
            "minPOITime" -> 3
            "maxPOIOccurrence" -> 4
            "locationTrackingInterval" -> 45
            else -> throw NoDefaultSettingsDefinedForMetric("You must assign default values in the DataStoreImpl for metric Settings")
        }
    }

    /**
     * Get the default boolean value for the specified setting key.
     *
     * @param key The key for the setting.
     * @return The default boolean value for the setting.
     * @throws NoDefaultSettingsDefinedForMetric If no default value is defined for the metric setting.
     */
    private fun getDefaultValuesBool(key: String): Boolean {
        return when (key) {
            "dynamicLimit" -> true
            "isCoarseLocationRelevant" -> false
            else -> throw NoDefaultSettingsDefinedForMetric("You must assign default values")
        }
    }
}

/**
 * Exception thrown when no default settings are defined for a metric.
 *
 * @param message The exception message.
 */
class NoDefaultSettingsDefinedForMetric(message:String) : Exception(message)