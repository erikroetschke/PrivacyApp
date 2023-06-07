package com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AllAppsScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails.AppDetailsScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.DashboardScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites.FavoritesScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.map.MapScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem

/**
 * Function with composable for the bottom navigation bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar() {
    val navController = rememberNavController()
    val items = listOf(
        NavigationItem.Dashboard,
        NavigationItem.AllApps,
        NavigationItem.Favorites,
        NavigationItem.Map
    )

    Scaffold(bottomBar = {
        NavigationBar(modifier = Modifier, containerColor = Color.DarkGray, tonalElevation = 5.dp) {
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
                        selectedIconColor = Color.Green,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Gray
                    ),
                    icon = {
                        Column(horizontalAlignment = CenterHorizontally) {
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
    }) {
        it
        NavigationController(navController = navController)
    }

}


/**
 * Function with the Route/Navigation logic
 */
@Composable
fun NavigationController(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationItem.Dashboard.route) {

        composable(NavigationItem.Dashboard.route) {
            DashboardScreen()
        }

        composable(NavigationItem.AllApps.route) {
            AllAppsScreen()
        }

        composable(NavigationItem.Favorites.route) {
            FavoritesScreen()
        }

        composable(NavigationItem.Map.route) {
            MapScreen()
        }
    }
}