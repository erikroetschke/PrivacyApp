package com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * A Jetpack Compose composable function that displays a line chart with given data points.
 * It meant to be used together with a [LineChartV2], as it has no x axis by itself.
 * It should be Used to draw a second Line above the existing chart.
 *
 * @param data List of data points represented as pairs of X (hour) and Y (value) coordinates.
 * @param modifier Modifier to apply to the composable.
 */
@Composable
fun LineChartV2YAxisRight(
    data: List<Pair<Int, Double>>,
    modifier: Modifier = Modifier
) {
    val spacing = 100f
    val graphColor = Color(5, 189, 245, 255)
    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }

    var upperValueWithoutSpacing = (data.maxOfOrNull { it.second })?.toInt() ?: 1
    if (upperValueWithoutSpacing < 1) {
        upperValueWithoutSpacing = 1
    }
    val upperValue = upperValueWithoutSpacing + upperValueWithoutSpacing/5.toFloat()
    val lowerValue = 0
    val density = LocalDensity.current
    val textColor = MaterialTheme.colorScheme.onBackground

    val textPaint = remember(density) {
        Paint().apply {
            color = textColor.hashCode()
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing*1.5f) / data.size

        //lable
        drawContext.canvas.nativeCanvas.apply{
            drawText(
                "Score:",
                size.width - 30f,
                0f,
                textPaint
            )
        }

        //y axis labels
        val priceStep = (upperValueWithoutSpacing - lowerValue) / 4f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                if (priceStep > 1){
                    drawText(
                        (lowerValue + priceStep * i).roundToInt().toString(),
                        size.width - 30f,
                        size.height - spacing - i * size.height / 5f,
                        textPaint
                    )
                }else {
                    drawText(
                        (((lowerValue + priceStep * i * 100).roundToInt())/100f).toString(),
                        size.width - 30f,
                        size.height - spacing - i * size.height / 5f - 10,
                        textPaint
                    )
                }
            }
        }

        val strokePath = Path().apply {
            val height = size.height
            data.indices.forEach { i ->
                val info = data[i]
                val ratio = (info.second - lowerValue) / (upperValue - lowerValue)
                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (ratio * height)

                if (i == 0) { moveTo(x1, y1.toFloat()) }
                lineTo(x1, y1.toFloat())
            }
        }

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        val fillPath = android.graphics.Path(strokePath.asAndroidPath()).asComposePath().apply {
            lineTo((data.size-1)* spacePerHour + spacing, size.height - spacing)
            lineTo(spacing, size.height - spacing)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )

    }
}