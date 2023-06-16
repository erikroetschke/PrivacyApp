package com.example.privacyapp.feature_PrivacyDashboard.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AllAppsScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.allApps.AppsViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.DashboardScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.favorites.FavoritesScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.map.MapScreen
import com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation.BottomNavigationBar
import com.example.privacyapp.feature_PrivacyDashboard.presentation.util.NavigationItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationProvider.initialize(this.application)
        setContent {

            //viewmodels
            val appsViewModel: AppsViewModel = viewModel()

            BottomNavigationBar(appsViewModel)
        }
    }
}
