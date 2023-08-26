package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey


/**
 * Data class representing the usage of an application at a specific timestamp.
 *
 * @property packageName The package name of the application.
 * @property timestamp The timestamp representing when the usage data was recorded.
 * @property foreground Boolean indicating if the application was in the foreground during usage.
 * @property background Boolean indicating if the application was in the background during usage.
 */
@Entity(
    primaryKeys = ["packageName", "timestamp"],
    foreignKeys = [ForeignKey(
        entity = App::class,
        parentColumns = arrayOf("packageName"),
        childColumns = arrayOf("packageName"),
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class AppUsage(
    val packageName: String,
    val timestamp: Long,
    var foreground: Boolean,
    var background: Boolean
)
