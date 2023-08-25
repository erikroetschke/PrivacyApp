package com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


/**
 * A custom info dialog that displays a description and an "OK" button.
 *
 * @param infoText The text to be displayed as the description in the dialog.
 * @param onDismiss Callback to be invoked when the "OK" button is clicked, dismissing the dialog.
 */
@Composable
fun InfoDialog(
    infoText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Info") },
        text = { Text(text = infoText) },
        confirmButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(text = "OK")
            }
        }
    )
}