package com.example.privacyapp.feature_PrivacyDashboard.domain.util

sealed class AppOrder(val orderType: OrderType) {
    class Title(orderType: OrderType): AppOrder(orderType)
    class LocationUsage(orderType: OrderType): AppOrder(orderType)

    fun copy(orderType: OrderType): AppOrder {
        return when(this) {
            is Title -> Title(orderType)
            is LocationUsage -> LocationUsage(orderType)
        }
    }
}