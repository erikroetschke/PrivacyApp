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

/**
 * Implementation of the AppRepository interface that interacts with the AppDao.
 *
 * @param dao The AppDao used for data operations.
 */
class AppRepositoryImpl(private val dao: AppDao) : AppRepository {

    /**
     * Retrieves a flow of all apps.
     */
    override fun getApps(): Flow<List<App>> {
        return dao.getApps()
    }

    /**
     * Retrieves a list of all apps synchronously.
     */
    override suspend fun getAppsSuspend(): List<App> {
        return dao.getAppsSuspend()
    }

    /**
     * Retrieves an AppAndAppUsage entity for a specific app by package name.
     *
     * @param packageName The package name of the app.
     */
    override suspend fun getAppWithUsage(packageName: String): AppAndAppUsage {
        return dao.getAppWithUsage(packageName)
    }

    /**
     * Retrieves an app entity for a specific package name.
     *
     * @param packageName The package name of the app.
     */
    override suspend fun getAppByName(packageName: String): App? {
        return dao.getAppByName(packageName)
    }

    /**
     * Retrieves a flow of favorite apps.
     */
    override fun getFavoriteApps(): Flow<List<App>> {
        return dao.getFavoriteApps()
    }

    /**
     * Inserts an app entity into the database.
     *
     * @param app The app entity to be inserted.
     */
    override suspend fun insertApp(app: App) {
        dao.insertApp(app)
    }

    /**
     * Deletes an app entity from the database.
     *
     * @param app The app entity to be deleted.
     */
    override suspend fun deleteApp(app: App) {
        dao.deleteApp(app)
    }

    /**
     * Deletes all app entities from the database.
     */
    override suspend fun deleteAllApps() {
        dao.deleteAllApps()
    }
}


