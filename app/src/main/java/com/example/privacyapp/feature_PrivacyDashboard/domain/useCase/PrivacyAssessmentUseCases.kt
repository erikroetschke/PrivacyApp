package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.AddPrivacyAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.DeletePrivacyAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.DoAssessment
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.ExtractPOIsLast24h
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.GetAssessment1dByMetricSinceTimestamp
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.CallMetric

data class PrivacyAssessmentUseCases(
    val callMetric: CallMetric,
    val addPrivacyAssessment: AddPrivacyAssessment,
    val deletePrivacyAssessment: DeletePrivacyAssessment,
    val getAssessment1dByMetricSinceTimestamp: GetAssessment1dByMetricSinceTimestamp,
    val doAssessment: DoAssessment,
    val extractPOIsLast24h: ExtractPOIsLast24h
)
