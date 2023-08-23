package com.example.privacyapp.feature_PrivacyDashboard.data.repositories

import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.UsageEventProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.UsageEvent

class FakeUsageEventProvider: UsageEventProvider {

    private val usageEvents = listOf(
        UsageEvent(1692662410000, 1, "A"), // App A started - Dienstag, 22. August 2023 02:00:10 GMT+02:00
        UsageEvent(1692662425000, 2, "A"), // App A paused - Dienstag, 22. August 2023 02:00:25 GMT+02:00
        UsageEvent(1692662460000, 1, "B"), // App B started - Dienstag, 22. August 2023 02:01:00 GMT+02:00
        UsageEvent(1692662475000, 23, "A"), // App A stopped - Dienstag, 22. August 2023 02:01:15 GMT+02:00
        UsageEvent(1692662478000, 1, "B"), // App B started - Dienstag, 22. August 2023 02:01:18 GMT+02:00
        UsageEvent(1692662500000, 2, "B"), // App B paused - Dienstag, 22. August 2023 02:01:40 GMT+02:00
        UsageEvent(1692662515000, 1, "B"), // App B started - Dienstag, 22. August 2023 02:01:55 GMT+02:00
        UsageEvent(1692662550000, 19, "C"), // App C foreground service started - Dienstag, 22. August 2023 02:02:30 GMT+02:00
        UsageEvent(1692662575000, 20, "C"), // App C foreground service stopped - Dienstag, 22. August 2023 02:02:55 GMT+02:00
        UsageEvent(1692662600000 - 120000, 1, "D"), // App D started - Dienstag, 22. August 2023 02:01:20 GMT+02:00
        UsageEvent(1692662625000 - 120000, 1, "E"), // App E started - Dienstag, 22. August 2023 02:01:45 GMT+02:00
        UsageEvent(1692662650000 - 120000, 1, "F"), // App F started - Dienstag, 22. August 2023 02:02:10 GMT+02:00
        UsageEvent(1692662680000 - 120000, 2, "F"), // App F paused - Dienstag, 22. August 2023 02:02:40 GMT+02:00
        UsageEvent(1692662585000, 1, "F"), // App F started - Dienstag, 22. August 2023 02:03:05 GMT+02:00

        UsageEvent(1692673200000, 1, "A"), // App F started - Dienstag, 22. August 2023 02:03:05 GMT+02:00
        UsageEvent(1692673200000, 23, "F"), // App F started - Dienstag, 22. August 2023 02:03:05 GMT+02:00
    )



    override fun getUsageEventsByInterval(
        startTimestamp: Long,
        endTimestamp: Long
    ): List<UsageEvent> {
        val reslist = mutableListOf<UsageEvent>()
        for (event in usageEvents) {
            if (event.timeStamp in startTimestamp .. endTimestamp) {
                reslist.add(event)
            }
        }
        return reslist.sortedBy { it.timeStamp }
    }

    /*fun addUsages(usageEventsList: List<UsageEvent>) {
        usageEvents.addAll(usageEventsList)
    }*/
}