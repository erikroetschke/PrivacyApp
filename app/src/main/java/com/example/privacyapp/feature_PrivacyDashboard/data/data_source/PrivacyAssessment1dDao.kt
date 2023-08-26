package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d

/**
 * Data Access Object (DAO) for managing PrivacyAssessment1d entities.
 */
@Dao
interface PrivacyAssessment1dDao {

    /**
     * Retrieves a list of PrivacyAssessment1d entities for a specific metric since the specified timestamp.
     */
    @Query("SELECT * FROM privacyassessment1d WHERE metricName = :metric AND timestampStart >= :timestamp")
    suspend fun getAssessmentByMetricSinceTimestamp(metric: String, timestamp: Long): List<PrivacyAssessment1d>

    /**
     * Inserts or replaces a PrivacyAssessment1d entity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(privacyAssessment1d: PrivacyAssessment1d)

    /**
     * Deletes PrivacyAssessment1d entities older than the specified timestamp.
     */
    @Query("DELETE FROM privacyassessment1d WHERE timestampStart < :timestamp")
    suspend fun deleteAssessmentOlderThanTimestamp(timestamp: Long)

    /**
     * Deletes a PrivacyAssessment1d entity.
     */
    @Delete
    suspend fun deleteAssessment(privacyAssessment1d: PrivacyAssessment1d)
}