package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            var packageName = ""
            var appName = ""
            var ACCESS_COARSE_LOCATION = false
            var ACCESS_FINE_LOCATION = false
            var ACCESS_BACKGROUND_LOCATION = false

            if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                // System application
            } else {
                // Installed by user
                if (applicationInfo.name == null) {
                    continue
                }
                val packageInfo = packageManager.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                packageName = packageInfo.packageName
                appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)) as String
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
            }
            val app = App(
                packageName,
                appName,
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                ACCESS_BACKGROUND_LOCATION,
                15,
                false
            )
            appsList.add(app)
        }
        return appsList
    }
}
