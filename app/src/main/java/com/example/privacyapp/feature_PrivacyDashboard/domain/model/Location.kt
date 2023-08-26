package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Data class representing a location point with its associated details.
 *
 * @property longitude The longitude coordinate of the location.
 * @property latitude The latitude coordinate of the location.
 * @property timestamp The timestamp representing when the location was recorded.
 * @property locationUsed Boolean indicating if the location was used.
 * @property processed Boolean indicating if the location has been processed.
 */
@Entity
data class Location(
    val longitude: Double,
    val latitude: Double,
    @PrimaryKey val timestamp: Long,
    var locationUsed: Boolean?,
    @ColumnInfo(name = "processed", defaultValue = "0")var processed: Boolean
)
