package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.example.privacyapp.feature_PrivacyDashboard.data.util.hasLocationPermission
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * Default implementation of the [LocationClient] interface, providing location updates using
 * the FusedLocationProviderClient from Google Play Services.
 *
 * @param context The application context.
 * @param client The FusedLocationProviderClient instance.
 */
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    /**
     * Requests location updates at the specified interval.
     *
     * @param interval The interval in milliseconds between location updates.
     * @return A Flow emitting the received Location updates.
     * @throws LocationClient.LocationException if location permission is missing or GPS is disabled.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if(!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            // Build the location request
            val request = LocationRequest.Builder(interval).build()

            // Define the location callback
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }

            // Request location updates
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            // Remove location updates when the Flow is closed
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}