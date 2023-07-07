package com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.BackgroundLocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.LocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.NotificationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.PermissionDialog


@Composable
fun welcomeScreen(viewModel: WelcomeScreenViewModel, mainActivity: MainActivity) {

    val dialogQueue = viewModel.visiblePermissionDialogQueue

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            //Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        arrayOf(
            //Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        arrayOf(
            //Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = viewModel.headline.value,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Text(
            text = viewModel.text.value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(25.dp, 0.dp)
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (viewModel.onFirstPage.value) {
                        mainActivity.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    } else {
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                    }
                },
            ) {
                Text(text = viewModel.actionButton.value)
            }
            Button(
                onClick = {
                    if (viewModel.onFirstPage.value) {
                        if (!viewModel.onNextButtonClick(mainActivity)) {
                            Toast.makeText(
                                ApplicationProvider.application,
                                "Please grant the said permission before moving on.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }else {
                        if (!viewModel.onNextButtonClick(mainActivity)) {
                            Toast.makeText(
                                ApplicationProvider.application,
                                "Please grant the said permissions before moving on.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                ApplicationProvider.application,
                                "YEAH",
                                Toast.LENGTH_SHORT
                            ).show()
                            //TODO popbackstack

                        }
                    }
                },
            ) {
                Text(text = viewModel.nextButton.value)
            }
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

                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                        BackgroundLocationPermissionTextProvider()
                    }

                    Manifest.permission.POST_NOTIFICATIONS -> {
                        NotificationPermissionTextProvider()
                    }

                    else -> return@forEach
                },
                isPermanentlyDeclined = ActivityCompat.shouldShowRequestPermissionRationale(
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
