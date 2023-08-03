package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import android.app.PendingIntent.getActivity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.room.PrimaryKey
import com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDao
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppAndAppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp

class AppRepositoryImpl(
    private val dao: AppDao
) : AppRepository {
    override fun getApps(): Flow<List<App>> {
        return dao.getApps()
    }

    override suspend fun getAppsSuspend(): List<App> {
        return dao.getAppsSuspend()
    }

    override suspend fun getAppWithUsage(packageName: String): AppAndAppUsage {
        return dao.getAppWithUsage(packageName)
    }

    override suspend fun getAppByName(packageName: String): App? {
        return dao.getAppByName(packageName)
    }

    override fun getFavoriteApps(): Flow<List<App>> {
        return dao.getFavoriteApps()
    }

    override suspend fun insertApp(app: App) {
        return dao.insertApp(app)
    }

    override suspend fun deleteApp(app: App) {
        return dao.deleteApp(app)
    }

    override suspend fun deleteAllApps() {
        return dao.deleteAllApps()
    }
}

