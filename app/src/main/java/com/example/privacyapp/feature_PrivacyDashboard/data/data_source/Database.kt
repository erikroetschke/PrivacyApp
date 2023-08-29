package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d

/**
 * Room database for managing various data entities.
 */
@Database(
    entities = [Location::class, App::class, AppUsage::class, PrivacyAssessment1d::class, POI::class],
    version = 15,
    autoMigrations = [
        AutoMigration (
            from = 13,
            to = 14,
            spec = AppDatabase.Migration13t014::class
                ),
    AutoMigration (
        from = 14,
        to = 15
            )
    ]
)
abstract class AppDatabase: RoomDatabase() {

    /**
     * Returns an instance of LocationDao for accessing location-related data.
     */
    abstract val locationDao: LocationDao

    /**
     * Returns an instance of AppDao for accessing app-related data.
     */
    abstract val appDao: AppDao

    /**
     * Returns an instance of AppUsageDao for accessing app usage-related data.
     */
    abstract val appUsageDao: AppUsageDao

    /**
     * Returns an instance of PrivacyAssessment1dDao for accessing privacy assessment data.
     */
    abstract val privacyAssessment1dDao: PrivacyAssessment1dDao

    /**
     * Returns an instance of POIDao for accessing point of interest data.
     */
    abstract val pOIDao: POIDao

    /**
     * Custom AutoMigration specification to handle migration from version 13 to 14.
     */
    @DeleteColumn(tableName = "PrivacyAssessment1d", columnName = "weighting")
    @DeleteColumn(tableName = "PrivacyAssessment1d", columnName = "metricDescription")
    @DeleteColumn(tableName = "POI", columnName = "count")
    class Migration13t014 : AutoMigrationSpec

    companion object {
        const val DATABASE_NAME = "privacy_db"

        //to get an instance of the DB without injection
        private var INSTANCE: com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase? = null

        /**
         * Returns an instance of the AppDatabase.
         */
        fun getInstance(context: Context): com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, com.example.privacyapp.feature_PrivacyDashboard.data.data_source.AppDatabase::class.java, "privacy_db").build()
            }
            return INSTANCE!!
        }

        val MIGRATION_14_15: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No explicit changes needed, Room will handle the foreign key constraint change
            }
        }

        /**
         * Destroys the existing instance of the AppDatabase.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }

}