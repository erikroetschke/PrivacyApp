package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A Composable function that displays a permission dialog with options for granting or dismissing permissions.
 *
 * @param permissionTextProvider A provider for generating permission descriptions.
 * @param isPermanentlyDeclined A flag indicating if the permission has been permanently declined.
 * @param onDismiss Callback when the dialog is dismissed.
 * @param onOkClick Callback when the "OK" button is clicked.
 * @param onGoToAppSettingsClick Callback when the "Grant permission" button is clicked.
 * @param modifier The modifier to apply to the layout.
 */
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider()
                Text(
                    text = if(isPermanentlyDeclined) {
                        "Grant permission"
                    } else {
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        modifier = modifier
    )
}

/**
 * An interface for providing permission description texts.
 */
interface PermissionTextProvider {
    /**
     * Get the permission description based on whether it's permanently declined or not.
     *
     * @param isPermanentlyDeclined Flag indicating if the permission has been permanently declined.
     * @return The description text.
     */
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

/**
 * A specific implementation of [PermissionTextProvider] for location permission.
 */
class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "It seems you permanently declined location permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your location to work as intended."
        }
    }
}

/**
 * A specific implementation of [PermissionTextProvider] for background location permission.
 */
class BackgroundLocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "To be able to track your location in the background, you need to grant background location usage for this app in the settings."
        } else {
            "This app needs access to your location in the background to work as intended."
        }
    }
}

/**
 * A specific implementation of [PermissionTextProvider] for notification permission.
 */
class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "It seems you permanently declined Notification permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to be able to send you Notifications to work as intended."
        }
    }
}
