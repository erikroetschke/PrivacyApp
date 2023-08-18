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

    override  fun setSettingInt(key: String, value: Int) {
        with (sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun getSettingInt(key: String): Int {
        return sharedPref.getInt(key, getDefaultValuesInt(key))
    }

    override fun setSettingBool(key: String, value: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun getSettingBool(key: String): Boolean {
        return sharedPref.getBoolean(key, getDefaultValuesBool(key))
    }

    private fun getDefaultValuesInt(key: String): Int {
        return when (key) {
            "maxPOIPerDay" -> 6
            "pOIRadius" -> 200
            "minPOITime" -> 3
            "maxPOIOccurrence" -> 4
            else -> throw NoDefaultSettingsDefinedForMetric("You must assign default values in the DataStoreImpl for metric Settings")
        }
    }

    private fun getDefaultValuesBool(key: String): Boolean {
        return when (key) {
            "dynamicLimit" -> true
            else -> throw NoDefaultSettingsDefinedForMetric("You must assign default values in the DataStoreImpl for metric Settings")
        }
    }
}

class NoDefaultSettingsDefinedForMetric(message:String) : Exception(message)