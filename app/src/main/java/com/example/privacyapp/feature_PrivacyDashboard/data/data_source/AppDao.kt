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

/**
 * Data Access Object (DAO) for managing operations related to the 'app' table in the database.
 */
@Dao
interface AppDao {

    /**
     * Retrieves a Flow of all apps from the 'app' table.
     *
     * @return A Flow emitting a list of all apps.
     */
    @Query("SELECT * FROM app")
    fun getApps(): Flow<List<App>>

    /**
     * Retrieves a list of all apps from the 'app' table.
     *
     * @return A list of all apps.
     */
    @Query("SELECT * FROM app")
    suspend fun getAppsSuspend(): List<App>

    /**
     * Retrieves an app by its package name.
     *
     * @param packageName The package name of the app.
     * @return The app with the specified package name, or null if not found.
     */
    @Query("SELECT * FROM app WHERE packageName = :packageName")
    suspend fun getAppByName(packageName: String): App?

    /**
     * Retrieves a Flow of favorite apps from the 'app' table.
     *
     * @return A Flow emitting a list of favorite apps.
     */
    @Query("SELECT * FROM app WHERE favorite = 1")
    fun getFavoriteApps(): Flow<List<App>>

    /**
     * Inserts or updates an app into the 'app' table.
     *
     * @param app The app to be inserted or updated.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

    /**
     * Deletes an app from the 'app' table.
     *
     * @param app The app to be deleted.
     */
    @Delete
    suspend fun deleteApp(app: App)

    /**
     * Deletes all apps from the 'app' table.
     */
    @Query("DELETE FROM app")
    suspend fun deleteAllApps()

    /**
     * Retrieves an app along with its app usage information using a package name.
     *
     * @param packageName The package name of the app.
     * @return An [AppAndAppUsage] object representing the app and its usage information.
     */
    @Transaction
    @Query("SELECT * from app WHERE packageName = :packageName")
    suspend fun getAppWithUsage(packageName: String): AppAndAppUsage
}