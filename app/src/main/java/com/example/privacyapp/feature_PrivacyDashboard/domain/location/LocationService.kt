package com.example.privacyapp.feature_PrivacyDashboard.domain.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.privacyapp.R
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.DefaultLocationClient
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.util.LOCATION_CHANNEL_ID
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


/**
 * Service responsible for tracking the user's location in the background.
 * This service uses the FusedLocationProviderClient to receive location updates at a specified interval.
 * It stores the received location data in the database and displays an ongoing notification showing the tracked location.
 */
class LocationService : Service() {

    private val sharedPrefs = PreferencesManagerImpl(ApplicationProvider.application)

    // Coroutine scope to manage location updates
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize the location client using DefaultLocationClient
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Starts the location tracking process.
     */
    private fun start() {

        // Get the location tracking interval from preferences and convert it to milliseconds
        val locationInterval = sharedPrefs.getSettingInt(PreferencesManager.LOCATION_TRACKING_INTERVAL) * 1000L // in milliseconds

        // Build a notification indicating ongoing location tracking
        val notification = NotificationCompat.Builder(this, LOCATION_CHANNEL_ID)
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Start receiving location updates
        locationClient
            .getLocationUpdates(locationInterval)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                //add location to db
                val db = AppDatabase.getInstance(this)
                db.locationDao.insertLocation(
                    Location(
                        location.longitude,
                        location.latitude,
                        System.currentTimeMillis(),
                        null,
                        false
                    )
                )
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        // Start the service in the foreground with the ongoing notification
        startForeground(1, notification.build())
    }

    /**
     * Stops the service and removes it from the foreground.
     */
    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the service's coroutine scope when the service is destroyed
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}