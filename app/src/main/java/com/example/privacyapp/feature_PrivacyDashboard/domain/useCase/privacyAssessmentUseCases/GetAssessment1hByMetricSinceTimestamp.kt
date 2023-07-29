package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class GetAssessment1hByMetricSinceTimestamp(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    suspend operator fun invoke(metric: Metric, timestamp: Long):List<PrivacyAssessment1h> {
        return privacyAssessmentRepository.getAssessment1hByMetricSinceTimestamp(metric, timestamp)
    }
}