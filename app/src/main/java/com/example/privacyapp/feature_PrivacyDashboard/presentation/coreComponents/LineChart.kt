package com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * A custom Composable function that draws a line chart to visualize data points.
 *
 * @param data A list of pairs representing data points (x, y).
 * @param modifier The modifier to apply to the layout.
 * @param maxValue The maximum value to be displayed on the y-axis.
 */
@Composable
fun LineChart(
    data: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    maxValue: Int
) {
    val spacing = 100f
    val graphColor = MaterialTheme.colorScheme.primary
    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
    val upperValueWithoutSpacing = maxValue.coerceAtLeast(1)
    val upperValueWithoutSpacingThisDataSet = (data.maxOfOrNull { it.second }) ?: 1
    val finalUpperValueWithoutSpacing = upperValueWithoutSpacing.coerceAtLeast(upperValueWithoutSpacingThisDataSet)
    val upperValue = finalUpperValueWithoutSpacing + finalUpperValueWithoutSpacing/5.toFloat()
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
        val spacePerHour = (size.width - spacing) / data.size
        (data.indices step 2).forEach { i ->
            val hour = data[i].first
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height,
                    textPaint
                )
            }
        }

        //lable
        drawContext.canvas.nativeCanvas.apply{
            drawText(
                "Time:",
                0f,
                size.height,
                textPaint
            )
        }

        //x-axis
        drawLine(
            color = textColor,
            start = Offset(0f, size.height - spacing),
            end = Offset(size.width -10 , size.height - spacing),
            strokeWidth = 5f
        )
        (data.indices step 2).forEach {i ->
            drawLine(
                color = textColor,
                start = Offset(spacing + i * spacePerHour, size.height - spacing +30),
                end = Offset(spacing + i * spacePerHour , size.height - spacing - 10),
                strokeWidth = 5f
            )
        }

        //arrow at the end of the x-Axis
        val trianglePath = Path().apply {
            moveTo(size.width, size.height - spacing)
            lineTo(size.width - 30, size.height - spacing - 20)
            lineTo(size.width - 30, size.height - spacing + 20)
            close()
        }
        drawPath(
            path = trianglePath,
            color = textColor
        )
        //arrow at the beginning of the x-Axis
        val trianglePath2 = Path().apply {
            moveTo(30F, size.height - spacing)
            lineTo(0F, size.height - spacing - 20)
            lineTo(0F, size.height - spacing + 20)
            close()
        }
        drawPath(
            path = trianglePath2,
            color = textColor
        )

        //y axis labels
/*        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }*/

        //line
        val strokePath = Path().apply {
            val height = size.height
            data.indices.forEach { i ->
                val info = data[i]
                val ratio = (info.second - lowerValue) / (upperValue - lowerValue)
                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (ratio * height)

                if (i == 0) { moveTo(x1, y1) }
                lineTo(x1, y1)
            }
        }

        //draw line
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        //fill below line
        val fillPath = android.graphics.Path(strokePath.asAndroidPath()).asComposePath().apply {
            lineTo(size.width - spacePerHour, size.height - spacing)
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