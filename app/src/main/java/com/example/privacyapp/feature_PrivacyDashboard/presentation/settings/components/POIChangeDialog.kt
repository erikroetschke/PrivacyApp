package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.components

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
fun POIChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Action Required") },
        text = { Text(text = "You changed some POI settings. Do you want to recompute your POIs with the new Parameters? \n" +
                "If you choose yes, be aware that this might take some time, even minutes!") },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(text = "No")
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm() }
            ) {
                Text(text = "OK")
            }
        }
    )
}