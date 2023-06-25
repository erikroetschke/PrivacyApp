package com.example.privacyapp.feature_PrivacyDashboard.domain.util

import android.app.Application

object ApplicationProvider {

    @Volatile
    lateinit var application: Application

    fun initialize(_application: Application) {
        if (!::application.isInitialized) {
            synchronized(this) {
                application = _application
            }
        }
    }
}