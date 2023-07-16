package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1w
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval

class DeletePrivacyAssessment(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    suspend operator fun invoke(
        timestamp: Long,
        metricInterval: MetricInterval
    ) {
        return when (metricInterval) {
            MetricInterval.HOUR -> {
                privacyAssessmentRepository.deleteAssessment1hOlderThanTimestamp(timestamp)
            }

            MetricInterval.DAY -> {
                privacyAssessmentRepository.deleteAssessment1dOlderThanTimestamp(timestamp)
            }

            MetricInterval.WEEK -> {
                privacyAssessmentRepository.deleteAssessment1wOlderThanTimestamp(timestamp)
            }
        }
    }
}