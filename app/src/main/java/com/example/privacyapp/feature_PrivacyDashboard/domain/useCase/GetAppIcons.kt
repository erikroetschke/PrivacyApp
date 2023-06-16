package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

class GetAppIcons(private val packageName: String) {

    operator fun invoke(): Drawable? {
        val packageManager: PackageManager =
            ApplicationProvider.application.applicationContext.packageManager

        try
        {
            return packageManager.getApplicationIcon(packageName)
        }
        catch (e: PackageManager.NameNotFoundException)
        {
            e.printStackTrace()
        }
        return null
    }

}