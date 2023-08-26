package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a Point of Interest (POI) with its coordinates and timestamp.
 *
 * @property longitude The longitude coordinate of the POI.
 * @property latitude The latitude coordinate of the POI.
 * @property timestamp The timestamp representing when the POI was recorded.
 */
@Entity
data class POI(
    val longitude: Double,
    val latitude: Double,
    @PrimaryKey val timestamp: Long
)
