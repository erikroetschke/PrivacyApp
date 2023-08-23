package com.example.privacyapp.feature_PrivacyDashboard.data.repository

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.UsageEventProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.UsageEvent

/**
 * An implementation of the [UsageEventProvider] interface that retrieves usage events
 * using the Android UsageStatsManager within a specified time interval.
 *
 * @param context The application context.
 */
class UsageEventProviderImpl(context: Context) : UsageEventProvider {
    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    /**
     * Retrieves a list of [UsageEvent] objects that fall within the specified time interval.
     *
     * @param startTimestamp The start timestamp of the time interval.
     * @param endTimestamp The end timestamp of the time interval.
     * @return A list of [UsageEvent] objects representing usage events within the interval.
     * @see [UsageEvent]
     */
    override fun getUsageEventsByInterval(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<UsageEvent> {
        //get usage Stats NOTE: Events are only kept by the system for a few days.
        val usageEvents: UsageEvents? = usageStatsManager.queryEvents(
            startTimestamp,
            endTimestamp
        )
        val resList = mutableListOf<UsageEvent>()
        if (usageEvents != null) {
            val currentEvent = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(currentEvent)
                val filteredEvent = UsageEvent(
                    currentEvent.timeStamp,
                    currentEvent.eventType,
                    currentEvent.packageName
                )
                resList.add(filteredEvent)
            }
        }
        return resList
    }
}