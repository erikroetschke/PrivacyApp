package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval

/**
 * Use case for deleting privacy assessments older than a certain timestamp from the database.
 *
 * @param privacyAssessmentRepository The repository for managing privacy assessments.
 */
class DeletePrivacyAssessment(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    /**
     * Deletes privacy assessments older than the given timestamp from the database.
     *
     * @param timestamp The timestamp indicating the threshold for deleting assessments.
     */
    suspend operator fun invoke(
        timestamp: Long
    ) {
        privacyAssessmentRepository.deleteAssessment1dOlderThanTimestamp(timestamp)
    }
}
