package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1w

@Dao
interface PrivacyAssessment1wDao {

    @Query("SELECT * FROM privacyassessment1w WHERE metricName = :metric AND timestampStart >= :timestamp")
    suspend fun getAssessmentByMetricSinceTimestamp(metric: String, timestamp: Long): List<PrivacyAssessment1w>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(privacyAssessment1w: PrivacyAssessment1w)

    @Query("DELETE FROM privacyassessment1w WHERE timestampStart < :timestamp")
    suspend fun deleteAssessmentOlderThanTimestamp(timestamp: Long)
}