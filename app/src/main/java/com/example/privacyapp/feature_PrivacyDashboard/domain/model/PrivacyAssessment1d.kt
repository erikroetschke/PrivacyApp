package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity

@Entity(primaryKeys = ["metricName", "timestampStart"])
data class PrivacyAssessment1d(
    val timestampStart: Long,
    val metricName: String,
    val metricValue: Double
)