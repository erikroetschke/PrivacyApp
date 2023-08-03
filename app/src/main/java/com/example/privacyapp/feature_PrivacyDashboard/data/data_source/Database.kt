package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d

@Database(
    entities = [Location::class, App::class, AppUsage::class, PrivacyAssessment1d::class, POI::class],
    version = 12
)
abstract class Database: RoomDatabase() {

    abstract val locationDao: LocationDao
    abstract val appDao: AppDao
    abstract val appUsageDao: AppUsageDao
    abstract val privacyAssessment1dDao: PrivacyAssessment1dDao
    abstract val pOIDao: POIDao

    companion object {
        const val DATABASE_NAME = "privacy_db"

        //to get an instance of the DB without injection
        private var INSTANCE: com.example.privacyapp.feature_PrivacyDashboard.data.data_source.Database? = null
        fun getInstance(context: Context): com.example.privacyapp.feature_PrivacyDashboard.data.data_source.Database {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, com.example.privacyapp.feature_PrivacyDashboard.data.data_source.Database::class.java, "privacy_db").build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}