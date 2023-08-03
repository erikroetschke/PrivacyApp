package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.appUseCases

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

class InitApps(

) {

    operator fun invoke(): List<App> {
        return getInstalledApps()
    }

    private fun getInstalledApps(): List<App> {
        val packageManager: PackageManager =
            ApplicationProvider.application.applicationContext.packageManager

        val appsList = mutableListOf<App>()
        // get list of all the apps installed
        var packages: List<ApplicationInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        for (applicationInfo in packages) {
            var packageName = ""
            var appName = ""
            var ACCESS_COARSE_LOCATION = false
            var ACCESS_FINE_LOCATION = false
            var ACCESS_BACKGROUND_LOCATION = false
            var nonSystemAppOrSystemAppWithLocationPermission = false

            var packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
            } else {
                packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS)
            }

            packageName = packageInfo.packageName
            appName = packageManager.getApplicationLabel(applicationInfo) as String

            //Get Permissions
            val requestedPermissions = packageInfo.requestedPermissions
            if (requestedPermissions != null) {
                for (i in requestedPermissions.indices) {
                    if (requestedPermissions[i].contains("android.permission.ACCESS_COARSE_LOCATION")) {
                        ACCESS_COARSE_LOCATION = true
                    } else if (requestedPermissions[i].contains("android.permission.ACCESS_FINE_LOCATION")) {
                        ACCESS_FINE_LOCATION = true
                    } else if (requestedPermissions[i].contains("android.permission.ACCESS_BACKGROUND_LOCATION")) {
                        ACCESS_BACKGROUND_LOCATION = true
                    }
                }
            }

            if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                if(ACCESS_COARSE_LOCATION && ACCESS_FINE_LOCATION) {
                    //preinstalled
                    nonSystemAppOrSystemAppWithLocationPermission = true
                }
            }else {
                // Installed by user
                nonSystemAppOrSystemAppWithLocationPermission = true
            }
            if(nonSystemAppOrSystemAppWithLocationPermission) {

                val app = App(
                    packageName,
                    appName,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION,
                    0,
                    false,
                    true
                )
                if (app.packageName != ""){
                    appsList.add(app)
                }
            }


        }
        return appsList.sortedBy { it.appName.lowercase() }
    }
}
