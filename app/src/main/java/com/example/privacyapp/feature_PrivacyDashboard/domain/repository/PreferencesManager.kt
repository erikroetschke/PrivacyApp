package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

interface PreferencesManager {

    companion object {
        const val POI_LIMIT = "maxPOIPerDay"
        const val POI_RADIUS = "pOIRadius"
        const val MIN_POI_TIME = "minPOITime"
        const val MAX_OCCURRENCE_PER_DAY = "maxOccurrencePerDay"
        const val MAX_OCCURRENCE_PER_WEEK = "maxOccurrencePerWeek"
        const val MAX_OCCURRENCE_PER_MONTH = "maxOccurrencePerMonth"
        const val DYNAMIC_LIMIT = "dynamicLimit"
    }

    fun setSettingInt(key: String, value: Int)
    fun getSettingInt(key: String): Int

    fun setSettingBool(key: String, value: Boolean)
    fun getSettingBool(key: String): Boolean
}