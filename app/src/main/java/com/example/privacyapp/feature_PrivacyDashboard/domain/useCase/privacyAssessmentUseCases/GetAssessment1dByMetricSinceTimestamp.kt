package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class GetAssessment1dByMetricSinceTimestamp(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    suspend operator fun invoke(metric: Metric, timestamp: Long):List<PrivacyAssessment1d> {
        return privacyAssessmentRepository.getAssessment1dByMetricSinceTimestamp(metric, timestamp)
    }
}