package com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AllAppsScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails.AppDetailsScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails.AppDetailsViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.DashboardScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.DashboardViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites.FavoritesScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites.FavoritesViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.map.MapScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.map.MapViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.WelcomeScreenViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.welcomeScreen

//import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Function with composable for the bottom navigation bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val items = listOf(
        NavigationItem.Dashboard,
        NavigationItem.AllApps,
        NavigationItem.Favorites,
        NavigationItem.Map
    )

    NavigationBar(modifier = Modifier, containerColor = MaterialTheme.colorScheme.secondary, tonalElevation = 5.dp) {
        items.forEach { item ->
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val selected = item.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.graph?.startDestinationRoute?.let {
                            navController.popBackStack(it, true)
                        }

                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary
                ),
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                        if (selected) {
                            Text(
                                text = item.label,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }
}


/**
 * Function with the Route/Navigation logic
 */
@Composable
fun NavigationController(navController: NavHostController, mainActivity: MainActivity) {


        NavHost(navController = navController, startDestination = NavigationItem.Dashboard.route) {

            composable(NavigationItem.Dashboard.route) {
                val dashboardViewModel = hiltViewModel<DashboardViewModel>()
                DashboardScreen(navController, dashboardViewModel, mainActivity)
            }

            composable(NavigationItem.AllApps.route) {
                val appsViewModel = hiltViewModel<AppsViewModel>()
                AllAppsScreen(navController, appsViewModel)
            }

            composable(NavigationItem.Favorites.route) {
                val favoritesViewModel = hiltViewModel<FavoritesViewModel>()
                FavoritesScreen(navController, favoritesViewModel)
            }

            composable(NavigationItem.Map.route) {
                val mapViewModel = hiltViewModel<MapViewModel>()
                MapScreen(mapViewModel)
            }

            composable(NavigationItem.AppDetails.route +
                    "/{packageName}",
                arguments = listOf(
                    navArgument(name = "packageName") {
                        type= NavType.StringType
                    }
                )
            ){
                val appDetailsViewModel = hiltViewModel<AppDetailsViewModel>()
                AppDetailsScreen(navController = navController, viewModel = appDetailsViewModel)
            }
        }

}