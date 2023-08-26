package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.InfoDialog
import kotlin.math.roundToInt

/**
 * Composable function that creates a slider setting with a label, slider, and optional info dialog.
 *
 * @param label The label for the slider setting.
 * @param value The current value of the slider.
 * @param onValueChange The callback function to be called when the value of the slider changes.
 * @param valueRange The range of values that the slider can take.
 * @param steps The number of steps for the slider.
 * @param infoText The text to be displayed in the info dialog.
 * @param onInfoClick The callback function to be called when the info icon is clicked.
 * @param infoDialogVisible A boolean indicating whether the info dialog is visible or not.
 */
@Composable
fun SliderSetting(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    infoText:String, onInfoClick: () -> Unit, infoDialogVisible: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = label)
            Text(text = value.roundToInt().toString())
            IconButton(
                onClick = {
                    onInfoClick()
                }
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
        )

        if(infoDialogVisible){
            InfoDialog(infoText = infoText, onDismiss = {onInfoClick()})
        }
    }
}