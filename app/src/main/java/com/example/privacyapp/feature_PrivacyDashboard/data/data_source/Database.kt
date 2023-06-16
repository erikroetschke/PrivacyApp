package com.example.privacyapp.feature_PrivacyDashboard.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location

@Database(
    entities = [Location::class, App::class],
    version = 1
)
abstract class Database: RoomDatabase() {

    abstract val locationDao: LocationDao
    abstract val appDao: AppDao

    companion object {
        const val DATABASE_NAME = "privacy_db"
    }
}