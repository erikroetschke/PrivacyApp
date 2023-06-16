package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class App(
    @PrimaryKey val packageName: String,
    val appName: String,
    val ACCESS_COARSE_LOCATION: Boolean,
    val ACCESS_FINE_LOCATION: Boolean,
    val ACCESS_BACKGROUND_LOCATION: Boolean,
    val estimatedLocationRequestFrequency: Int,
    val favorite: Boolean
)


