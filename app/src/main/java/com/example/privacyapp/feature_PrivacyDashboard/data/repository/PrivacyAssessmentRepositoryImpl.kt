package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.PrivacyAssessment1dDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class PrivacyAssessmentRepositoryImpl(
    private val privacyAssessment1dDao: PrivacyAssessment1dDao,
): PrivacyAssessmentRepository {


    override suspend fun deleteAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        return privacyAssessment1dDao.deleteAssessment(privacyAssessment1d)
    }

    override suspend fun getAssessment1dByMetricSinceTimestamp(
        metric: Metric,
        timestamp: Long
    ): List<PrivacyAssessment1d> {
        return privacyAssessment1dDao.getAssessmentByMetricSinceTimestamp(metric.metricName, timestamp)
    }

    override suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        return privacyAssessment1dDao.insertAssessment(privacyAssessment1d = privacyAssessment1d)
    }

    override suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long) {
        return privacyAssessment1dDao.deleteAssessmentOlderThanTimestamp(timestamp)
    }
}