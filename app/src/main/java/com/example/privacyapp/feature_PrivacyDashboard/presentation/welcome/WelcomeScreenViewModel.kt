package com.example.privacyapp.feature_PrivacyDashboard.presentation.welcome

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.privacyapp.feature_PrivacyDashboard.domain.location.LocationService
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUsageUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.AppUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.LocationUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.PrivacyAssessmentUseCases
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.AppOrder
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.OrderType
import com.example.privacyapp.feature_PrivacyDashboard.presentation.MainActivity
import com.example.privacyapp.feature_PrivacyDashboard.presentation.dashboard.DashboardEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Welcome Screen that guides users through permissions setup.
 */
@HiltViewModel
class WelcomeScreenViewModel @Inject constructor() : ViewModel() {

    //states
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    private var _headline = mutableStateOf("Welcome!")
    var headline = _headline

    private var _text =
        mutableStateOf("For the app to work as designed, we need you to grant the permission to use your App usage statistics.")
    var text = _text

    private var _nextButton = mutableStateOf("Next")
    var nextButton = _nextButton

    private var _actionButton = mutableStateOf("Bring me to the Settings!")
    var actionButton = _actionButton

    private var _onFirstPage = mutableStateOf(true)
    var onFirstPage = _onFirstPage

    /**
     * Handle the click event of the "Next" button on the welcome screen.
     *
     * @param welcomeActivity The WelcomeScreenActivity instance.
     * @return true if the action was handled successfully, false otherwise.
     */
    fun onNextButtonClick(welcomeActivity: WelcomeScreenActivity): Boolean {
        if (_onFirstPage.value) {
            if (checkIfUsagePermissionIsGranted()) {
                _onFirstPage.value = false
                _headline.value = "One more Thing!"
                text.value =
                    "To evaluate your location privacy score we need your access your location. " +
                            "Furthermore depending on your Android version you have to grant a permission for notifications, " +
                            "so that you can see when the tracking is active"
                nextButton.value = "Finish"
                actionButton.value = "Grant Permissions"
                welcomeActivity.getSharedPreferences("PREFS_NAME", ComponentActivity.MODE_PRIVATE)
                    .edit()
                    .putBoolean("USAGE_PERMISSION_GRANTED", true)
                    .apply()
                return true
            } else {
                return false
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                welcomeActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ) {
                welcomeActivity.getSharedPreferences("PREFS_NAME", ComponentActivity.MODE_PRIVATE)
                    .edit()
                    .putBoolean("FIRST_RUN", false)
                    .apply()
                return true
            }else {
                return false
            }
        }
    }


    /**
     * Dismiss the permission dialog.
     */
    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    /**
     * Handle the result of a permission request.
     *
     * @param permission       The permission that was requested.
     * @param welcomeActivity The WelcomeScreenActivity instance.
     */
    fun onPermissionResult(
        permission: String,
        welcomeActivity: WelcomeScreenActivity
    ) {
        var isGranted = false
        if(ContextCompat.checkSelfPermission(welcomeActivity, permission)
            == PackageManager.PERMISSION_GRANTED) {
            isGranted = true
        }

        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    /**
     * Check if the usage permission is granted.
     *
     * @return true if the usage permission is granted, false otherwise.
     */
    private fun checkIfUsagePermissionIsGranted(): Boolean {
        try {
            val packageManager: PackageManager =
                ApplicationProvider.application.applicationContext.packageManager
            val applicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(
                    ApplicationProvider.application.packageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                packageManager.getApplicationInfo(ApplicationProvider.application.packageName, 0)
            }
            val appOpsManager =
                ApplicationProvider.application.applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            } else {
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                );
            }
            return mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }
}