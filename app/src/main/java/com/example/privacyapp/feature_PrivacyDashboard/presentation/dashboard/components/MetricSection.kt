package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.DefaultRadioButton

/**
 * Composable function to display a section for selecting metrics and metric intervals.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param metrics The list of available metrics to select from.
 * @param metricInterval The currently selected metric interval.
 * @param onMetricChange Callback function when a metric is selected or deselected.
 * @param onMetricIntervalChange Callback function when a metric interval is selected.
 */
@Composable
fun MetricSection(
    modifier: Modifier = Modifier,
    metrics: List<Metric>,
    metricInterval: MetricInterval,
    onMetricChange: (Metric) -> Unit,
    onMetricIntervalChange: (MetricInterval) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Metric: ")
            enumValues<Metric>().forEach {
                DefaultRadioButton(
                    text = it.metricName,
                    selected = metrics.contains(it),
                    onSelect = { onMetricChange(it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Interval: ")
            DefaultRadioButton(
                text = "24h",
                selected = metricInterval == MetricInterval.DAY,
                onSelect = {
                    onMetricIntervalChange(MetricInterval.DAY)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "7d",
                selected = metricInterval == MetricInterval.WEEK,
                onSelect = {
                    onMetricIntervalChange(MetricInterval.WEEK)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "1mon",
                selected = metricInterval == MetricInterval.MONTH,
                onSelect = {
                    onMetricIntervalChange(MetricInterval.MONTH)
                }
            )
        }
    }
}