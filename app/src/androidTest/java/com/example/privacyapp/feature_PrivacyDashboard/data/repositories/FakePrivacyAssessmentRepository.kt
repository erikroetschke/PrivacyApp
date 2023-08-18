package com.example.privacyapp.feature_PrivacyDashboard.data.repositories

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class FakePrivacyAssessmentRepository: PrivacyAssessmentRepository {

    private val privacyAssessments = mutableListOf<PrivacyAssessment1d>()

    override suspend fun deleteAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        privacyAssessments.remove(privacyAssessment1d)
    }

    override suspend fun getAssessment1dByMetricSinceTimestamp(
        metric: Metric,
        timestamp: Long
    ): List<PrivacyAssessment1d> {
        val list = mutableListOf<PrivacyAssessment1d>()
        for (assessment in privacyAssessments) {
            if(assessment.timestampStart >= timestamp && assessment.metricName == metric.metricName) {
                list.add(assessment)
            }
        }
        return list

    }

    override suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d) {
        privacyAssessments.add(privacyAssessment1d)
    }

    override suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long) {
        //not needed for test cases
    }
}