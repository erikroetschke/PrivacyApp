package com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsEvent
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.components.AppItem
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel
) {

    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)) {
            Text(
                text = "Favorite Apps",
                style = MaterialTheme.typography.headlineMedium
            )
        }


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.apps) { app ->
                AppItem(
                    app = app,
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