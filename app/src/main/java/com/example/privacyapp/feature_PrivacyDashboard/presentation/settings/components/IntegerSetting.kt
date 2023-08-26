package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.InfoDialog

/**
 * Composable function that displays an integer setting with a label, input field, and info button.
 *
 * @param label The label text describing the setting.
 * @param value The current value of the setting.
 * @param onValueChange The callback function to be called when the value changes.
 * @param infoText The information text to be shown in the info dialog.
 * @param onInfoClick The callback function to be called when the info button is clicked.
 * @param infoDialogVisible A boolean indicating whether the info dialog is currently visible.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun IntegerSetting(label: String, value: Int, onValueChange: (Int) -> Unit, infoText:String, onInfoClick: () -> Unit, infoDialogVisible: Boolean) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val pattern = remember { Regex("^\\d+\$") }
        val keyboardController = LocalSoftwareKeyboardController.current

        Text(text = label, modifier = Modifier.weight(2f))
        TextField(
            value = value.toString(),
            onValueChange = { newText ->
                if(newText.isEmpty()) {
                    onValueChange(1)
                }
                var intValue = newText.toIntOrNull() ?: value
                if (intValue == 0){
                    intValue = 1
                }
                onValueChange(intValue)
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = {keyboardController?.hide()}),
            colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.secondary, cursorColor = MaterialTheme.colorScheme.primary, textColor = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.weight(0.5f),
            singleLine = true
        )

        IconButton(
            onClick = {
                onInfoClick()
            }
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
        }

        if(infoDialogVisible){
            InfoDialog(infoText = infoText, onDismiss = {onInfoClick()})
        }
    }
}