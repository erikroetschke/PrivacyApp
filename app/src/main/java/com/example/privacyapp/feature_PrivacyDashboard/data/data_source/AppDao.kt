package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM app")
    fun getApps(): Flow<List<App>>

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
}