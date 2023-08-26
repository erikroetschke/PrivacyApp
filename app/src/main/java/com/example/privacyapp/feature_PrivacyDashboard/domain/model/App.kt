package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Data class representing an application's information.
 *
 * @property packageName The package name of the application.
 * @property appName The name of the application.
 * @property ACCESS_COARSE_LOCATION Indicates if the application has access to coarse location.
 * @property ACCESS_FINE_LOCATION Indicates if the application has access to fine location.
 * @property ACCESS_BACKGROUND_LOCATION Indicates if the application has access to background location.
 * @property numberOfEstimatedRequests The number of estimated location requests made by the application.
 * @property favorite Indicates if the application is marked as a favorite.
 * @property active Indicates if the application is active.
 */
@Entity
data class App(
    @PrimaryKey val packageName: String,
    val appName: String,
    val ACCESS_COARSE_LOCATION: Boolean,
    val ACCESS_FINE_LOCATION: Boolean,
    val ACCESS_BACKGROUND_LOCATION: Boolean,
    val numberOfEstimatedRequests: Int,
    val favorite: Boolean,
    val active: Boolean
)


