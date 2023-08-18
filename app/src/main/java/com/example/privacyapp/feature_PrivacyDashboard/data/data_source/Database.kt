package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d

@Database(
    entities = [Location::class, App::class, AppUsage::class, PrivacyAssessment1d::class, POI::class],
    version = 14,
    autoMigrations = [
        AutoMigration (
            from = 13,
            to = 14,
            spec = AppDatabase.Migration13t014::class
                )
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract val locationDao: LocationDao
    abstract val appDao: AppDao
    abstract val appUsageDao: AppUsageDao
    abstract val privacyAssessment1dDao: PrivacyAssessment1dDao
    abstract val pOIDao: POIDao

    @DeleteColumn(tableName = "PrivacyAssessment1d", columnName = "weighting")
    @DeleteColumn(tableName = "PrivacyAssessment1d", columnName = "metricDescription")
    @DeleteColumn(tableName = "POI", columnName = "count")
    class Migration13t014 : AutoMigrationSpec

    companion object {
        const val DATABASE_NAME = "privacy_db"

        //to get an instance of the DB without injection
        private var INSTANCE: com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase? = null
        fun getInstance(context: Context): com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase::class.java, "privacy_db").build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}