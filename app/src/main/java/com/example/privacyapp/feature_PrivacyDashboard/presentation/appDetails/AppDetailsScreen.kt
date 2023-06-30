package com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.lineDiagram
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entriesOf

@Composable
fun AppDetailsScreen(
    navController: NavController,
    viewModel: AppDetailsViewModel
) {

    val state = viewModel.stateApp.value
    if(state.appName == "Instagram"){
        Log.v("test", "test")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = state.appName, color = Color.Black, style = MaterialTheme.typography.headlineMedium)
            if (state.favorite){
                IconButton(onClick = {
                    viewModel.onEvent(AppDetailsEvent.Favor)
                }) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favor", tint = Color.Green)
                }
            } else {
                IconButton(onClick = {
                    viewModel.onEvent(AppDetailsEvent.Favor)
                }) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favor")
                }
            }
        }
        Box(modifier = Modifier
            .padding(10.dp, 15.dp, 10.dp, 15.dp)
            .fillMaxWidth()
            .height(300.dp)
        ) {
            lineDiagram(yPoints = entriesOf(*viewModel.appUsagePerHour.toTypedArray()), modifier = Modifier.fillMaxWidth().padding(5.dp))
        }
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Location Permission:")
                Text(text = state.ACCESS_COARSE_LOCATION.toString())
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Accurate Location Permission:")
                Text(text = state.ACCESS_FINE_LOCATION.toString())
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Background Location Permission:")
                Text(text = state.ACCESS_BACKGROUND_LOCATION.toString())
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Estimated Location Requests in the last 24 hours:")
                Text(text = state.numberOfEstimatedRequests.toString())
            }
        }
    }
}