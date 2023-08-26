package com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A custom RadioButton composable with an associated text label.
 *
 * @param text The text label to be displayed next to the RadioButton.
 * @param selected Whether the RadioButton should appear selected or not.
 * @param onSelect The callback function to be invoked when the RadioButton is clicked.
 * @param modifier The modifier to apply to the layout.
 */
@Composable
fun DefaultRadioButton(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Display the RadioButton with the associated text label in a Row layout
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the RadioButton
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Green,
                unselectedColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Display the text label
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}