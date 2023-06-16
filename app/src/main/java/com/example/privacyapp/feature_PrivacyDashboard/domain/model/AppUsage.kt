package com.example.privacyapp.feature_PrivacyDashboard.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["packageName", "timestamp"],
    foreignKeys = [ForeignKey(
        entity = App::class,
        parentColumns = arrayOf("packageName"),
        childColumns = arrayOf("packageName"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class AppUsage(
    val packageName: String,
    val timestamp: Long,
    val foreground: Boolean,
    val background: Boolean
)
