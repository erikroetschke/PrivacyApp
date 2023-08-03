package com.example.privacyapp.feature_PrivacyDashboard.presentation

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation.BottomNavigationBar
import com.example.privacyapp.feature_PrivacyDashboard.presentation.navigation.NavigationController
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.WelcomeScreenActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.WelcomeScreenViewModel
import com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome.welcomeScreen
import com.example.privacyapp.ui.theme.PrivacyAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val isLoading = mutableStateOf(true)

    @Inject
    lateinit var appUseCases: AppUseCases

    @Inject
    lateinit var locationUseCases: LocationUseCases

    @Inject
    lateinit var appUsageUseCases: AppUsageUseCases

    @Inject
    lateinit var privacyAssessmentUseCases: PrivacyAssessmentUseCases

    @Inject
    lateinit var poiRepository: POIRepository


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //start python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }

        //get Application to provide it in other classes
        ApplicationProvider.initialize(this.application)

        val sharedPreferences = getSharedPreferences("PREFS_NAME", 0)
        val firstRun = sharedPreferences.getBoolean("FIRST_RUN", true)
        val grantedUsagePermission = sharedPreferences.getBoolean("USAGE_PERMISSION_GRANTED", false)
        if (firstRun) {
            isLoading.value = false
            val intent = Intent(this, WelcomeScreenActivity::class.java)
            startActivity(intent)
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        } else {
            initData()
            cleanDb()
        }


        installSplashScreen().apply {
            setKeepOnScreenCondition {
                isLoading.value
            }
        }
        //actual content/ui
        setContent {
            PrivacyAppTheme {
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

    /**
     * function to remove data from the db which wont be needed anymore.
     * SO everything older than a month will be removed
     * Locations, AppUSages, PrivacyAssessments, POIs
     */
    private fun cleanDb() {
        //get timestamp one month ago
        val timestamp = ChronoUnit.MILLIS.between(
            Instant.EPOCH,
            Instant.now().atZone(ZoneId.systemDefault()).minusMonths(1)
        )
        lifecycleScope.launch {
            privacyAssessmentUseCases.deletePrivacyAssessment(timestamp)
            locationUseCases.deleteLocationsOlderThanTimestamp(timestamp)
            appUsageUseCases.deleteAppUsageOlderThanTimestamp(timestamp)
            poiRepository.deletePOIOlderThanTimestamp(timestamp)
        }
    }

    private fun initData() {
        //run blocking to make sure init completes before dashboard will be initialized
        runBlocking {
            //init apps from the phone
            //get the apps
            val appsFromPhone =
                appUseCases.initApps().toMutableList()   //is sorted ascending by appName
            val appsFromDb = appUseCases.getApps(AppOrder.Title(OrderType.Ascending))

            //check if app-db is up to date
            for ((index, app) in appsFromDb.withIndex()) {
                val indexFromPhone =
                    appsFromPhone.indexOfFirst { it.packageName == app.packageName }
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
                                app.favorite,
                                app.active
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
            //get locationData which isnt processed into UsageStats yet
            val locationsWithLocationUsedIsNull =
                locationUseCases.getLocationsWithLocationUsedIsNull()
            if (locationsWithLocationUsedIsNull.isNotEmpty()) {
                //get usageStats into db
                appUsageUseCases.computeUsage(locationsWithLocationUsedIsNull)
            }

            //update Apps in db with number of location Requests in th last 24 hours
            appUsageUseCases.updateAppUsageLast24Hours()
            isLoading.value = false
        }
    }

    override fun onSharedPreferenceChanged(pref: SharedPreferences?, key: String?) {
        if (key.equals("USAGE_PERMISSION_GRANTED")) {
            initData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = getSharedPreferences("PREFS_NAME", 0)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
