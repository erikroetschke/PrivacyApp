package com.example.privacyapp.feature_PrivacyDashboard.presentation.util

import android.content.res.Resources
import android.provider.Settings.Global.getString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.privacyapp.R


sealed class NavigationItem(val route:String, val label:String, val icon:ImageVector){

    object Dashboard : NavigationItem(Resources.getSystem().getString(R.string.dashboard), Resources.getSystem().getString(R.string.dashboard), Icons.Default.Home)
    object AppDetails : NavigationItem(Resources.getSystem().getString(R.string.app_deatails), Resources.getSystem().getString(R.string.app_deatails), Icons.Default.Info)
    object Favorites : NavigationItem(Resources.getSystem().getString(R.string.favorites), Resources.getSystem().getString(R.string.favorites), Icons.Default.Favorite)
    object Map : NavigationItem(Resources.getSystem().getString(R.string.map), Resources.getSystem().getString(R.string.map), Icons.Default.LocationOn)

}
