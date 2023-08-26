package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class representing a combination of an application and its associated usage data.
 *
 * @property app The [App] instance representing the application.
 * @property appUsage List of [AppUsage] instances representing the usage data associated with the application.
 */
data class AppAndAppUsage(
    @Embedded val app: App,
    @Relation(
        parentColumn = "packageName",
        entityColumn = "packageName"
    )
    val appUsage: List<AppUsage> //1 to many relationship
)
