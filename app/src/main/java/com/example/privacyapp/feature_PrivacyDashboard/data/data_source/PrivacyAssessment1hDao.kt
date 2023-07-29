package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

@Dao
interface PrivacyAssessment1hDao {

    @Query("SELECT * FROM privacyassessment1h WHERE metricName = :metric AND timestampStart >= :timestamp")
    suspend fun getAssessmentByMetricSinceTimestamp(metric: String, timestamp: Long): List<PrivacyAssessment1h>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(privacyAssessment1h: PrivacyAssessment1h)

    @Query("DELETE FROM privacyassessment1h WHERE timestampStart < :timestamp")
    suspend fun deleteAssessmentOlderThanTimestamp(timestamp: Long)
}