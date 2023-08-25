package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.InfoDialog
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.IntegerSetting
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.LoadingDialog
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.POIChangeDialog
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.Section
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.SliderSetting
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.SwitchSetting

/**
 * Composable function that displays the settings screen, allowing the user to configure various settings.
 *
 * @param viewModel The ViewModel instance that holds the settings state and handles user interactions.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel
) {

    Column(
        modifier = Modifier
    ) {

        Box(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(
                    onClick = { viewModel.onEvent(SettingsScreenEvent.SaveSettings) },
                    enabled = viewModel.changed.value
                ) {
                    Text(text = "Save")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Section(title = "General Settings") {
                SwitchSetting(
                    label = "Coarse location relevant",
                    isChecked = viewModel.isCoarseLocationRelevant.value,
                    onCheckedChange = { viewModel.onEvent(SettingsScreenEvent.ToggleCoarseLocationIsRelevant) },
                    infoDialogVisible = viewModel.infoDialogState.value.coarseLocationRelevantInfoDialogVisible,
                    infoText = "When active, app usages from apps which only have coarse location granted (aprox. 2km accuracy), will also be taken into account when computing the privacy score." +
                            "\n Please be aware, when changing this setting it will only effect future data, not the existing one",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.IS_COARSE_LOCATION_RELEVANT))}
                )

                SliderSetting(
                    label = "Tracking Interval in s",
                    value = viewModel.locationTrackingInterval.value,
                    onValueChange = { value -> viewModel.onEvent(SettingsScreenEvent.ChangeLocationTrackingInterval(value)) },
                    valueRange = 15f..300f,
                    steps = 18,
                    infoDialogVisible = viewModel.infoDialogState.value.locationTrackingIntervalInfoDialogVisible,
                    infoText = "This defines how often this app will get your location. With every location the AppUsages will be assessed. " +
                            "So moving it up will decrease the accuracy, but moving it down will increase battery and memory consumption.",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.LOCATION_TRACKING_INTERVAL))}
                )
            }

            Section(title = "POI Detection") {
                SliderSetting(
                    label = "POI Limit",
                    value = viewModel.pOILimit.value,
                    onValueChange = { sliderValue_ ->
                        viewModel.onEvent(
                            SettingsScreenEvent.ChangeMaxPOIPerDay(
                                sliderValue_
                            )
                        )
                    },
                    valueRange = 1f..20f,
                    steps = 18,
                    infoDialogVisible = viewModel.infoDialogState.value.pOILimitInfoDialogVisible,
                    infoText = "The value corresponds to the maximum POIs found. Here you should set " +
                            "the maximum value that is acceptable for you of POIs that are associated with you. " +
                            "Be sure to note the setting \"Dynamic Limit\", as it has influence on this setting.",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.POI_LIMIT))}
                )

                SwitchSetting(
                    label = "Dynamic Limit",
                    isChecked = viewModel.dynamicLimit.value,
                    onCheckedChange = { viewModel.onEvent(SettingsScreenEvent.ToggleDynamicLimit) },
                    infoDialogVisible = viewModel.infoDialogState.value.dynamicLimitInfoDialogVisible,
                    infoText = "If false the POI limit will be applicable for the whole period. So it is the same for the 24h, 7d and 1m Score. " +
                    "If true, the limit is multiplied by the number of days. So if you set it to 5, the POI limit for 24h will be 5, for 7d it will be 7 * 5 = 35 and for 1m it will be 30 * 5 = 150",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.DYNAMIC_LIMIT))}
                )

                IntegerSetting(label = "POI radius in m",
                    value = viewModel.pOIRadius.value,
                    onValueChange = { intVal ->
                        viewModel.onEvent(SettingsScreenEvent.ChangePOIRadius(intVal))
                    },
                    infoDialogVisible = viewModel.infoDialogState.value.pOIRadiusInfoDialogVisible,
                    infoText = "This defines the minimum radius of a stay to be a POI.",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.POI_RADIUS))}
                )

                IntegerSetting(label = "POI time threshold in min",
                    value = viewModel.minPOITime.value,
                    onValueChange = { intVal ->
                        viewModel.onEvent(SettingsScreenEvent.ChangeMinPOITime(intVal))
                    },
                    infoDialogVisible = viewModel.infoDialogState.value.minPOITimeInfoDialogVisible,
                    infoText = "This defines the minimum time of stay to be a POI.",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.MIN_POI_TIME))}
                )
            }

            Section(title = "POI Frequency") {
                SliderSetting(
                    label = "Max POI occurrence",
                    value = viewModel.maxPOIOccurrence.value,
                    onValueChange = { sliderValue_ ->
                        viewModel.onEvent(
                            SettingsScreenEvent.ChangeMaxPOIOccurrence(
                                sliderValue_
                            )
                        )
                    },
                    valueRange = 2f..10f,
                    steps = 7,
                    infoDialogVisible = viewModel.infoDialogState.value.maxPOIOccurrenceInfoDialogVisible,
                    infoText = "The more a often you visit a POI, the more important it is. This Setting defines a threshold of how often you need to visit the same POI before it will be taken into account into the \"Stop Frequency\" metric.",
                    onInfoClick = {viewModel.onEvent(SettingsScreenEvent.TriggerInfoDialog(PreferencesManager.MAX_POI_OCCURRENCE))}
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    onClick = { viewModel.onEvent(SettingsScreenEvent.RestoreSettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Reset to default")
                }
            }
        }

        //Dialogs when changing specific values
        if (viewModel.valuesSaved.value) {
            when {
                viewModel.trackingIntervalChanged.value -> {
                    ShowTrackingIntervalChangedDialog(viewModel)
                }
                viewModel.pOISettingsChanged.value -> {
                    ShowPOISettingsChangedDialog(viewModel)
                }
                else -> {
                    viewModel.onEvent(SettingsScreenEvent.ToggleValuesSaved)
                }
            }
        }
        //Loading dialog
        if(viewModel.loading.value) {
            LoadingDialog()
        }

    }
}

/**
 * Composable function that displays a dialog when the tracking interval is changed, providing information and instructions.
 *
 * @param viewModel The ViewModel instance managing the settings state and logic.
 */
