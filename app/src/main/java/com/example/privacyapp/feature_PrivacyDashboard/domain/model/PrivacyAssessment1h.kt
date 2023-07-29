package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity

@Entity(primaryKeys = ["metricName", "timestampStart"])
data class PrivacyAssessment1h(
    val timestampStart: Long,
    val metricName: String,
    val metricDescription: String,
    val metricValue: Double,
    val weighting: Int
)