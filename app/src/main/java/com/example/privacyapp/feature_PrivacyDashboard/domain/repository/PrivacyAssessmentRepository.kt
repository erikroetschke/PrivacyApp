package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

/**
 * An interface for managing privacy assessments.
 */
interface PrivacyAssessmentRepository {

    /**
     * Deletes a 1-day privacy assessment entry.
     *
     * @param privacyAssessment1d The privacy assessment entry to be deleted.
     */
    suspend fun deleteAssessment1d(privacyAssessment1d: PrivacyAssessment1d)

    /**
     * Retrieves 1-day privacy assessment entries by metric since a specified timestamp.
     *
     * @param metric The metric for which to retrieve privacy assessments.
     * @param timestamp The starting timestamp for retrieval.
     * @return A list of privacy assessment entries.
     */
    suspend fun getAssessment1dByMetricSinceTimestamp(metric: Metric, timestamp: Long): List<PrivacyAssessment1d>

    /**
     * Inserts a 1-day privacy assessment entry.
     *
     * @param privacyAssessment1d The privacy assessment entry to be inserted.
     */
    suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d)

    /**
     * Deletes 1-day privacy assessment entries older than a specified timestamp.
     *
     * @param timestamp The threshold timestamp.
     */
    suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long)
}
