package com.example.privacyapp.feature_PrivacyDashboard.domain.util

/**
 * Represents different metrics used in privacy assessment.
 * @param metricName The name of the metric.
 * @param metricDescription The description of the metric.
 * @param weighting The weighting of the metric (used for combination).
 * @param maxValue The maximum value of the metric.
 */
enum class Metric(
    var metricName: String,
    var metricDescription: String,
    var weighting: Int,//could be used to combine multiple metrics
    var maxValue: Float
) {

    StopDetection("POI Detection","This Metric searches for POI in your location Data and its output is the number of the found POIs",1, 8.toFloat()),
    StopFrequency("POI Frequency","This Metric searches for repeating POIs in your location Data and its output is the frequency",1, 8.toFloat());


}
