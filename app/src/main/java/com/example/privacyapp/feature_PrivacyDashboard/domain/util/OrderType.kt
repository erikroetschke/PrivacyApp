package com.example.privacyapp.feature_PrivacyDashboard.domain.util

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}
