package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1w
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

interface PrivacyAssessmentRepository {

    suspend fun getAssessment1hByMetricSinceTimestamp(metric: Metric, timestamp: Long): List<PrivacyAssessment1h>

    suspend fun insertAssessment1h(privacyAssessment1h: PrivacyAssessment1h)

    suspend fun deleteAssessment1hOlderThanTimestamp(timestamp: Long)

    suspend fun getAssessment1dByMetricSinceTimestamp(metric: Metric, timestamp: Long): List<PrivacyAssessment1d>

    suspend fun insertAssessment1d(privacyAssessment1d: PrivacyAssessment1d)

    suspend fun deleteAssessment1dOlderThanTimestamp(timestamp: Long)

    suspend fun getAssessment1wByMetricSinceTimestamp(metric: Metric, timestamp: Long): List<PrivacyAssessment1w>

    suspend fun insertAssessment1w(privacyAssessment1w: PrivacyAssessment1w)

    suspend fun deleteAssessment1wOlderThanTimestamp(timestamp: Long)
}