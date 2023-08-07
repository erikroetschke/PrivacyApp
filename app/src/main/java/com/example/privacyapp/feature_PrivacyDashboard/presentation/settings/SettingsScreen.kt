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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel
) {

    Column(
        modifier = Modifier
    ) {

        Box(modifier = Modifier.padding(10.dp, 8.dp, 0.dp, 0.dp)) {
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


            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "POI detection:", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Max POI per day: " + viewModel.maxPOIPerDay.value.toInt())
                        Slider(
                            value = viewModel.maxPOIPerDay.value,
                            onValueChange = { sliderValue_ ->
                                viewModel.onEvent(
                                    SettingsScreenEvent.ChangeMaxPOIPerDay(
                                        sliderValue_
                                    )
                                )
                            },
                            valueRange = 1f..20f,
                            steps = 18,
                            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "POI radius in m: " + viewModel.pOIRadius.value.toInt())
                        Slider(
                            value = viewModel.pOIRadius.value,
                            onValueChange = { sliderValue_ ->
                                viewModel.onEvent(SettingsScreenEvent.ChangePOIRadius(sliderValue_))
                            },
                            valueRange = 20f..500f,
                            steps = 47,
                            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "POI time threshold in min: " + viewModel.minPOITime.value.toInt())
                        Slider(
                            value = viewModel.minPOITime.value,
                            onValueChange = { sliderValue_ ->
                                viewModel.onEvent(SettingsScreenEvent.ChangeMinPOITime(sliderValue_))
                            },
                            valueRange = 1f..20f,
                            steps = 18,
                            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "POI frequency:", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Max POI occurrence per day: " + viewModel.maxOccurrencePerDay.value.toInt())
                        Slider(
                            value = viewModel.maxOccurrencePerDay.value,
                            onValueChange = { sliderValue_ ->
                                viewModel.onEvent(
                                    SettingsScreenEvent.ChangeMaxOccurrencePerDay(
                                        sliderValue_
                                    )
                                )
                            },
                            valueRange = 2f..5f,
                            steps = 2,
                            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Max POI occurrence per Week: " + viewModel.maxOccurrencePerWeek.value.toInt())
                        Slider(
                            value = viewModel.maxOccurrencePerWeek.value,
                            onValueChange = { sliderValue_ ->
                                viewModel.onEvent(
                                    SettingsScreenEvent.ChangeMaxOccurrencePerWeek(
                                        sliderValue_
                                    )
                                )
                            },
                            valueRange = 2f..10f,
                            steps = 7,
                            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Max POI occurrence per Month: " + viewModel.maxOccurrencePerMonth.value.toInt())
                        Slider(
                            value = viewModel.maxOccurrencePerMonth.value,
                            onValueChange = { sliderValue_ ->
                                viewModel.onEvent(
                                    SettingsScreenEvent.ChangeMaxOccurrencePerMonth(
                                        sliderValue_
                                    )
                                )
                            },
                            valueRange = 2f..20f,
                            steps = 17,
                            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.tertiary)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                Button(onClick = { viewModel.onEvent(SettingsScreenEvent.SaveSettings) }, modifier = Modifier.fillMaxWidth(), enabled = viewModel.changed.value) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Button(onClick = { viewModel.onEvent(SettingsScreenEvent.RestoreSettings) }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Reset to default")
                }
            }
        }
    }
}