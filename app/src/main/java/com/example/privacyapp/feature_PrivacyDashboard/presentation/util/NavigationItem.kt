package com.example.privacyapp.feature_PrivacyDashboard.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector


sealed class NavigationItem(val route:String, val label:String, val icon: ImageVector){

    //object Dashboard : NavigationItem(Resources.getSystem().getString(R.string.dashboard), Resources.getSystem().getString(R.string.dashboard), Icons.Default.Home)
    object Dashboard : NavigationItem("Dashboard", "Dashboard", Icons.Default.Home)
    //object AllApps : NavigationItem(Resources.getSystem().getString(R.string.app_deatails), Resources.getSystem().getString(R.string.app_deatails), Icons.Default.Info)
    object AllApps : NavigationItem("All Apps", "All Apps", Icons.Default.List)
    //object Favorites : NavigationItem(Resources.getSystem().getString(R.string.favorites), Resources.getSystem().getString(R.string.favorites), Icons.Default.Favorite)
    object Favorites : NavigationItem("Favorites", "Favorites", Icons.Default.Favorite)
    //object Map : NavigationItem(Resources.getSystem().getString(R.string.map), Resources.getSystem().getString(R.string.map), Icons.Default.LocationOn)
    object Map : NavigationItem("Map", "Map", Icons.Default.LocationOn)
    object AppDetails : NavigationItem("AppDetails", "AppDetails", Icons.Default.LocationOn)
}
