package com.example.privacyapp.feature_PrivacyDashboard.domain.util

enum class Metric {

    StopDetection("POI Detection","This Metric searches for POI in your location Data and its output is the number of the found POIs",1);

    var metricName: String = "POI Detection"
    var metricDescription: String = "This Metric searches for POI in your location Data and its output is the number of the found POIs"
    var weighting: Int = 1//could be used to combine multiple metrics

    constructor(metricName: String, metricDescription: String, weighting: Int) {
        this.metricName = metricName
        this.metricDescription = metricDescription
        this.weighting = weighting
    }


}
