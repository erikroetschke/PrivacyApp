package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

class AddPrivacyAssessment(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    /**
     * Inserts an Assessment in the DB
     */
    suspend operator fun invoke(
        metric: Metric,
        timestampStart: Long,
        metricValue: Double
    ) {
        return privacyAssessmentRepository.insertAssessment1d(
                    PrivacyAssessment1d(
                        timestampStart,
                        metric.metricName,
                        metricValue
                    )
                )
    }
}