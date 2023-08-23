package com.example.privacyapp.feature_PrivacyDashboard.domain.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Class to track the activity and service status of apps.
 */
class AppStatusTracker {

    private val sharedPref = ApplicationProvider.application.getSharedPreferences(
        "PrivacyApp.LatestAppStatusMap", Context.MODE_PRIVATE
    )
    private val APP_STATUS_MAP = "AppStatusTrackerMap"
    private val END_OF_LAST_COMPUTATION = "EndOfLastComputation"

    private var appStatusMap: MutableMap<String, AppStatus> = mutableMapOf()

    /**
     * Sets the app status map with a new map.
     * @param appStatusMap The new app status map to be set.
     */
    fun setAppStatusMap(appStatusMap: MutableMap<String, AppStatus>){
        this.appStatusMap = appStatusMap
    }

    /**
     * Deletes an app's status from the tracker.
     * @param packageName The package name of the app to be deleted.
     */
    fun deleteApp(packageName: String){
        appStatusMap.remove(packageName)
    }

    /**
     * Called when an activity is resumed.
     * @param packageName The package name of the app.
     */
    fun onActivityResumed(packageName: String) {
        val appStatus = appStatusMap.getOrPut(packageName) {
            AppStatus(0, 0, 0)
        }

        val backgroundCounter = if(appStatus.backgroundCounter > 0){
            appStatus.backgroundCounter - 1
        }else {
            0
        }

        appStatusMap[packageName] = appStatus.copy(
            foregroundCounter = appStatus.foregroundCounter + 1,
            backgroundCounter = backgroundCounter
        )
    }

    /**
     * Called when an activity is paused.
     * @param packageName The package name of the app.
     */
    fun onActivityPaused(packageName: String) {
        val appStatus = appStatusMap.getOrPut(packageName) {
            AppStatus(0, 0, 0)
        }

        val foregroundCounter = if(appStatus.foregroundCounter > 0){
            appStatus.foregroundCounter - 1
        }else {
            0
        }

        appStatusMap[packageName] = appStatus.copy(
            foregroundCounter = foregroundCounter,
            backgroundCounter = appStatus.backgroundCounter + 1
        )
    }

    /**
     * Called when an activity is stopped.
     * @param packageName The package name of the app.
     */
    fun onActivityStopped(packageName: String) {
        val appStatus = appStatusMap.getOrPut(packageName) {
            AppStatus(0, 0, 0)
        }

        var foregroundCounter = 0
        var backgroundCounter = 0
        /*if(appStatus.backgroundCounter > 0){
            backgroundCounter = appStatus.backgroundCounter - 1
        }else if(appStatus.foregroundCounter > 0){
            foregroundCounter = appStatus.foregroundCounter - 1
        }*/

        appStatusMap[packageName] = appStatus.copy(
            foregroundCounter = foregroundCounter,
            backgroundCounter = backgroundCounter
        )
    }

    /**
     * Called when a service is started.
     * @param packageName The package name of the app.
     */
    fun onServiceStart(packageName: String) {
        val appStatus = appStatusMap.getOrPut(packageName) {
            AppStatus(0, 0, 0)
        }

        appStatusMap[packageName] = appStatus.copy(
            serviceCounter = appStatus.serviceCounter + 1
        )
    }

    /**
     * Called when a service is stopped.
     * @param packageName The package name of the app.
     */
    fun onServiceStopped(packageName: String) {
        val appStatus = appStatusMap.getOrPut(packageName) {
            AppStatus(0, 0, 0)
        }

        val serviceCounter = if(appStatus.serviceCounter > 0){
            appStatus.serviceCounter - 1
        }else {
            0
        }

        appStatusMap[packageName] = appStatus.copy(
            serviceCounter = serviceCounter
        )
    }

    /**
     * Retrieves a map of active apps with non-zero activity or service counts.
     * @return A map of active apps and their status.
     */
    fun getActiveApps(): Map<String, AppStatus> {
        return appStatusMap.filter { it.value.foregroundCounter > 0 || it.value.backgroundCounter > 0 || it.value.serviceCounter > 0 }
    }

    /**
     * Retrieves the current app status map.
     * @return The map containing app status information.
     */
    fun getAppStatusMap(): Map<String, AppStatus> {
        return this.appStatusMap
    }

    /**
     * Retrieves the app status map from the shared preferences.
     *
     * @return The HashMap containing app statuses.
     */
    fun getAppStatusMapFromSharedPrefs(): HashMap<String, AppStatus> {
        val string =
            sharedPref.getString(APP_STATUS_MAP, Gson().toJson(HashMap<String, AppStatus>()))
        return Gson().fromJson(string, object : TypeToken<HashMap<String, AppStatus>>() {}.type)
    }

    /**
     * Retrieves the endpoint timestamp from the shared preferences.
     *
     * @return The endpoint timestamp.
     */
    fun getEndPointFromSharedPrefs(): Long {
        return sharedPref.getLong(END_OF_LAST_COMPUTATION, 0L)
    }

    /**
     * Saves the provided app status map and endpoint timestamp to the shared preferences.
     *
     * @param map The HashMap containing app statuses to be saved.
     * @param endpoint The endpoint timestamp to be saved.
     */
    fun saveAppStatusMapAndEndPoint(endpoint: Long) {
        //save map
        val jsonString = Gson().toJson(getActiveApps())
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

}