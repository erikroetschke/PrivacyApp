package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.AddPrivacyAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.DeletePrivacyAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.DoAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.ExtractPOIsLast24h
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetAssessment1dByMetricSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetPOISinceTimestampAsFlow
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.RecomputePOIs
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.UpdatePOIs

/**
 * Encapsulates the use cases related to privacy assessments.
 *
 * @see AddPrivacyAssessment
 * @see DeletePrivacyAssessment
 * @see GetAssessment1dByMetricSinceTimestamp
 * @see DoAssessment
 * @see ExtractPOIsLast24h
 * @see GetPOISinceTimestampAsFlow
 * @see UpdatePOIs
 * @see RecomputePOIs
 */
data class PrivacyAssessmentUseCases(
    val addPrivacyAssessment: AddPrivacyAssessment,
    val deletePrivacyAssessment: DeletePrivacyAssessment,
    val getAssessment1dByMetricSinceTimestamp: GetAssessment1dByMetricSinceTimestamp,
    val doAssessment: DoAssessment,
    val extractPOIsLast24h: ExtractPOIsLast24h,
    val getPOISinceTimestampAsFlow: GetPOISinceTimestampAsFlow,
    val updatePOIs: UpdatePOIs,
    val recomputePOIs: RecomputePOIs
)
