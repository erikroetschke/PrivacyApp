package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.UsageEventProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppStatus
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppStatusTracker
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.UsageEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * The `ComputeUsage` class encapsulates the use case of processing user location data along with associated usage events
 * to update app status and usage information.
 *
 * @param repository Repository for app usage data.
 * @param locationRepository Repository for location data.
 * @param appRepository Repository for app data.
 * @param usageEventProvider Provider for usage events.
 */
class ComputeUsage(
    private val repository: AppUsageRepository,
    private val locationRepository: LocationRepository,
    private val appRepository: AppRepository,
    private val usageEventProvider: UsageEventProvider
) {

    private val sharedPrefs = PreferencesManagerImpl(ApplicationProvider.application)

    /**
     * Processes a list of [Location] objects along with associated usage events to update app status and usage information.
     * This method integrates location data and usage events, updating the state of each app's usage and status during the specified time interval.
     * Creates [AppUsage]s accordingly and sets the locationUsed flag of each location
     *
     * @param locations The list of [Location] objects representing user's movements and timestamps.
     *                  The list is expected to be sorted by timestamp in ascending order.
     *
     *
     * @see Location
     * @see UsageEvents
     * @see AppStatus
     * @see AppStatusTracker
     * @see AppUsage
     * @see UsageStatsManager
     */
    suspend operator fun invoke(locations: List<Location>) {

        //init
        var skip = false
        val locations: List<Location> = locations.sortedBy { it.timestamp }
        var currentEvent: UsageEvent = UsageEvent(0, 0, "")  //event for iteration
        val appStatusTracker = AppStatusTracker()
        val appUsages = HashMap<String, AppUsage>() // Store app usage objects
        var locationUsed = false

        //Get apps of which usage is relevant
        val listAppsWithForegroundPermission = if(sharedPrefs.getSettingBool(PreferencesManager.IS_COARSE_LOCATION_RELEVANT)){
            appRepository.getAppsSuspend().filter { it.ACCESS_COARSE_LOCATION }
        }else {
            appRepository.getAppsSuspend().filter { it.ACCESS_FINE_LOCATION }
        }
        val listAppsWithBackgroundPermission =
            appRepository.getAppsSuspend().filter { it.ACCESS_BACKGROUND_LOCATION }

        //get usage Stats NOTE: Events are only kept by the system for a few days.
        val eventList = usageEventProvider.getUsageEventsByInterval(
            locations.first().timestamp,
            locations.last().timestamp + sharedPrefs.getSettingInt(PreferencesManager.LOCATION_TRACKING_INTERVAL) * 1000L // in milliseconds
        )

        //get appStatusMap from last computation if needed, so if there were long running services/apps, they wont be missed
        val endOfLastComputation = appStatusTracker.getEndPointFromSharedPrefs()
        val diff = locations.first().timestamp - endOfLastComputation
        //if diff is not too big, the appStatusMap will be fetched from the last computation and updated to the current timestamp
        //if diff is to big, its not worth the expense
        if (diff < 1000 * 60 * 60 * 24) {
            //less than 24 h
            appStatusTracker.setAppStatusMap(appStatusTracker.getAppStatusMapFromSharedPrefs())
            appStatusTracker.setAppStatusMap(
                updateAppStatusMap(
                    appStatusTracker,
                    endOfLastComputation,
                    locations.first().timestamp
                ).toMutableMap()
            )
            //if user changed this recently this must be updated
            val listAppsRecentlyDeactivated = mutableListOf<String>()
            for ((name, status) in appStatusTracker.getAppStatusMap()) {
                if (listAppsWithForegroundPermission.any { it.packageName == name && !it.active }) {
                    listAppsRecentlyDeactivated.add(name)
                }
            }
            for (key in listAppsRecentlyDeactivated) {
                appStatusTracker.deleteApp(key)
            }

            for ((packageName, appStatus) in appStatusTracker.getActiveApps()) {
                locationUsed = true
                    val foreground = appStatus.foregroundCounter > 0
                    val background = appStatus.backgroundCounter > 0 || appStatus.serviceCounter > 0
                    appUsages[packageName] = AppUsage(
                        packageName,
                        locations[0].timestamp,
                        foreground,
                        background
                    )
            }

        }

        val eventIterator = eventList.iterator()
        var newOuterLoop = false

        for ((counter, location) in locations.withIndex()) {

            while (eventIterator.hasNext() || newOuterLoop) {

                //set on next event
                if (!newOuterLoop) {
                    currentEvent = eventIterator.next()
                }
                newOuterLoop = false

                //checks if current event is applicable to this location, or if its timestamp is beyond the next (location)-timestamp
                if (counter + 1 != locations.size) {
                    if (currentEvent.timeStamp >= locations[counter + 1].timestamp) {
                        newOuterLoop = true
                        break
                    }
                }

                //get packageName of Event and check if package has no relevant permissions
                val packageName = currentEvent.packageName

                //continue if app is deactivated by the user, so this app has no influence
                if (listAppsWithForegroundPermission.any { it.packageName == packageName && !it.active }) {
                    continue
                }

                var background = false
                if (!listAppsWithForegroundPermission.any { it.packageName == packageName }) {
                    continue //app is not relevant
                    // also Background permission cant be granted without normal foreground permission
                }

                if (listAppsWithBackgroundPermission.any { it.packageName == packageName }) {
                    background = true
                }

                //check if event is too for away from location,
                //this could happen when the location could not be tracked for while and there is a bigger gap between two locations
                if (currentEvent.timeStamp >= locations[counter].timestamp + sharedPrefs.getSettingInt(PreferencesManager.LOCATION_TRACKING_INTERVAL) * 3) {
                    // in this case still  update appusageMap but dont create appUsages
                    skip = true
                }

                //update appStatusMap

                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    locationUsed = true
                    appStatusTracker.onActivityResumed(packageName)

                    //create new AppUsage, if ACTIVITY_RESUMED occurs multiple times between two locations within the same package, it will be overwritten
                    if (!skip) {
                        appUsages.getOrPut(packageName) {
                            AppUsage(packageName, location.timestamp, foreground = true, background = false)
                        }.foreground = true
                    }
                } else if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED && (background || Build.VERSION.SDK_INT < 29)) {
                    locationUsed = true
                    appStatusTracker.onActivityPaused(packageName)

                    if (!skip) {
                        appUsages.getOrPut(packageName) {
                            AppUsage(packageName, location.timestamp, foreground = false, background = true)
                        }.background = true
                    }

                } else if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                    appStatusTracker.onActivityStopped(packageName)

                } else if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START && (background || Build.VERSION.SDK_INT < 30)) {
                    locationUsed = true
                    appStatusTracker.onServiceStart(packageName)
                    //create new appUsage
                    if (!skip) {
                        appUsages.getOrPut(packageName) {
                            AppUsage(packageName, location.timestamp, foreground = false, background = true)
                        }.background = true
                    }

                } else if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_STOP && (background || Build.VERSION.SDK_INT < 30)) {
                    appStatusTracker.onServiceStopped(packageName)
                }
                skip = false
            }

            //save AppUsage objects to the db
            for ((packageName, appUsage) in appUsages) {
                repository.insertAppUsage(appUsage)
            }

            appUsages.clear()

            for ((packageName, appStatus) in appStatusTracker.getActiveApps()) {
                locationUsed = true
                // if not already at the end, prepare AppUsage objects which are still running for the next iteration
                if (locations.lastIndex != counter) {
                    val foreground = appStatus.foregroundCounter > 0
                    val background = appStatus.backgroundCounter > 0 || appStatus.serviceCounter > 0
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
            locationUsed = false

        }
        //save appStatusMap for future computations
        appStatusTracker.saveAppStatusMapAndEndPoint(locations.last().timestamp + sharedPrefs.getSettingInt(PreferencesManager.LOCATION_TRACKING_INTERVAL) * 1000L)
    }

    /**
     * Updates the provided app status map based on usage events within the specified time range.
     * The map is updated with app status changes corresponding to the usage events.
     *
     * @param appStatusTracker The Tracker class containing app statuses to be updated.
     * @param startTimestamp The start timestamp of the time range for querying usage events.
     * @param endTimestamp The end timestamp of the time range for querying usage events.
     * @return The updated HashMap of app statuses after processing the usage events.
     */
    private suspend fun updateAppStatusMap(
        appStatusTracker: AppStatusTracker,
        startTimestamp: Long,
        endTimestamp: Long
    ): Map<String, AppStatus> {
        var currentEvent: UsageEvent
        val eventList = usageEventProvider.getUsageEventsByInterval(
            startTimestamp,
            endTimestamp
        )
        val eventIterator = eventList.iterator()

        //Get apps of which usage is relevant
        val listAppsWithForegroundPermission = if(sharedPrefs.getSettingBool(PreferencesManager.IS_COARSE_LOCATION_RELEVANT)){
            appRepository.getAppsSuspend().filter { it.ACCESS_COARSE_LOCATION }
        }else {
            appRepository.getAppsSuspend().filter { it.ACCESS_FINE_LOCATION }
        }
        val listAppsWithBackgroundPermission =
            appRepository.getAppsSuspend().filter { it.ACCESS_BACKGROUND_LOCATION }

        while (eventIterator.hasNext()) {
            //set on next event
            currentEvent = eventIterator.next()

            //get packageName of Event and check if package has no relevant permissions
            val packageName = currentEvent.packageName

            var background = false
            if (!listAppsWithForegroundPermission.any { it.packageName == packageName }) {
                continue //app is not relevant
                // also Background permission cant be granted without normal foreground permission
            }

            if (listAppsWithBackgroundPermission.any { it.packageName == packageName }) {
                background = true
            }

            //update appStatusMap

            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                appStatusTracker.onActivityResumed(packageName)

            } else if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED && (background || Build.VERSION.SDK_INT < 29)) {
                appStatusTracker.onActivityPaused(packageName)

            } else if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                appStatusTracker.onActivityStopped(packageName)

            } else if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START && (background || Build.VERSION.SDK_INT < 30)) {
                appStatusTracker.onServiceStart(packageName)

            } else if (currentEvent.eventType == UsageEvents.Event.FOREGROUND_SERVICE_STOP && (background || Build.VERSION.SDK_INT < 30)) {
                appStatusTracker.onServiceStopped(packageName)
            }
        }
        return appStatusTracker.getAppStatusMap()
    }
}