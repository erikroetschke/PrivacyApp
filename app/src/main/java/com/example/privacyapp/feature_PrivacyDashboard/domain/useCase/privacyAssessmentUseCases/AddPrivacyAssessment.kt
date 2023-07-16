package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1w
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval

class AddPrivacyAssessment(
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    /**
     * Inserts an Assessment in the DB
     */
    suspend operator fun invoke(
        metric: Metric,
        timestampStart: Long,
        metricValue: Double,
        metricInterval: MetricInterval
    ) {
        return when (metricInterval) {
            MetricInterval.HOUR -> {
                privacyAssessmentRepository.insertAssessment1h(
                    PrivacyAssessment1h(
                        timestampStart,
                        metric.metricName,
                        metric.metricDescription,
                        metricValue,
                        metric.weighting
                    )
                )
            }

            MetricInterval.DAY -> {
                privacyAssessmentRepository.insertAssessment1d(
                    PrivacyAssessment1d(
                        timestampStart,
                        metric.metricName,
                        metric.metricDescription,
                        metricValue,
                        metric.weighting
                    )
                )
            }

            MetricInterval.WEEK -> {
                privacyAssessmentRepository.insertAssessment1w(
                    PrivacyAssessment1w(
                        timestampStart,
                        metric.metricName,
                        metric.metricDescription,
                        metricValue,
                        metric.weighting
                    )
                )
            }
        }
    }
}