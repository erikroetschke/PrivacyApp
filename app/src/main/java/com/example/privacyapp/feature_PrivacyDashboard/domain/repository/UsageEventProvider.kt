package com.example.privacyapp.feature_PrivacyDashboard.domain.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.UsageEvent

/**
 * A provider interface for retrieving usage events within a specified time interval.
 */
interface UsageEventProvider {

    /**
     * Retrieves a list of [UsageEvent] objects that fall within the specified time interval.
     *
     * @param startTimestamp The start timestamp of the time interval.
     * @param endTimestamp The end timestamp of the time interval.
     * @return A list of [UsageEvent] objects representing usage events within the interval.
     * @see [UsageEvent]
     */
    fun getUsageEventsByInterval(startTimestamp: Long, endTimestamp: Long): List<UsageEvent>
}