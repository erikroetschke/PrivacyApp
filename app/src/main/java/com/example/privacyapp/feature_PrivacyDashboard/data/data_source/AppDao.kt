package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppAndAppUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM app")
    suspend fun getApps(): List<App>

    @Query("SELECT * FROM app WHERE packageName = :packageName")
    suspend fun getAppByName(packageName: String): App?

    @Query("SELECT * FROM app WHERE favorite = 1")
    fun getFavoriteApps(): Flow<List<App>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

    @Delete
    suspend fun deleteApp(app: App)

    @Query("DELETE FROM app")
    suspend fun deleteAllApps()

    @Transaction
    @Query("SELECT * from app WHERE packageName = :packageName")
    suspend fun getAppWithUsage(packageName: String): List<AppAndAppUsage>
}