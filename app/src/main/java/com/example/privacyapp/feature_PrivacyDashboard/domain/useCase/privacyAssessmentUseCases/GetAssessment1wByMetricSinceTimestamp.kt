package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1w
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class GetAssessment1wByMetricSinceTimestamp(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    suspend operator fun invoke(metric: Metric, timestamp: Long):List<PrivacyAssessment1w> {
        return privacyAssessmentRepository.getAssessment1wByMetricSinceTimestamp(metric, timestamp)
    }
}