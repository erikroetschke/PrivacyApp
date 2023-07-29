package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class AppAndAppUsage(
    @Embedded val app: App,
    @Relation(
        parentColumn = "packageName",
        entityColumn = "packageName"
    )
    val appUsage: List<AppUsage> //1 to many relationship
)
