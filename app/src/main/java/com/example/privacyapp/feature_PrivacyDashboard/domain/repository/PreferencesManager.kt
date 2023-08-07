package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    companion object {
        const val MAX_POI_PER_DAY = "maxPOIPerDay"
        const val POI_RADIUS = "pOIRadius"
        const val MIN_POI_TIME = "minPOITime"
        const val MAX_OCCURRENCE_PER_DAY = "maxOccurrencePerDay"
        const val MAX_OCCURRENCE_PER_WEEK = "maxOccurrencePerWeek"
        const val MAX_OCCURRENCE_PER_MONTH = "maxOccurrencePerMonth"
    }

    fun setSetting(key: String, value: Int)
    fun getSetting(key: String): Int
}