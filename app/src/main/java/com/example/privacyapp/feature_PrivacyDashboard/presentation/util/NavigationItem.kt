package com.example.privacyapp.feature_PrivacyDashboard.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


sealed class NavigationItem(val route: String, val label: String, val icon: ImageVector) {

    /**
     * Represents the dashboard navigation item.
     */
    object Dashboard : NavigationItem("Dashboard", "Dashboard", Icons.Default.Home)

    /**
     * Represents the all apps navigation item.
     */
    object AllApps : NavigationItem("All Apps", "All Apps", Icons.Default.List)

    /**
     * Represents the favorites navigation item.
     */
    object Favorites : NavigationItem("Favorites", "Favorites", Icons.Default.Favorite)

    /**
     * Represents the map navigation item.
     */
    object Map : NavigationItem("Map", "Map", Icons.Default.LocationOn)

    /**
     * Represents the app details navigation item.
     */
    object AppDetails : NavigationItem("AppDetails", "AppDetails", Icons.Default.LocationOn)

    /**
     * Represents the settings navigation item.
     */
    object Settings : NavigationItem("Settings", "Settings", Icons.Default.Settings)
}
