package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

data class SettingsInfoDialogState(
    val coarseLocationRelevantInfoDialogVisible: Boolean = false,
    val minPOITimeInfoDialogVisible: Boolean = false,
    val locationTrackingIntervalInfoDialogVisible: Boolean = false,
    val dynamicLimitInfoDialogVisible: Boolean = false,
    val maxPOIOccurrenceInfoDialogVisible: Boolean = false,
    val pOIRadiusInfoDialogVisible : Boolean = false,
    val pOILimitInfoDialogVisible: Boolean = false
)
