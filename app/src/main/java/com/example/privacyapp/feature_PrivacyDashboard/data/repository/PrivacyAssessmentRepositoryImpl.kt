package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.PrivacyAssessment1dDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

/**
 * Implementation of the PrivacyAssessmentRepository interface that interacts with the PrivacyAssessment1dDao.
 *
 * @param privacyAssessment1dDao The PrivacyAssessment1dDao used for data operations.
 */
class PrivacyAssessmentRepositoryImpl(
    private val privacyAssessment1dDao: PrivacyAssessment1dDao
) : PrivacyAssessmentRepository {

    /**
     * Deletes a PrivacyAssessment1d from the database.
     *
     * @param privacyAssessment1d The PrivacyAssessment1d to be deleted.
     */
    override suspend fun deleteAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        privacyAssessment1dDao.deleteAssessment(privacyAssessment1d)
    }

    /**
     * Retrieves a list of PrivacyAssessment1d by metric since a specific timestamp.
     *
     * @param metric The metric.
     * @param timestamp The timestamp.
     */
    override suspend fun getAssessment1dByMetricSinceTimestamp(
        metric: Metric,
        timestamp: Long
    ): List<PrivacyAssessment1d> {
        return privacyAssessment1dDao.getAssessmentByMetricSinceTimestamp(metric.metricName, timestamp)
    }

    /**
     * Inserts a PrivacyAssessment1d into the database.
     *
     * @param privacyAssessment1d The PrivacyAssessment1d to be inserted.
     */
    override suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        privacyAssessment1dDao.insertAssessment(privacyAssessment1d = privacyAssessment1d)
    }

    /**
     * Deletes PrivacyAssessment1d records older than a specific timestamp.
     *
     * @param timestamp The timestamp.
     */
    override suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long) {
        privacyAssessment1dDao.deleteAssessmentOlderThanTimestamp(timestamp)
    }
}
