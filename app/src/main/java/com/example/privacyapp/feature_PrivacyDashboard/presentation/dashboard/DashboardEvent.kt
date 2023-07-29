package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval

sealed class DashboardEvent {

    data class ToggleTracking(val running: Boolean): DashboardEvent()
    object ToggleMetricDropDown :DashboardEvent()
    data class ChangeMetricDropdown(val metric: Metric, val metricInterval: MetricInterval):DashboardEvent()
    object RefreshData : DashboardEvent()

}
