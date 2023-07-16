package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    val longitude: Double,
    val latitude: Double,
    @PrimaryKey val timestamp: Long,
    var locationUsed: Boolean?
)
