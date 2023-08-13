package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppStatus
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.util.LOCATION_INTERVAL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Stack

class ComputeUsage(
    private val repository: AppUsageRepository,
    private val locationRepository: LocationRepository,
    private val appRepository: AppRepository
) {

    private val sharedPref = ApplicationProvider.application.getSharedPreferences(
        "PrivacyApp.AppStatusMap", Context.MODE_PRIVATE
    )
    private val APP_STATUS_MAP = "AppStatusMap"
    private val END_OF_LAST_COMPUTATION = "EndOfLastComputation"

    private val usageStatsManager =
        ApplicationProvider.application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    suspend operator fun invoke(locations: List<Location>) {

        //init
        var skip = false
        val locations: List<Location> =
            locations.sortedBy { it.timestamp }     //sort by timestamp
        val currentEvent = UsageEvents.Event()  //event for iteration
        var appStatusMap =
            HashMap<String, AppStatus>() //to track weather an app is currently running
        val appUsages =
            HashMap<String, AppUsage>() //final appUsage objects in a Hashmap to access them easily
        val appsStack =
            Stack<Pair<String, Long>>() //Stack of apps that have been stated running even before this interval [locations.first.timestamp, locations.last.timestamp] but terminated in the interval
        var slice = 0 //helper for the stack above
        var newOuterForLoop = false
        //Get apps of which usage is relevant, ACCESS_CORSE_LOCATION is not considered as relevant, as it has am accuracy of 2km
        val listAppsWithForegroundPermission =
            appRepository.getAppsSuspend().filter { it.ACCESS_FINE_LOCATION }
        val listAppsWithBackgroundPermission =
            appRepository.getAppsSuspend().filter { it.ACCESS_BACKGROUND_LOCATION }

        //get usage Stats NOTE: Events are only kept by the system for a few days.

        val usageEvents: UsageEvents? = usageStatsManager.queryEvents(
            locations.first().timestamp,
            locations.last().timestamp + LOCATION_INTERVAL
        )

        //get appStatusMap from last computation if needed, so if there were long running services/apps, they wont be missed
        val endOfLastComputation = getEndPoint()
        val diff = locations.first().timestamp - endOfLastComputation
        //if diff is not too big, the appStatusMap will be fetched from the last computation and updated to the current timestamp
        //if diff is to big, its not worth the expense
        if (diff < 1000 * 60 * 60 * 24) {
            //less than 24 h
            appStatusMap = updateAppStatusMap(getAppStatusMap(), endOfLastComputation, locations.first().timestamp)
            //if user changed this recently this must be updated
            val listAppsRecentlyDeactivated = mutableListOf<String>()
            for ((name, status) in appStatusMap) {
                if (listAppsWithForegroundPermission.any { it.packageName == name }) {
                    listAppsRecentlyDeactivated.add(name)
                }
            }
            for (key in listAppsRecentlyDeactivated) {
                appStatusMap.remove(key)
            }
        }

        for ((counter, location) in locations.withIndex()) {
            if (usageEvents != null) {
                var locationUsed = false
                while (usageEvents.hasNextEvent()) {
                    //checks if current event is applicable to this location, or if its timestamp is beyond the next (location)-timestamp
                    if (counter + 1 != locations.size) {
                        if (currentEvent.timeStamp >= locations[counter + 1].timestamp) {
                            newOuterForLoop = true
                            break
                        }
                    }
                    //check if event is too for away from location(more than 3 minutes),
                    //this could happen when the location could not be tracked for while and there is a bigger gap between two locations
                    if (currentEvent.timeStamp >= locations[counter].timestamp + 180000) {
                        usageEvents.getNextEvent(currentEvent)
                        skip = true
                    }

                    //if a new location has begun, skip one iteration so the last event is reconsidered in this locaion
                    if (!newOuterForLoop) {
                        usageEvents.getNextEvent(currentEvent)
                    }
                    newOuterForLoop = false


                    //get packageName of Event and check if package has no relevant permissions
                    val packageName = currentEvent.packageName


                    var background = false
                    if (!listAppsWithForegroundPermission.any { it.packageName == packageName }) {
                        continue //app is not relevant
                        // also Background permission cant be granted without normal foreground permission
                    }

                    if (listAppsWithBackgroundPermission.any { it.packageName == packageName }) {
                        background =
                            true
                    }


                    //continue if app is deactivated by the user, so this app has no influence
                    if(listAppsWithForegroundPermission.any { it.packageName == packageName && !it.active }) {
                        continue
                    }

                    //update appStatusMap

                    if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                        locationUsed = true
                        when (appStatusMap[packageName]) {
                            AppStatus.BACKGROUND -> appStatusMap[packageName] = AppStatus.FOREGROUND
                            AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] =
                                AppStatus.FOREGROUND_AND_SERVICE

                            AppStatus.SERVICE -> appStatusMap[packageName] =
                                AppStatus.FOREGROUND_AND_SERVICE

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

                    if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED && (background || Build.VERSION.SDK_INT < 29)) {
                        locationUsed = true
                        when (appStatusMap[packageName]) {
                            AppStatus.FOREGROUND -> appStatusMap[packageName] = AppStatus.BACKGROUND
                            AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] =
                                AppStatus.BACKGROUND_AND_SERVICE

                            AppStatus.SERVICE -> appStatusMap[packageName] =
                                AppStatus.BACKGROUND_AND_SERVICE

                            null -> appStatusMap[packageName] = AppStatus.BACKGROUND
                            else -> {}
                        }

                        if (appUsages[packageName] == null) {
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
                        } else {
                            appUsages[packageName]?.background = true
                        }

                    }

                    if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                        when (appStatusMap[packageName]) {
                            AppStatus.FOREGROUND -> appStatusMap.remove(packageName)
                            AppStatus.BACKGROUND -> appStatusMap.remove(packageName)
                            AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] =
                                AppStatus.SERVICE

                            AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] =
                                AppStatus.SERVICE

                            else -> {
                                //in this case the activity has been running before this app started tracking
                                //so all the time there was no info that this app was running already in the background
                                //so remember this timestamp and after the outer for loop completes, go back and edit the
                                //UsageStats and locationUsages accordingly
                                appsStack.add(Pair(packageName, location.timestamp))
                                slice = counter
                            }
                        }
                    }

                    if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START && (background || Build.VERSION.SDK_INT < 30)) {
                        locationUsed = true
                        when (appStatusMap[packageName]) {
                            AppStatus.FOREGROUND -> appStatusMap[packageName] =
                                AppStatus.FOREGROUND_AND_SERVICE

                            AppStatus.BACKGROUND -> appStatusMap[packageName] =
                                AppStatus.BACKGROUND_AND_SERVICE

                            null -> {
                                appStatusMap[packageName] = AppStatus.SERVICE
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

                    if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_STOP && (background || Build.VERSION.SDK_INT < 30)) {
                        when (appStatusMap[packageName]) {
                            AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] =
                                AppStatus.FOREGROUND

                            AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] =
                                AppStatus.BACKGROUND

                            AppStatus.SERVICE -> appStatusMap.remove(packageName)
                            else -> {
                                //in this case the service has been running before this app started tracking
                                //so all the time there was no info that this service was running already in the background
                                //so remember this timestamp and after the outer for loop completes, go back and edit the
                                //UsageStats and locationUsages accordingly
                                appsStack.add(Pair(packageName, location.timestamp))
                                slice = counter
                            }
                        }
                    }

                }
                //save AppUsage objects to db, because they might be overwritten in the next iteration
                //skip saving when event more than 3 min away from location.timestamp
                if (!skip) {
                    for ((packageName, appUsage) in appUsages) {
                        repository.insertAppUsage(appUsage)
                    }
                }
                appUsages.clear()

                for ((packageName, appStatus) in appStatusMap) {
                    locationUsed = true
                    // if not already at the end, prepare AppUsage objects which are still running for the next iteration
                    if (locations.lastIndex != counter) {
                        val foreground =
                            (appStatus == AppStatus.FOREGROUND || appStatus == AppStatus.FOREGROUND_AND_SERVICE)
                        val background =
                            (appStatus == AppStatus.BACKGROUND || appStatus == AppStatus.BACKGROUND_AND_SERVICE || appStatus == AppStatus.SERVICE)
                        appUsages[packageName] = AppUsage(
                            packageName,
                            locations[counter + 1].timestamp,
                            foreground,
                            background
                        )
                    }
                }
                //if location was used once by an app in the interval set locationUsed to true in the location object
                location.locationUsed = locationUsed
                locationRepository.insertLocation(location)
            }
        }
        //save appStatusMap for future computations
        saveAppStatusMapAndEndPoint(appStatusMap, locations.last().timestamp + LOCATION_INTERVAL)
        //if an event was found, that denoted that an app/service was destroyed, but there was no starting point/event
        //now go back from the end and add also those UsageEvents and manipulate locations accordingly
        //TODO test implementation
        if (appsStack.isNotEmpty()) {
            appUsages.clear()
            val locationsBackwards = locations.subList(0, slice).reversed()
            for (location in locationsBackwards) {
                //cant be null, as location must have been processed before, therefore waive a try catch block, as this will slow down the app
                if (!location.locationUsed!!) {
                    locationRepository.insertLocation(location)
                }
                for (app in appsStack) {
                    if (app.second <= location.timestamp) {
                        appUsages[app.first] = AppUsage(
                            packageName = app.first,
                            timestamp = app.second,
                            foreground = true,
                            background = false
                        )
                    }
                }
            }
            //insert appusages, if there was already an existing appusage for this app at this timestamp it will be overwritten
            for ((packageName, appUsage) in appUsages) {
                repository.insertAppUsage(appUsage)
            }
        }
    }

    private suspend fun updateAppStatusMap(
        appStatusMap: HashMap<String, AppStatus>,
        startTimestamp: Long,
        endTimestamp: Long
    ): HashMap<String, AppStatus> {
        val usageEvents: UsageEvents? = usageStatsManager.queryEvents(
            startTimestamp,
            endTimestamp
        )
        val currentEvent = UsageEvents.Event()

        if (usageEvents != null) {

            //Get apps of which usage is relevant, ACCESS_CORSE_LOCATION is not considered as relevant, as it has am accuracy of 2km
            val listAppsWithForegroundPermission =
                appRepository.getAppsSuspend().filter { it.ACCESS_FINE_LOCATION }
            val listAppsWithBackgroundPermission =
                appRepository.getAppsSuspend().filter { it.ACCESS_BACKGROUND_LOCATION }

            while (usageEvents.hasNextEvent()) {

                usageEvents.getNextEvent(currentEvent)

                //get packageName of Event and check if package has no relevant permissions
                val packageName = currentEvent.packageName

                var background = false
                if (!listAppsWithForegroundPermission.any { it.packageName == packageName }) {
                    continue
                }
                if (listAppsWithBackgroundPermission.any { it.packageName == packageName }) {
                    background =
                        true // Background permission cant be granted without normal foreground permission
                }

                //continue if app is deactivated by the user, so this app has no influence
                if(listAppsWithForegroundPermission.any { it.packageName == packageName && !it.active }) {
                    continue
                }

                //update appStatusMap
                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    when (appStatusMap[packageName]) {
                        AppStatus.BACKGROUND -> appStatusMap[packageName] = AppStatus.FOREGROUND
                        AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] =
                            AppStatus.FOREGROUND_AND_SERVICE

                        AppStatus.SERVICE -> appStatusMap[packageName] =
                            AppStatus.FOREGROUND_AND_SERVICE

                        null -> appStatusMap[packageName] = AppStatus.FOREGROUND
                        else -> {}
                    }

                }

                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED && background) {
                    when (appStatusMap[packageName]) {
                        AppStatus.FOREGROUND -> appStatusMap[packageName] = AppStatus.BACKGROUND
                        AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] =
                            AppStatus.BACKGROUND_AND_SERVICE

                        AppStatus.SERVICE -> appStatusMap[packageName] =
                            AppStatus.BACKGROUND_AND_SERVICE

                        null -> appStatusMap[packageName] = AppStatus.BACKGROUND
                        else -> {}
                    }
                }

                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                    when (appStatusMap[packageName]) {
                        AppStatus.FOREGROUND -> appStatusMap.remove(packageName)
                        AppStatus.BACKGROUND -> appStatusMap.remove(packageName)
                        AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] =
                            AppStatus.SERVICE

                        AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] =
                            AppStatus.SERVICE

                        else -> {}
                    }
                }

                if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START && background) {
                    when (appStatusMap[packageName]) {
                        AppStatus.FOREGROUND -> appStatusMap[packageName] =
                            AppStatus.FOREGROUND_AND_SERVICE

                        AppStatus.BACKGROUND -> appStatusMap[packageName] =
                            AppStatus.BACKGROUND_AND_SERVICE

                        null -> {
                            appStatusMap[packageName] = AppStatus.SERVICE
                        }

                        else -> {}
                    }
                }

                if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_STOP && background) {
                    when (appStatusMap[packageName]) {
                        AppStatus.FOREGROUND_AND_SERVICE -> appStatusMap[packageName] =
                            AppStatus.FOREGROUND

                        AppStatus.BACKGROUND_AND_SERVICE -> appStatusMap[packageName] =
                            AppStatus.BACKGROUND

                        AppStatus.SERVICE -> appStatusMap.remove(packageName)
                        else -> {}
                    }
                }

            }
        }

        return appStatusMap
    }

    private fun getAppStatusMap(): HashMap<String, AppStatus> {
        val string =
            sharedPref.getString(APP_STATUS_MAP, Gson().toJson(HashMap<String, AppStatus>()))
        return Gson().fromJson(string, object : TypeToken<HashMap<String, AppStatus>>() {}.type)
    }

    private fun getEndPoint(): Long {
        return sharedPref.getLong(END_OF_LAST_COMPUTATION, 0L)
    }

    private fun saveAppStatusMapAndEndPoint(map: HashMap<String, AppStatus>, endpoint: Long) {
        //save map
        val jsonString = Gson().toJson(map)
        with(sharedPref.edit()) {
            putString(APP_STATUS_MAP, jsonString)
            apply()
        }
        //save endPoint
        with(sharedPref.edit()) {
            putLong(END_OF_LAST_COMPUTATION, endpoint)
            apply()
        }
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