@Composable
fun ShowTrackingIntervalChangedDialog(viewModel: SettingsScreenViewModel) {
    if (ApplicationProvider.application.isServiceRunning(LocationService::class.java)) {
        InfoDialog(
            infoText = "Successfully Saved! \n" +
                    "Please restart the tracking manually from the dashboard to apply the new location tracking interval!",
            onDismiss = {
                viewModel.onEvent(SettingsScreenEvent.ToggleValuesSaved)
                viewModel.onEvent(SettingsScreenEvent.ToggleTrackingIntervalChanged)
            }
        )
    } else {
        viewModel.onEvent(SettingsScreenEvent.ToggleValuesSaved)
        viewModel.onEvent(SettingsScreenEvent.ToggleTrackingIntervalChanged)
    }
}

/**
 * Composable function that displays a dialog when the POI settings are changed, allowing the user to confirm recomputing POIs.
 *
 * @param viewModel The ViewModel instance managing the settings state and logic.
 */
@Composable
fun ShowPOISettingsChangedDialog(viewModel: SettingsScreenViewModel) {
        POIChangeDialog(
            onDismiss = {
                viewModel.onEvent(SettingsScreenEvent.ToggleValuesSaved)
                viewModel.onEvent(SettingsScreenEvent.TogglePOISettingsChanged)
            },
            onConfirm = {
                viewModel.onEvent(SettingsScreenEvent.RecomputePOIsWithNewParameters)
                viewModel.onEvent(SettingsScreenEvent.ToggleValuesSaved)
                viewModel.onEvent(SettingsScreenEvent.TogglePOISettingsChanged)
            }
        )
}

/**
 * Checks if a specified service is currently running in the background.
 *
 * @param service The class of the service to check.
 * @return `true` if the service is running, `false` otherwise.
 */
@Suppress("DEPRECATION") // Deprecated for third party Services.
private fun Context.isServiceRunning(service: Class<LocationService>) =
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }