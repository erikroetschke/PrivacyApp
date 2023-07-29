package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.DefaultRadioButton

@Composable
fun MetricSection(
    modifier: Modifier = Modifier,
    metric: Metric,
    metricInterval: MetricInterval,
    onMetricChange: (Metric, MetricInterval) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            enumValues<Metric>().forEach {
                DefaultRadioButton(
                    text = it.metricName,
                    selected = it == metric,
                    onSelect = { onMetricChange(it, metricInterval) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = "24h",
                selected = metricInterval == MetricInterval.DAY,
                onSelect = {
                    onMetricChange(metric, MetricInterval.DAY)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "7d",
                selected = metricInterval == MetricInterval.WEEK,
                onSelect = {
                    onMetricChange(metric, MetricInterval.WEEK)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "1m",
                selected = metricInterval == MetricInterval.MONTH,
                onSelect = {
                    onMetricChange(metric, MetricInterval.MONTH)
                }
            )
        }
    }
}