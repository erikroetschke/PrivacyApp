package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

interface PrivacyAssessmentRepository {

    suspend fun deleteAssessment1d(privacyAssessment1d: PrivacyAssessment1d)


    suspend fun getAssessment1dByMetricSinceTimestamp(metric: Metric, timestamp: Long): List<PrivacyAssessment1d>

    suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d)

    suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long)


}