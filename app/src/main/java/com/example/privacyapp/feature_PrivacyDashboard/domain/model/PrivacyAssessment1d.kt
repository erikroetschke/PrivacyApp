package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity

/**
 * Data class representing a privacy assessment for a specific metric over a day.
 *
 * @property timestampStart The starting timestamp of the assessment period.
 * @property metricName The name of the metric for which the assessment was conducted.
 * @property metricValue The value of the privacy metric assessed.
 */
@Entity(primaryKeys = ["metricName", "timestampStart"])
data class PrivacyAssessment1d(
    val timestampStart: Long,
    val metricName: String,
    val metricValue: Double
)