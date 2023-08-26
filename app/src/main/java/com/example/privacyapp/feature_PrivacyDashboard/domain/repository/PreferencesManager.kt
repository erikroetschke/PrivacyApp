package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

/**
 * A interface for managing user preferences.
 */
interface PreferencesManager {
    companion object {
        // Keys for various settings
        const val POI_LIMIT = "maxPOIPerDay"
        const val POI_RADIUS = "pOIRadius"
        const val MIN_POI_TIME = "minPOITime"
        const val MAX_POI_OCCURRENCE = "maxPOIOccurrence"
        const val DYNAMIC_LIMIT = "dynamicLimit"
        const val IS_COARSE_LOCATION_RELEVANT = "isCoarseLocationRelevant"
        const val LOCATION_TRACKING_INTERVAL = "locationTrackingInterval"
    }

    /**
     * Sets an integer value for the specified preference key.
     *
     * @param key The preference key.
     * @param value The integer value to be set.
     */
    fun setSettingInt(key: String, value: Int)

    /**
     * Retrieves an integer value for the specified preference key.
     *
     * @param key The preference key.
     * @return The integer value associated with the key.
     */
    fun getSettingInt(key: String): Int

    /**
     * Sets a boolean value for the specified preference key.
     *
     * @param key The preference key.
     * @param value The boolean value to be set.
     */
    fun setSettingBool(key: String, value: Boolean)

    /**
     * Retrieves a boolean value for the specified preference key.
     *
     * @param key The preference key.
     * @return The boolean value associated with the key.
     */
    fun getSettingBool(key: String): Boolean
}