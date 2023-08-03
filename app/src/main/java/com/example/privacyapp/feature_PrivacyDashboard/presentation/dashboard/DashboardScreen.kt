package com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard


import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.AppItem
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.LineChartV2
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.BackgroundLocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.LocationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.MetricSection
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.NotificationPermissionTextProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.components.PermissionDialog
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem


@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel,
    mainActivity: MainActivity
) {


    val dialogQueue = viewModel.visiblePermissionDialogQueue
    //Request Permissions
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else if (Build.VERSION.SDK_INT >= 30) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            permissionsToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    mainActivity = mainActivity
                )
            }
        }
    )

    val scrollState = rememberScrollState()

    Column() {
        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Row() {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(DashboardEvent.RefreshData)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.onEvent(DashboardEvent.ToggleMetricDropDown)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Metric Settings"
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = viewModel.metricSectionExpanded.value,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                MetricSection(
                    metrics = viewModel.selectedMetrics.toList(),
                    metricInterval = viewModel.metricInterval.value,
                    onMetricChange = { metric ->
                        viewModel.onEvent(
                            DashboardEvent.ChangeMetric(metric)
                        )
                    },
                    metricType = viewModel.metricType.value,
                    onMetricTypeChange = {metricType -> viewModel.onEvent(DashboardEvent.ChangeMetricType(metricType))},
                    modifier = Modifier.padding(10.dp),
                    onMetricIntervalChange = {metricInterval -> viewModel.onEvent(DashboardEvent.ChangeMetricInterval(metricInterval))}
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp, 20.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = "Tracking:", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.width(20.dp))
                    Switch(
                        checked = viewModel.trackingActive.value,
                        onCheckedChange = { switchOn ->
                            viewModel.onEvent(DashboardEvent.ToggleTracking(switchOn))
                            if (switchOn) {
                                if (ContextCompat.checkSelfPermission(
                                        mainActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED &&
                                    (ContextCompat.checkSelfPermission(
                                        mainActivity,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) || Build.VERSION.SDK_INT < 29) {
                                    Intent(
                                        ApplicationProvider.application,
                                        LocationService::class.java
                                    ).apply {
                                        action = LocationService.ACTION_START
                                        ApplicationProvider.application.startService(this)
                                    }
                                } else {
                                    viewModel.onEvent(DashboardEvent.ToggleTracking(false))
                                    multiplePermissionResultLauncher.launch(permissionsToRequest)
                                }
                            } else {

                                Intent(
                                    ApplicationProvider.application,
                                    LocationService::class.java
                                ).apply {
                                    action = LocationService.ACTION_STOP
                                    ApplicationProvider.application.startService(this)
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = Color.Gray
                        )
                    )
                }

                if (viewModel.trackingActive.value) {
                    Text(
                        text = "Tracking is currently on!",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Tracking is currently off!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp, 20.dp, 0.dp)
            ) {
                Text(text = "Privacy Leak:", style = MaterialTheme.typography.headlineSmall)
            }
            Box(
                modifier = Modifier
                    .padding(10.dp, 5.dp, 10.dp, 15.dp)
                    .fillMaxWidth()
                    .height(300.dp)
                //.background(Color.Black)
            ) {
                if (viewModel.isLoading.value){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }else{
                    LineChartV2(
                        data = viewModel.privacyLeakData.value,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp, 20.dp, 0.dp)
            ) {
                Text(text = "Top 5 last 24 hours:", style = MaterialTheme.typography.headlineSmall)
            }
            Column(modifier = Modifier.fillMaxSize()) {
                (viewModel.top5Apps).forEach { app ->
                    AppItem(
                        app = app,
                        maxLocationUsage = viewModel.maxLocationUsage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 10.dp, 10.dp, 0.dp)
                            .clickable {
                                navController.navigate(NavigationItem.AppDetails.route + "/${app.packageName}")
                            }
                    )
                }
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
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    mainActivity,
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


