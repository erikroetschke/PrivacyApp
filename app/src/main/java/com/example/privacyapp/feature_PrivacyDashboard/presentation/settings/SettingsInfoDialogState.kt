package com.example.privacyapp.feature_PrivacyDashboard.presentation.settings

/**
 * Represents the state of information dialogs related to settings in the application.
 *
 * @param coarseLocationRelevantInfoDialogVisible Whether the information dialog for coarse location relevance is visible.
 * @param minPOITimeInfoDialogVisible Whether the information dialog for minimum POI time is visible.
 * @param locationTrackingIntervalInfoDialogVisible Whether the information dialog for location tracking interval is visible.
 * @param dynamicLimitInfoDialogVisible Whether the information dialog for dynamic POI limit is visible.
 * @param maxPOIOccurrenceInfoDialogVisible Whether the information dialog for maximum POI occurrence is visible.
 * @param pOIRadiusInfoDialogVisible Whether the information dialog for POI radius is visible.
 * @param pOILimitInfoDialogVisible Whether the information dialog for POI limit is visible.
 */
data class SettingsInfoDialogState(
    val coarseLocationRelevantInfoDialogVisible: Boolean = false,
    val minPOITimeInfoDialogVisible: Boolean = false,
    val locationTrackingIntervalInfoDialogVisible: Boolean = false,
    val dynamicLimitInfoDialogVisible: Boolean = false,
    val maxPOIOccurrenceInfoDialogVisible: Boolean = false,
    val pOIRadiusInfoDialogVisible : Boolean = false,
    val pOILimitInfoDialogVisible: Boolean = false
)
