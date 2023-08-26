package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

/**
 * Use case for adding a privacy assessment to the database.
 *
 * @param privacyAssessmentRepository The repository for managing privacy assessments.
 */
class AddPrivacyAssessment(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    /**
     * Inserts a privacy assessment into the database.
     *
     * @param metric The privacy metric associated with the assessment.
     * @param timestampStart The starting timestamp of the assessment.
     * @param metricValue The value of the privacy metric.
     */
    suspend operator fun invoke(
        metric: Metric,
        timestampStart: Long,
        metricValue: Double
    ) {
        privacyAssessmentRepository.insertAssessment1d(
            PrivacyAssessment1d(
                timestampStart,
                metric.metricName,
                metricValue
            )
        )
    }
}