package com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.LineChart


@Composable
fun AppDetailsScreen(
    navController: NavController,
    viewModel: AppDetailsViewModel
) {

    val mContext = LocalContext.current

    val state = viewModel.stateApp.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = state.appName, style = MaterialTheme.typography.headlineMedium)
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
        Text(text = "Possible location tracking last 24h:", style = MaterialTheme.typography.headlineSmall)
        Box(modifier = Modifier
            .padding(10.dp, 15.dp, 10.dp, 15.dp)
            .fillMaxWidth()
            .height(300.dp)
            //.background(Color.Black)
        ) {
            LineChart(
                data = viewModel.stateDiagramData.value,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .align(Center)
            )
            //lineDiagram(yPoints = entriesOf(*viewModel.appUsagePerHour.toTypedArray()), modifier = Modifier.fillMaxWidth().padding(5.dp))
        }
        Text(text = "Permissions:", style = MaterialTheme.typography.headlineSmall)
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
        }
        Button(onClick = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", viewModel.stateApp.value.packageName, null)
            )
            try {
            mContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(mContext, "Operation is not possible with this app.",Toast.LENGTH_SHORT).show()
        } }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Change permissions")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Other:", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = { /*TODO*/}, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Don´t consider app in assessment")
        }
    }
}

