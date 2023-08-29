package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

class InitApps(

) {

    /**
     * Invokes the [getInstalledApps] function to retrieve a list of installed applications
     * along with their location-related permissions.
     *
     * This operator function provides a more concise way to call the [getInstalledApps] function
     * to obtain a sorted list of installed applications with location-related permissions.
     *
     * @return A sorted list of [App] instances representing the installed applications with
     *         location-related permissions.
     * @see getInstalledApps
     */
    operator fun invoke(): List<App> {
        return getInstalledApps()
    }

    /**
     * Retrieves a list of installed apps along with their permission details related to location.
     *
     * This function gathers information about installed applications, including app name,
     * package name, and location-related permissions. It also determines the visibility
     * of apps based on their installation status and location permission requests.
     *
     * @return A list of [App] objects containing app information and location permissions.
     */
    private fun getInstalledApps(): List<App> {
        // Get the application's package manager
        val packageManager: PackageManager =
            ApplicationProvider.application.applicationContext.packageManager

        // List to store the retrieved app information
        val appsList = mutableListOf<App>()

        // Get the list of all installed applications
        val packages: List<ApplicationInfo> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use enhanced API for Android 12 (API level 31) or higher
                packageManager.getInstalledApplications(
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                )
            } else {
                // Use standard API for earlier Android versions
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            }

        // Loop through each installed application
        for (applicationInfo in packages) {
            var packageName = ""
            var appName = ""
            var ACCESS_COARSE_LOCATION = false
            var ACCESS_FINE_LOCATION = false
            var ACCESS_BACKGROUND_LOCATION = false
            var requestedACCESS_COARSE_LOCATION = false
            var requestedACCESS_FINE_LOCATION = false
            var requestedACCESS_BACKGROUND_LOCATION = false
            var preinstalled = false
            var visible = false

            // Get detailed package information
            val packageInfo: PackageInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Use enhanced API for Android 12 (API level 31) or higher
                    packageManager.getPackageInfo(
                        applicationInfo.packageName,
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                    )
                } else {
                    // Use standard API for earlier Android versions
                    packageManager.getPackageInfo(
                        applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS
                    )
                }

            // Extract package name and app name
            packageName = packageInfo.packageName
            appName = packageManager.getApplicationLabel(applicationInfo) as String

            // Get requested permissions and their flags
            val requestedPermissions = packageInfo.requestedPermissions
            val requestedPermissionsFlags = packageInfo.requestedPermissionsFlags

            if (requestedPermissions != null) {
                // Loop through each requested permission and its corresponding flags
                for ((index, per) in requestedPermissions.withIndex()) {
                    // Check if the permission is granted (by comparing flags using bitwise AND)
                    if ((requestedPermissionsFlags[index] and PackageInfo.REQUESTED_PERMISSION_GRANTED) == PackageInfo.REQUESTED_PERMISSION_GRANTED) {
                        // Assign corresponding location permission based on the permission name
                        when (per) {
                            "android.permission.ACCESS_COARSE_LOCATION" -> ACCESS_COARSE_LOCATION = true
                            "android.permission.ACCESS_FINE_LOCATION" -> ACCESS_FINE_LOCATION = true
                            "android.permission.ACCESS_BACKGROUND_LOCATION" -> ACCESS_BACKGROUND_LOCATION = true
                        }
                    }

                    // Check if the app requested coarse location permission
                    if (per == "android.permission.ACCESS_COARSE_LOCATION") {
                        visible = true
                    }

                    //set requested permissions
                    if (requestedPermissions[index].contains("android.permission.ACCESS_COARSE_LOCATION")) {
                        requestedACCESS_COARSE_LOCATION = true
                    } else if (requestedPermissions[index].contains("android.permission.ACCESS_FINE_LOCATION")) {
                        requestedACCESS_FINE_LOCATION = true
                    } else if (requestedPermissions[index].contains("android.permission.ACCESS_BACKGROUND_LOCATION")) {
                        requestedACCESS_BACKGROUND_LOCATION = true
                    }

                }
            }

            // Determine app visibility based on system status or user installation
            if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                // Preinstalled app
                preinstalled = true
                // Only visible when app requested location, but not necessarily granted
            } else {
                // Installed by user
                // Always visible
                visible = true
            }

            // Create an App object with extracted details and add it to the list
            if (visible) {
                val app = App(
                    packageName = packageName,
                    appName = appName,
                    ACCESS_COARSE_LOCATION = ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION = ACCESS_FINE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION = ACCESS_BACKGROUND_LOCATION,
                    numberOfEstimatedRequests = 0,
                    favorite = false,
                    active = true,
                    requestedACCESS_COARSE_LOCATION = requestedACCESS_COARSE_LOCATION,
                    requestedACCESS_FINE_LOCATION = requestedACCESS_FINE_LOCATION,
                    requestedACCESS_BACKGROUND_LOCATION = requestedACCESS_BACKGROUND_LOCATION,
                    preinstalled = preinstalled
                )
                if (app.packageName != "") {
                    appsList.add(app)
                }
            }
        }

        // Sort the list of apps by their lowercase app names
        return appsList.sortedBy { it.appName.lowercase() }
    }
}
