package com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.coreComponents.AppItem
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem

/**
 * A Composable function that displays the list of favorite apps.
 *
 * @param navController The navigation controller for navigating between screens.
 * @param viewModel The ViewModel associated with the favorites screen.
 */
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

        Box(modifier = Modifier.padding(10.dp, 8.dp, 0.dp, 0.dp)) {
            Text(
                text = "Favorite Apps",
                style = MaterialTheme.typography.headlineMedium
            )
        }


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.apps) { app ->
                AppItem(
                    app = app,
                    cumulativeUsage = viewModel.cumulativeUsage,
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