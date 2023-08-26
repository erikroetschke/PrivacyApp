package com.example.privacyapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.privacyapp.feature_PrivacyDashboard.util.LOCATION_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

/**
 * The main Application class for the PrivacyApp.
 */
@HiltAndroidApp
class PrivacyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        // Create a notification channel for location-related notifications
        val channel = NotificationChannel(
            LOCATION_CHANNEL_ID,
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}