package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard


import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.LocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.PermissionDialog


@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel,
    mainActivity: MainActivity
) {


    val dialogQueue = viewModel.visiblePermissionDialogQueue
    //Request Permissions
    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        Button(onClick = {
            if (ContextCompat.checkSelfPermission(
                    mainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    mainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.onEvent(DashboardEvent.ToggleTracking(true))
                Intent(ApplicationProvider.application, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    ApplicationProvider.application.startService(this)
                }
            } else {
                //TODO handle ACTION_USAGE_ACCESS_SETTINGS better
                mainActivity.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                multiplePermissionResultLauncher.launch(permissionsToRequest)
            }

        }) {
            Text(text = "Start")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = {
            viewModel.onEvent(DashboardEvent.ToggleTracking(false))
            Intent(ApplicationProvider.application, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                ApplicationProvider.application.startService(this)
            }
        }) {
            Text(text = "Stop")
        }
        if (viewModel.trackingActive.value) {
            Text(
                text = "Tracking is currently on!",
                color = Color.Green,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "tracking is currently off!",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    dialogQueue
        .reversed()
        .forEach { permission ->
            fun openAppSettings() {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", ApplicationProvider.application.packageName, null)
                )
                mainActivity.startActivity(intent)
            }
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        LocationPermissionTextProvider()
                    }

                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        LocationPermissionTextProvider()
                    }

                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    mainActivity,
                    permission
                ),
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    viewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = ::openAppSettings
            )
        }

}


