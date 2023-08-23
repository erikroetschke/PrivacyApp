package com.example.privacyapp.feature_PrivacyDashboard.presentation.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsEvent
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.components.OrderSection
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.DefaultRadioButton
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapScreen(viewModel: MapViewModel) {

    val builder = LatLngBounds.builder()

    for (poi in viewModel.pois) {
        builder.include(LatLng(poi.latitude, poi.longitude))
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.padding(10.dp, 10.dp, 0.dp, 0.dp)) {
                Text(
                    text = "Found POIs:",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp, 0.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Interval: ")
                DefaultRadioButton(
                    text = "24h",
                    selected = viewModel.metricInterval.value == MetricInterval.DAY,
                    onSelect = {
                        viewModel.onMetricIntervalChange(MetricInterval.DAY)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                DefaultRadioButton(
                    text = "7d",
                    selected = viewModel.metricInterval.value == MetricInterval.WEEK,
                    onSelect = {
                        viewModel.onMetricIntervalChange(MetricInterval.WEEK)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                DefaultRadioButton(
                    text = "1mon",
                    selected = viewModel.metricInterval.value == MetricInterval.MONTH,
                    onSelect = {
                        viewModel.onMetricIntervalChange(MetricInterval.MONTH)
                    }
                )
            }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (viewModel.pois.isNotEmpty()) {
                    val bounds = builder.build()

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(bounds.center, 10f)
                    }
                ) {
                    viewModel.pois.forEach {
                        Marker(position = LatLng(it.latitude, it.longitude))
                    }
                }
                }else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = rememberCameraPositionState()
                    ) {
                    }
                }
            }
        }
    }
}