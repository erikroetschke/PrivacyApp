package com.example.privacyapp.feature_PrivacyDashboard.domain.util

enum class Metric(
    var metricName: String,
    var metricDescription: String,
    var weighting: Int,//could be used to combine multiple metrics
    var maxValue: Float
) {

    StopDetection("POI Detection","This Metric searches for POI in your location Data and its output is the number of the found POIs",1, 8.toFloat()),
    StopFrequency("POI Frequency","This Metric searches for repeating POIs in your location Data and its output is the frequency",1, 8.toFloat());


}
