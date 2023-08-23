package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

interface PreferencesManager {

    companion object {
        const val POI_LIMIT = "maxPOIPerDay"
        const val POI_RADIUS = "pOIRadius"
        const val MIN_POI_TIME = "minPOITime"
        const val MAX_POI_OCCURRENCE = "maxPOIOccurrence"
        const val DYNAMIC_LIMIT = "dynamicLimit"
        const val IS_COARSE_LOCATION_RELEVANT = "isCoarseLocationRelevant"
        const val LOCATION_TRACKING_INTERVAL = "locationTrackingInterval"
    }

    fun setSettingInt(key: String, value: Int)
    fun getSettingInt(key: String): Int

    fun setSettingBool(key: String, value: Boolean)
    fun getSettingBool(key: String): Boolean
}