package com.example.privacyapp.feature_PrivacyDashboard.data.repositories

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppAndAppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

class FakeAppRepository: AppRepository {

    private val apps = mutableListOf<App>()

    override fun getApps(): Flow<List<App>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAppsSuspend(): List<App> {
        return apps
    }

    override suspend fun getAppByName(packageName: String): App? {
        for (app in apps) {
            if(app.packageName == packageName) {
                return app
            }
        }
        return null
    }

    override fun getFavoriteApps(): Flow<List<App>> {
        TODO()
    }

    override suspend fun insertApp(app: App) {
        apps.add(app)
    }

    override suspend fun deleteApp(app: App) {
        apps.remove(app)
    }

    override suspend fun deleteAllApps() {
        apps.clear()
    }

    override suspend fun getAppWithUsage(packageName: String): AppAndAppUsage {
        TODO("Not yet implemented")
    }

}