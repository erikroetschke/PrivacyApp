package com.example.privacyapp.feature_PrivacyDashboard.domain.util

/**
 * Represents the order type for sorting (ascending or descending).
 */
sealed class OrderType {

    /**
     * Represents ascending order.
     */
    object Ascending: OrderType()

    /**
     * Represents descending order.
     */
    object Descending: OrderType()
}
