package com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.BackgroundLocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.LocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.NotificationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.PermissionDialog
import com.example.privacyapp.ui.theme.PrivacyAppTheme

/**
 * Welcome screen activity that guides users through permissions setup.
 */
class WelcomeScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PrivacyAppTheme {
                val viewModel = hiltViewModel<WelcomeScreenViewModel>()

                val dialogQueue = viewModel.visiblePermissionDialogQueue

                /**
                 * The list of permissions to request from the user.
                 */
                /**
                 * The list of permissions to request from the user.
                 */
                val permissionsToRequest = if (Build.VERSION.SDK_INT >= 33) {
                    arrayOf(
                        //Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else if (Build.VERSION.SDK_INT >= 29) {
                    arrayOf(
                        //Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION

                    )
                } else {
                    arrayOf(
                        //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }

                /**
                 * A launcher for requesting multiple permissions.
                 */
                /**
                 * A launcher for requesting multiple permissions.
                 */
                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { _ ->
                        permissionsToRequest.forEach { permission ->
                            viewModel.onPermissionResult(
                                permission = permission,
                                welcomeActivity = this@WelcomeScreenActivity
                            )
                        }
                    }
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    // Display headline
                    Text(
                        text = viewModel.headline.value,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    ) {
                        // Display main text
                        Text(
                            text = viewModel.text.value,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(25.dp, 0.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        // Action buttons
                        Row(
                            modifier = Modifier
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (viewModel.onFirstPage.value) {
                                        this@WelcomeScreenActivity.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                    } else {
                                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                                    }
                                },
                            ) {
                                Text(text = viewModel.actionButton.value)
                            }
                            Spacer(modifier = Modifier.width(30.dp))
                            Button(
                                onClick = {
                                    if (viewModel.onFirstPage.value) {
                                        if (!viewModel.onNextButtonClick(this@WelcomeScreenActivity)) {
                                            Toast.makeText(
                                                ApplicationProvider.application,
                                                "Please grant the said permission before moving on.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        if (!viewModel.onNextButtonClick(this@WelcomeScreenActivity)) {
                                            Toast.makeText(
                                                ApplicationProvider.application,
                                                "Please grant the said permissions before moving on.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            finish()
                                        }
                                    }
                                },
                            ) {
                                Text(text = viewModel.nextButton.value)
                            }
                        }
                    }
                }

                dialogQueue
                    .reversed()
                    .forEach { permission ->

                        PermissionDialog(
                            permissionTextProvider = when (permission) {

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
                                this@WelcomeScreenActivity,
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                multiplePermissionResultLauncher.launch(
                                    arrayOf(permission)
                                )
                                viewModel.dismissDialog()
                            },
                            onGoToAppSettingsClick = {
                                openAppSettings()
                                viewModel.dismissDialog()
                            }
                        )
                    }
            }
        }
    }

    /**
     * Open the app's settings.
     */
    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", ApplicationProvider.application.packageName, null)
        )
        this@WelcomeScreenActivity.startActivity(intent)
    }
}