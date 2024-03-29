package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval

class DeletePrivacyAssessment(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    suspend operator fun invoke(
        timestamp: Long,
        metricInterval: MetricInterval
    ) {
        return when (metricInterval) {
            MetricInterval.DAY -> {
                privacyAssessmentRepository.deleteAssessment1hOlderThanTimestamp(timestamp)
            }

            MetricInterval.WEEK -> {
                privacyAssessmentRepository.deleteAssessment1dOlderThanTimestamp(timestamp)
            }

            MetricInterval.MONTH -> {
                privacyAssessmentRepository.deleteAssessment1wOlderThanTimestamp(timestamp)
            }
        }
    }
}