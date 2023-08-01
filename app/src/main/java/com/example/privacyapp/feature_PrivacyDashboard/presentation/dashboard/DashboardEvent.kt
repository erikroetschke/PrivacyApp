package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType

sealed class DashboardEvent {

    data class ToggleTracking(val running: Boolean): DashboardEvent()
    object ToggleMetricDropDown :DashboardEvent()
    data class ChangeMetric(val metric: Metric):DashboardEvent()
    data class ChangeMetricInterval(val metricInterval: MetricInterval):DashboardEvent()
    object RefreshData : DashboardEvent()

    data class ChangeMetricType(val metricType: MetricType) :DashboardEvent()

}
