package com.example.privacyapp.feature_PrivacyDashboard.domain.util

import android.app.Application

/**
 * Singleton object responsible for providing access to the global Application context.
 * This class ensures that the Application context is initialized only once and is accessible throughout the app's lifecycle.
 */
object ApplicationProvider {

    /**
     * The Application context that will be accessed globally.
     */
    @Volatile
    lateinit var application: Application

    /**
     * Initializes the Application context. This method should be called only once during the app's setup.
     *
     * @param _application The Application instance to be set as the global context.
     */
    fun initialize(_application: Application) {
        if (!::application.isInitialized) {
            synchronized(this) {
                application = _application
            }
        }
    }
}