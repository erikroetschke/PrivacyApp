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
        val nonSystemsAppsList = getNonSystemAppsList()
        val appStatusMap =
            HashMap<String, AppStatus>() //to track weather an app is currently running
        val appUsages =
            HashMap<String, AppUsage>() //final appUsage objects in a Hashmap to access them easily
        var newOuterForLoop = false
        val listAppsWithForegroundPermission = appRepository.getApps().filter { it.ACCESS_COARSE_LOCATION }

        //get usage Stats
        val usageStatsManager =
            ApplicationProvider.application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents: UsageEvents? = usageStatsManager.queryEvents(
            locationsCopy.first().timestamp,
            locationsCopy.last().timestamp
        )



        for ((counter, location) in locationsCopy.withIndex()) {
            if (usageEvents != null) {
                var locationUsed = false
                while (usageEvents.hasNextEvent()) {
                    //checks if current event is applicable to the next location
                    if (currentEvent.timeStamp >= locationsCopy[counter + 1].timestamp) {
                        newOuterForLoop = true
                        break
                    }
                    //so that the last element before the break is considered in the next for loop
                    if (!newOuterForLoop) {
                        usageEvents.getNextEvent(currentEvent)
                    }
                    newOuterForLoop = false

                    val packageName = currentEvent.packageName
                    //update appStatusMap
                    if (nonSystemsAppsList.containsKey(packageName)) {
                        when (currentEvent.eventType) {
                            UsageEvents.Event.ACTIVITY_RESUMED -> {
                                locationUsed = true
                                appStatusMap[packageName] = AppStatus.FOREGROUND
                                //create new AppUsage, if ACTIVITY_RESUMED occurs multiple times between two locations within the same package, it will be overwritten
                                appUsages[packageName] = AppUsage(
                                    packageName, location.timestamp,
                                    foreground = true,
                                    background = false
                                )
                            }

                            UsageEvents.Event.ACTIVITY_PAUSED -> {
                                locationUsed = true
                                appStatusMap[packageName] = AppStatus.BACKGROUND
                                //As it must have been in Foreground before, add also background
                                appUsages[packageName]?.background = true
                            }

                            UsageEvents.Event.ACTIVITY_STOPPED -> {
                                appStatusMap[packageName] = AppStatus.NOT_RUNNING
                            }

                            //TODO implement foregroundservice start and end 
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

    private fun getNonSystemAppsList(): Map<String, String> {
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
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                //system application
            } else {
                //user app
                appInfoMap[appInfo.packageName] =
                    packageManager.getApplicationLabel(appInfo).toString()
            }
        }
        return appInfoMap
    }
}