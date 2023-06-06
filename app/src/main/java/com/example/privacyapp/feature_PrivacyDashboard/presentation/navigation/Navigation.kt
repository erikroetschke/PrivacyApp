package com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.appDetails.AppDetailsScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.DashboardScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites.FavoritesScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.map.MapScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val item = listOf<NavigationItem>(NavigationItem.Favorites, NavigationItem.Dashboard, NavigationItem.Map, NavigationItem.AppDetails)

    Scaffold(bottomBar = {BottomN}) {
        
    }
    
}

@Composable
fun NavigationController(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationItem.Dashboard.route){

        composable(NavigationItem.Dashboard.route){
            DashboardScreen()
        }

        composable(NavigationItem.AppDetails.route){
            AppDetailsScreen()
        }

        composable(NavigationItem.Favorites.route) {
            FavoritesScreen()
        }

        composable(NavigationItem.Map.route){
                MapScreen()
        }
    }
}