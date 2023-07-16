package com.example.privacyapp.feature_PrivacyDashboard.domain.util

sealed class Metric(
    val metricName: String,
    val metricDescription: String,
    val weighting: Int //could be used to combine multiple metrics
) {
object StopDetection: Metric("POI Detection", "This Metric searches for POI in your location Data and its output is the number of the found POIs", 1)
}
