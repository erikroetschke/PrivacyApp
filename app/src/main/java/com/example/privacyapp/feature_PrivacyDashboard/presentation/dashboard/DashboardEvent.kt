package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard

import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval

/**
 * A sealed class representing different events that can occur in a dashboard.
 */
sealed class DashboardEvent {

    /**
     * Event indicating the user's intention to toggle tracking (start or stop).
     *
     * @param running Indicates whether the tracking is being started (true) or stopped (false).
     */
    data class ToggleTracking(val running: Boolean) : DashboardEvent()

    /**
     * Event indicating the user's intention to toggle the visibility of the metric dropdown.
     */
    object ToggleMetricDropDown : DashboardEvent()

    /**
     * Event indicating the user's intention to change the metric being displayed.
     *
     * @param metric The new metric to be displayed.
     */
    data class ChangeMetric(val metric: Metric) : DashboardEvent()

    /**
     * Event indicating the user's intention to change the metric interval.
     *
     * @param metricInterval The new metric interval to be displayed.
     */
    data class ChangeMetricInterval(val metricInterval: MetricInterval) : DashboardEvent()

    /**
     * Event indicating the user's intention to refresh the data displayed in the dashboard.
     */
    object RefreshData : DashboardEvent()

    /**
     * Event indicating the user's intention to dismiss the energy dialog.
     */
    object DismissEnergyDialog : DashboardEvent()

    /**
     * Event indicating the user's intention to view the info dialog.
     */
    object ToggleInfoDialog : DashboardEvent()
}
