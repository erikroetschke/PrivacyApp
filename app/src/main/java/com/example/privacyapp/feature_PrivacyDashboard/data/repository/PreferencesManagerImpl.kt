package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import android.app.Application
import android.content.Context
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

/**
 * Class handles Shared preferences for Settings
 */
class PreferencesManagerImpl(context: Context) : PreferencesManager {

    private val sharedPref = context.getSharedPreferences(
        "PrivacyApp.Settings", Context.MODE_PRIVATE)


    override  fun setSetting(key: String, value: Int) {
        with (sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun getSetting(key: String): Int {
        return sharedPref.getInt(key, getDefaultValues(key))
    }

    private fun getDefaultValues(key: String): Int {
        return when (key) {
            "maxPOIPerDay" -> 6
            "pOIRadius" -> 200
            "minPOITime" -> 3
            "maxOccurrencePerDay" -> 2
            "maxOccurrencePerWeek" -> 4
            "maxOccurrencePerMonth" -> 6
            else -> throw NoDefaultSettingsDefinedForMetric("You must assign default values in the DataStoreImpl for metric Settings")
        }
    }
}

class NoDefaultSettingsDefinedForMetric(message:String) : Exception(message)