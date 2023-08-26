package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppAndAppUsage
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    /**
     * Retrieves a flow of all apps.
     */
    fun getApps(): Flow<List<App>>

    /**
     * Retrieves a list of all apps in a suspend function.
     */
    suspend fun getAppsSuspend(): List<App>

    /**
     * Retrieves an app by its package name.
     *
     * @param packageName The package name of the app to retrieve.
     * @return The retrieved app, or null if not found.
     */
    suspend fun getAppByName(packageName: String): App?

    /**
     * Retrieves a flow of favorite apps.
     */
    fun getFavoriteApps(): Flow<List<App>>

    /**
     * Inserts an app into the repository.
     *
     * @param app The app to insert.
     */
    suspend fun insertApp(app: App)

    /**
     * Deletes an app from the repository.
     *
     * @param app The app to delete.
     */
    suspend fun deleteApp(app: App)

    /**
     * Deletes all apps from the repository.
     */
    suspend fun deleteAllApps()

    /**
     * Retrieves an app along with its app usage information.
     *
     * @param packageName The package name of the app to retrieve.
     * @return The retrieved app along with its usage information.
     */
    suspend fun getAppWithUsage(packageName: String): AppAndAppUsage
}