package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import android.util.Log
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppStatus
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

class ComputeUsage(
    private val repository: AppUsageRepository,
    private val locationRepository: LocationRepository,
    private val appRepository: AppRepository
) {

    suspend operator fun invoke(locations: List<Location>) {

        //init
        val locationsCopy: List<Location> =
            locations.sortedBy { it.timestamp }     //sort by timestamp
        val currentEvent = UsageEvents.Event()  //event for iteration
        val nonSystemsAppsList = getAppsList()
        val appStatusMap =
            HashMap<String, AppStatus>() //to track weather an app is currently running
        val appUsages =
            HashMap<String, AppUsage>() //final appUsage objects in a Hashmap to access them easily
        var newOuterForLoop = false
        //Get apps of which usage is relevant, ACCESS_CORSE_LOCATION is not considered as relevant, as it has am accuracy of 2km
        val listAppsWithForegroundPermission =
            appRepository.getApps().filter { it.ACCESS_FINE_LOCATION }
        val listAppsWithBackgroundPermission =
            appRepository.getApps().filter { it.ACCESS_BACKGROUND_LOCATION }

        //get usage Stats NOTE: Events are only kept by the system for a few days.
        val usageStatsManager =
            ApplicationProvider.application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents: UsageEvents? = usageStatsManager.queryEvents(
            locationsCopy.first().timestamp,
            locationsCopy.last().timestamp + 60000
        )

        for ((counter, location) in locationsCopy.withIndex()) {
            if (usageEvents != null) {
                var locationUsed = false
                while (usageEvents.hasNextEvent()) {
                    //checks if current event is applicable to this location, or if its timestamp is beyond the next (location)-timestamp
                    if(counter + 1 != locationsCopy.size){
                        if (currentEvent.timeStamp >= locationsCopy[counter + 1].timestamp) {
                            newOuterForLoop = true
                            break
                        }
                    }
                    //check if event is too for away from location(more than 3 minutes),
                    //this could happen when the location could not be tracked for while and there is a bigger gap between to locations
                    if(currentEvent.timeStamp >= locationsCopy[counter].timestamp + 180000) {
                        usageEvents.getNextEvent(currentEvent)
                        continue
                    }

                    //if a new location has begun, skip one iteration so the last event is reconsidered in this locaion
                    if (!newOuterForLoop) {
                        usageEvents.getNextEvent(currentEvent)
                    }
                    newOuterForLoop = false


                    //get packageName of Event and check if package has no relevant permissions
                    val packageName = currentEvent.packageName

                    //continue if app is deactivated by the user, so this app has no influence
                    if(appRepository.getAppByName(packageName)?.active == false){
                        continue
                    }

                    var foreground = false
                    var background = false
                    if (listAppsWithForegroundPermission.any { it.packageName == packageName }) {
                        foreground = true
                    }
                    if (listAppsWithBackgroundPermission.any { it.packageName == packageName }) {
                        background =
                            true // Background permission cant be granted without normal foreground permission
                    }

                    //update appStatusMap
                    if (foreground && nonSystemsAppsList.containsKey(packageName)) {

                        if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                            locationUsed = true
                            when (appStatusMap[packageName]) {
                                AppStatus.BACKGROUND -> appStatusMap[packageName] = AppStatus.FOREGROUND
                                AppStatus.NOT_RUNNING -> appStatusMap[packageName] = AppStatus.FOREGROUND
                                AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] = AppStatus.FOREGROUND_AND_SERVICE
                                AppStatus.SERVICE -> appStatusMap[packageName] = AppStatus.FOREGROUND_AND_SERVICE
                                null -> appStatusMap[packageName] = AppStatus.FOREGROUND
                                else -> {}
                            }

                            //create new AppUsage, if ACTIVITY_RESUMED occurs multiple times between two locations within the same package, it will be overwritten
                            appUsages[packageName] = AppUsage(
                                packageName, location.timestamp,
                                foreground = true,
                                background = false
                            )
                        }

                        if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED && background) {
                            locationUsed = true
                            when (appStatusMap[packageName]){
                                AppStatus.FOREGROUND -> appStatusMap[packageName] = AppStatus.BACKGROUND
                                AppStatus.NOT_RUNNING -> appStatusMap[packageName] = AppStatus.BACKGROUND
                                AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] = AppStatus.BACKGROUND_AND_SERVICE
                                AppStatus.SERVICE -> appStatusMap[packageName] = AppStatus.BACKGROUND_AND_SERVICE
                                null -> appStatusMap[packageName] = AppStatus.BACKGROUND
                                else -> {}
                            }

                            if(appUsages[packageName] == null) {
                                //As it must have been in Foreground before this not trigger in most cases,
                                //but there could be the case that the start of the application was missed,
                                //as it happened before the first (location)-timestamp.
                                //Furthermore, this will happen everytime with this app itself,
                                // when tracking was not turned on when the app got opened
                                appUsages[packageName] = AppUsage(
                                    packageName, location.timestamp,
                                    foreground = false,
                                    background = true
                                )
                            }else {
                                appUsages[packageName]?.background = true
                            }

                        }

                        if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                            when(appStatusMap[packageName]) {
                                AppStatus.FOREGROUND -> appStatusMap[packageName] = AppStatus.NOT_RUNNING
                                AppStatus.BACKGROUND -> appStatusMap[packageName] = AppStatus.NOT_RUNNING
                                AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] = AppStatus.SERVICE
                                AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] = AppStatus.SERVICE
                                else -> {}
                            }
                        }

                        if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START && background){
                            locationUsed = true
                            when (appStatusMap[packageName]){
                                AppStatus.FOREGROUND -> appStatusMap[packageName] = AppStatus.FOREGROUND_AND_SERVICE
                                AppStatus.BACKGROUND -> appStatusMap[packageName] = AppStatus.BACKGROUND_AND_SERVICE
                                null -> {appStatusMap[packageName] = AppStatus.SERVICE
                                    //create new appUsage
                                    appUsages[packageName] = AppUsage(
                                        packageName, location.timestamp,
                                        foreground = false,
                                        background = true
                                    )
                                }
                                else -> {}
                            }
                            appUsages[packageName]?.background = true

                        }

                        if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_STOP && background){
                            when (appStatusMap[packageName]){
                                AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] = AppStatus.FOREGROUND
                                AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] = AppStatus.BACKGROUND
                                AppStatus.SERVICE -> appStatusMap[packageName] = AppStatus.NOT_RUNNING
                                else -> {}
                            }
                        }
                    }
                }
                //save AppUsage objects to db, because they might be overwritten in the next iteration
                for ((packageName, appUsage) in appUsages) {
                    repository.insertAppUsage(appUsage)
                }
                appUsages.clear()

                for ((packageName, appStatus) in appStatusMap) {
                    if (appStatus != AppStatus.NOT_RUNNING) {
                        locationUsed = true
                        // if not already at the end, prepare AppUsage objects which are still running for the next iteration
                        if (locations.lastIndex != counter) {
                            val foreground = appStatus == AppStatus.FOREGROUND
                            val background = appStatus == AppStatus.BACKGROUND
                            appUsages[packageName] = AppUsage(
                                packageName,
                                locations[counter + 1].timestamp,
                                foreground,
                                background
                            )
                        }
                    }
                }
                //if location was used once by an app in the interval set locationUsed to true in the location object
                location.locationUsed = locationUsed
                locationRepository.insertLocation(location)
            }
        }
        return
    }

    private fun getAppsList(): Map<String, String> {
        val packageManager: PackageManager =
            ApplicationProvider.application.applicationContext.packageManager
        val appInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        val appInfoMap = HashMap<String, String>()
        for (appInfo in appInfos) {
            appInfoMap[appInfo.packageName] =
                packageManager.getApplicationLabel(appInfo).toString()
            /*if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                //system application

            } else {
                //user app
                appInfoMap[appInfo.packageName] =
                    packageManager.getApplicationLabel(appInfo).toString()
            }*/
        }
        return appInfoMap
    }
}