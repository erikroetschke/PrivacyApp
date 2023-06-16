package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase

import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository

class DeleteAllApps(
    private val repository: AppRepository) {

    suspend operator fun invoke(){
        return repository.deleteAllApps()
    }
}