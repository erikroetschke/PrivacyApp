package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d

@Dao
interface PrivacyAssessment1dDao {

    @Query("SELECT * FROM privacyassessment1d WHERE metricName = :metric AND timestampStart >= :timestamp")
    suspend fun getAssessmentByMetricSinceTimestamp(metric: String, timestamp: Long): List<PrivacyAssessment1d>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(privacyAssessment1d: PrivacyAssessment1d)

    @Query("DELETE FROM privacyassessment1d WHERE timestampStart < :timestamp")
    suspend fun deleteAssessmentOlderThanTimestamp(timestamp: Long)

    @Delete
    suspend fun deleteAssessment(privacyAssessment1d: PrivacyAssessment1d)
}