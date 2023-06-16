package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    fun getApps(): Flow<List<App>>

    suspend fun getAppByName(packageName: String): App?

    fun getFavoriteApps(): Flow<List<App>>

    suspend fun insertApp(app: App)

    suspend fun deleteApp(app: App)

    suspend fun deleteAllApps()
}