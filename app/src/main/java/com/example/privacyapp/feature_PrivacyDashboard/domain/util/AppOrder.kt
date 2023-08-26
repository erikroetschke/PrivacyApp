package com.example.privacyapp.feature_PrivacyDashboard.domain.util

/**
 * Represents the possible orders for sorting apps.
 */
sealed class AppOrder(val orderType: OrderType) {

    /**
     * Represents an order based on app title.
     * @param orderType The type of order (ascending or descending).
     */
    class Title(orderType: OrderType): AppOrder(orderType)

    /**
     * Represents an order based on location usage.
     * @param orderType The type of order (ascending or descending).
     */
    class LocationUsage(orderType: OrderType): AppOrder(orderType)

    /**
     * Creates a copy of the AppOrder with a new order type.
     * @param orderType The new order type.
     * @return A new instance of AppOrder with the specified order type.
     */
    fun copy(orderType: OrderType): AppOrder {
        return when(this) {
            is Title -> Title(orderType)
            is LocationUsage -> LocationUsage(orderType)
        }
    }
}