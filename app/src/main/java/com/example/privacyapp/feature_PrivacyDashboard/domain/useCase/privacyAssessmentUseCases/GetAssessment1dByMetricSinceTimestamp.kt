package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

/**
 * The `GetAssessment1dByMetricSinceTimestamp` class encapsulates the use case of retrieving privacy assessments
 * by metric and since a specified timestamp.
 *
 * @param privacyAssessmentRepository Repository for privacy assessment data.
 */
class GetAssessment1dByMetricSinceTimestamp(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    /**
     * Retrieves privacy assessments by the specified metric and since the provided timestamp.
     *
     * @param metric The privacy metric for which assessments are to be retrieved.
     * @param timestamp The timestamp since which assessments should be retrieved.
     * @return A list of PrivacyAssessment1d objects corresponding to the specified metric and timestamp.
     */
    suspend operator fun invoke(metric: Metric, timestamp: Long): List<PrivacyAssessment1d> {
        return privacyAssessmentRepository.getAssessment1dByMetricSinceTimestamp(metric, timestamp)
    }
}
