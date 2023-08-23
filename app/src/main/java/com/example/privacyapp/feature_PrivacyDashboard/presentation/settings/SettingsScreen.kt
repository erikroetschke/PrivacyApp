package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.IntegerSetting
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.Section
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.SliderSetting
import com.example.privacyapp.feature_PrivacyDashboard.presentation.settings.composables.SwitchSetting

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel
) {

    Column(
        modifier = Modifier
    ) {

        Box(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
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
                    onCheckedChange = { viewModel.onEvent(SettingsScreenEvent.ToggleCoarseLocationIsRelevant) }
                )

                SliderSetting(
                    label = "Tracking Interval in s",
                    value = viewModel.locationTrackingInterval.value,
                    onValueChange = { value -> viewModel.onEvent(SettingsScreenEvent.ChangeLocationTrackingInterval(value)) },
                    valueRange = 15f..300f,
                    steps = 18
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
                )

                SwitchSetting(
                    label = "Dynamic Limit",
                    isChecked = viewModel.dynamicLimit.value,
                    onCheckedChange = { viewModel.onEvent(SettingsScreenEvent.ToggleDynamicLimit) }
                )

                IntegerSetting(label = "POI radius in m",
                    value = viewModel.pOIRadius.value,
                    onValueChange = { intVal ->
                        viewModel.onEvent(SettingsScreenEvent.ChangePOIRadius(intVal))
                    }
                )

                IntegerSetting(label = "POI time threshold in min",
                    value = viewModel.minPOITime.value,
                    onValueChange = { intVal ->
                        viewModel.onEvent(SettingsScreenEvent.ChangeMinPOITime(intVal))
                    }
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
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                Button(
                    onClick = { viewModel.onEvent(SettingsScreenEvent.SaveSettings) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.changed.value
                ) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    onClick = { viewModel.onEvent(SettingsScreenEvent.RestoreSettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Reset to default")
                }
            }
        }
    }
}