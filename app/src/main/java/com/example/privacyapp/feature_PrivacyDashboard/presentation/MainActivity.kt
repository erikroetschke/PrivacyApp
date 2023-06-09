package com.example.privacyapp.feature_PrivacyDashboard.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation.BottomNavigationBar
import com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation.NavigationController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.WelcomeScreenViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.welcomeScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {

    @Inject
    lateinit var appUseCases: AppUseCases

    @Inject
    lateinit var locationUseCases: LocationUseCases

    @Inject
    lateinit var appUsageUseCases: AppUsageUseCases

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //start python
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }

        //get Application to provide it in other classes
        ApplicationProvider.initialize(this.application)

        //init apps from the phone
        lifecycleScope.launch {


            //get the apps
            val appsFromPhone = appUseCases.initApps().toMutableList()   //is sorted ascending by appName
            val appsFromDb = appUseCases.getApps(AppOrder.Title(OrderType.Ascending))

            //check if db is up to date
            for ((index, app) in appsFromDb.withIndex()) {
                val indexFromPhone = appsFromPhone.indexOfFirst { it.packageName == app.packageName }
                if (indexFromPhone != -1) {
                    //app is already in the DB, now check if permissions have been changed and update when needed
                    if (appsFromPhone[indexFromPhone].ACCESS_COARSE_LOCATION != app.ACCESS_COARSE_LOCATION
                        || appsFromPhone[indexFromPhone].ACCESS_FINE_LOCATION != app.ACCESS_FINE_LOCATION
                        || appsFromPhone[indexFromPhone].ACCESS_BACKGROUND_LOCATION != app.ACCESS_BACKGROUND_LOCATION
                    ) {
                        //if at least on permission has changed update all of them
                        appUseCases.addApp(
                            App(
                                app.packageName,
                                app.appName,
                                appsFromPhone[indexFromPhone].ACCESS_COARSE_LOCATION,
                                appsFromPhone[indexFromPhone].ACCESS_FINE_LOCATION,
                                appsFromPhone[indexFromPhone].ACCESS_BACKGROUND_LOCATION,
                                app.numberOfEstimatedRequests,
                                app.favorite
                            )
                        )
                    }
                    appsFromPhone.removeAt(indexFromPhone)
                } else {
                    //App is not in the DB but not on the phone anymore, so delete it from the db
                    appUseCases.deleteApp(app)
                }
            }
            // add Apps that were not in the Db
            for (app in appsFromPhone) {
                appUseCases.addApp(app)
            }

            //create UsageStats and update location
            val locationsWithLocationUsedIsNull = locationUseCases.getLocationsWithLocationUsedIsNull()
            if (locationsWithLocationUsedIsNull.isNotEmpty()) {
                appUsageUseCases.computeUsage(locationsWithLocationUsedIsNull)
            }

            //update Apps with number of location Requests
            appUsageUseCases.updateAppUsageLast24Hours()
        }

        //actual content/ui
        setContent {

            val navController = rememberNavController()

            Scaffold(bottomBar = {
                BottomNavigationBar(navController)
            }) {
                Box(modifier = Modifier.padding(it)) {
                    NavigationController(navController = navController, this@MainActivity)
                }
            }
        }
    }
}
