package com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisTickComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.createHorizontalAxis
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.util.Calendar

@Composable
fun lineDiagram(
    modifier: Modifier,
    yPoints: List<FloatEntry>
) {
    val chartEntryModel = entryModelOf(yPoints)
    val lastHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 1
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> ((x+lastHour) % 24).toInt().toString() }
    val lineColor = Color.Green
    val chartColor = listOf(lineColor)

    Column(
        modifier = modifier
    ) {
        ProvideChartStyle(rememberChartStyle(chartColors = chartColor)) {
            Chart(
                chart = lineChart(
                    listOf(
                        lineSpec(
                            lineColor = Color.Green,
                            pointConnector = DefaultPointConnector(cubicStrength = 0f)
                        )
                    )
                ),
                model = chartEntryModel,
                startAxis = startAxis(
                    guideline = null,
                    titleComponent = textComponent(
                        color = Color.Black
                    ),
                    title = "Location Usage",
                    label = null,
                    maxLabelCount = 5
                ),
                bottomAxis = bottomAxis(
                    guideline = null,
                    titleComponent = textComponent(
                        color = Color.Black
                    ),
                    title = "Last 24 Hours",
                    valueFormatter = bottomAxisValueFormatter
                )
            )
        }
    }
}