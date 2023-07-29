package com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.AppItem
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.components.OrderSection
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem


@Composable
fun AllAppsScreen(
    navController: NavController,
    viewModel: AppsViewModel
) {
    val state = viewModel.state.value

    //TODO if  lower than Android 10 (API level 29), background location access is always allowed, therefore the permission not needed
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)) {
                    Text(
                        text = "All Apps",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.onEvent(AppsEvent.ToggleOrderSection)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Sort"
                    )
                }
            }
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    noteOrder = state.appOrder,
                    onOrderChange = {
                        viewModel.onEvent(AppsEvent.Order(it))
                    }
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.apps) { app ->
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