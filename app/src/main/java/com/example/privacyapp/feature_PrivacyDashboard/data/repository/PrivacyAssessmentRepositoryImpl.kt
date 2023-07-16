package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.PrivacyAssessment1dDao
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.PrivacyAssessment1hDao
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.PrivacyAssessment1wDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1w
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class PrivacyAssessmentRepositoryImpl(
    private val privacyAssessment1hDao: PrivacyAssessment1hDao,
    private val privacyAssessment1dDao: PrivacyAssessment1dDao,
    private val privacyAssessment1wDao: PrivacyAssessment1wDao
): PrivacyAssessmentRepository {
    override suspend fun getAssessment1hByMetricSinceTimestamp(
        metric: Metric,
        timestamp: Long
    ): List<PrivacyAssessment1h> {
        return privacyAssessment1hDao.getAssessmentByMetricSinceTimestamp(metric.metricName, timestamp)
    }

    override suspend fun getAssessment1dByMetricSinceTimestamp(
        metric: Metric,
        timestamp: Long
    ): List<PrivacyAssessment1d> {
        return privacyAssessment1dDao.getAssessmentByMetricSinceTimestamp(metric.metricName, timestamp)
    }

    override suspend fun getAssessment1wByMetricSinceTimestamp(
        metric: Metric,
        timestamp: Long
    ): List<PrivacyAssessment1w> {
        return privacyAssessment1wDao.getAssessmentByMetricSinceTimestamp(metric.metricName, timestamp)
    }

    override suspend fun insertAssessment1h(privacyAssessment1h: PrivacyAssessment1h) {
        return privacyAssessment1hDao.insertAssessment(privacyAssessment1h = privacyAssessment1h)
    }

    override suspend fun deleteAssessment1hOlderThanTimestamp(timestamp: Long) {
        return privacyAssessment1hDao.deleteAssessmentOlderThanTimestamp(timestamp)
    }

    override suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        return privacyAssessment1dDao.insertAssessment(privacyAssessment1d = privacyAssessment1d)
    }

    override suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long) {
        return privacyAssessment1dDao.deleteAssessmentOlderThanTimestamp(timestamp)
    }

    override suspend fun insertAssessment1w(privacyAssessment1w: PrivacyAssessment1w) {
        return privacyAssessment1wDao.insertAssessment(privacyAssessment1w = privacyAssessment1w)
    }

    override suspend fun deleteAssessment1wOlderThanTimestamp(timestamp: Long) {
        return privacyAssessment1wDao.deleteAssessmentOlderThanTimestamp(timestamp)
    }
}