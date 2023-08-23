package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.usageStatsUseCases

import android.content.Context
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakeAppRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakeAppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakeLocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakeUsageEventProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.App
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.AppUsage
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.AppUsageRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.UsageEventProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ComputeUsageTest {

    private lateinit var appRepository: AppRepository
    private lateinit var usageEventProvider: UsageEventProvider
    private lateinit var locationRepository: LocationRepository
    private lateinit var appUsageRepository: AppUsageRepository

    @Before
    fun setUp() {

        ApplicationProvider.initialize(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        //clear sharedpreffs
        ApplicationProvider.application.getSharedPreferences("PrivacyApp.LatestAppStatusMap", Context.MODE_PRIVATE).edit().clear().apply()

        appRepository = FakeAppRepository()
        usageEventProvider = FakeUsageEventProvider()
        locationRepository = FakeLocationRepository()
        appUsageRepository = FakeAppUsageRepository()
        runBlocking {
            //init Apps
            appRepository.insertApp(App("A", "A", true, true, true, 0, false, true))
            appRepository.insertApp(App("B", "B", true, true, true, 0, false, false))
            appRepository.insertApp(App("C", "C", true, true, true, 0, false, true))
            appRepository.insertApp(App("D", "D", true, false, false, 0, false, true))
            appRepository.insertApp(App("E", "E", true, true, true, 0, false, true))
            appRepository.insertApp(App("F", "F", true, true, true, 0, false, true))
        }
    }

    @Test
    fun testUsageComputationFullExample() {
        runBlocking {
            //cretae locaions
            val testLocations = listOf(
                Location(12.345, 67.890, 1692662400000, false, false), // Dienstag, 22. August 2023 02:00:00 GMT+02:00
                Location(12.355, 67.900, 1692662445000, false, false), // Dienstag, 22. August 2023 02:00:45 GMT+02:00
                Location(12.365, 67.910, 1692662490000, false, false), // Dienstag, 22. August 2023 02:01:30 GMT+02:00
                Location(12.365, 67.910, 1692662535000, false, false), // Dienstag, 22. August 2023 02:02:15 GMT+02:00
                Location(12.365, 67.910, 1692662580000, false, false), // Dienstag, 22. August 2023 02:03:00 GMT+02:00
            )

            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocations)

            // Expected app usage objects for each timestamp
            val expectedAppUsages = listOf(
                AppUsage("A", 1692662400000, foreground = true, background = true),
                AppUsage("A", 1692662445000, foreground = false, background = true),
                AppUsage("E", 1692662490000, foreground = true, background = false),
                AppUsage("F", 1692662490000, foreground = true, background = false),
                AppUsage("C", 1692662535000, foreground = false, background = true),
                AppUsage("F", 1692662535000, foreground = true, background = true),
                AppUsage("E", 1692662535000, foreground = true, background = false),
                AppUsage("E", 1692662580000, foreground = true, background = false),
                AppUsage("F", 1692662580000, foreground = true, background = true),
            )

            // Retrieve actual app usage objects from the fake repository
            val actualAppUsages = appUsageRepository.getAppUsageStats()

            // Create a mapping of expected app usages using package name and timestamp as the key
            val expectedAppUsageMap = expectedAppUsages.associateBy { it.packageName + it.timestamp }

            // Perform assertions
            assertEquals(expectedAppUsages.size, actualAppUsages.size)

            for (actual in actualAppUsages) {
                val expectedKey = actual.packageName + actual.timestamp
                val expected = expectedAppUsageMap[expectedKey]

                assertNotNull(expected)
                assertEquals(expected?.packageName, actual.packageName)
                assertEquals(expected?.timestamp, actual.timestamp)
                assertEquals(expected?.foreground, actual.foreground)
                assertEquals(expected?.background, actual.background)
            }
        }
    }

    @Test
    fun testUsageComputationTwiceWithBreakInBetween() {
        val testLocations = listOf(
            Location(12.345, 67.890, 1692662400000, false, false), // Dienstag, 22. August 2023 02:00:00 GMT+02:00
            Location(12.355, 67.900, 1692662445000, false, false), // Dienstag, 22. August 2023 02:00:45 GMT+02:00
            Location(12.365, 67.910, 1692662490000, false, false), // Dienstag, 22. August 2023 02:01:30 GMT+02:00
        )

        val testLocationsSecond = listOf(
            Location(12.365, 67.910, 1692669600000, false, false), // Dienstag, 22. August 2023 04:00:00 GMT+02:00
            Location(12.365, 67.910, 1692669645000, false, false), // Dienstag, 22. August 2023 04:00:45 GMT+02:00
        )

        runBlocking {
            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocations)
            //from now on E and F should keep running
            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocationsSecond)

            // Expected app usage objects for each timestamp
            val expectedAppUsages = listOf(
                AppUsage("E", 1692669600000, foreground = true, background = false),
                AppUsage("F", 1692669600000, foreground = true, background = false),
                AppUsage("E", 1692669645000, foreground = true, background = false),
                AppUsage("F", 1692669645000, foreground = true, background = false)
            )

            // Retrieve actual app usage objects from the fake repository
            val actualAppUsages = appUsageRepository.getAppUsageStats().filter { it.timestamp >=  1692669600000}

            // Create a mapping of expected app usages using package name and timestamp as the key
            val expectedAppUsageMap = expectedAppUsages.associateBy { it.packageName + it.timestamp }

            // Perform assertions
            assertEquals(expectedAppUsages.size, actualAppUsages.size)

            for (actual in actualAppUsages) {
                val expectedKey = actual.packageName + actual.timestamp
                val expected = expectedAppUsageMap[expectedKey]

                assertNotNull(expected)
                assertEquals(expected?.packageName, actual.packageName)
                assertEquals(expected?.timestamp, actual.timestamp)
                assertEquals(expected?.foreground, actual.foreground)
                assertEquals(expected?.background, actual.background)
            }
        }
    }

    @Test
    fun testMultipleUsageComputationWithBreakAndChangesInBetween() {
        val testLocations = listOf(
            Location(12.345, 67.890, 1692662400000, false, false), // Dienstag, 22. August 2023 02:00:00 GMT+02:00
            Location(12.355, 67.900, 1692662445000, false, false), // Dienstag, 22. August 2023 02:00:45 GMT+02:00
            Location(12.365, 67.910, 1692662490000, false, false), // Dienstag, 22. August 2023 02:01:30 GMT+02:00
        )

        val testLocationsSecond = listOf(
            Location(12.365, 67.910, 1692669600000, false, false), // Dienstag, 22. August 2023 04:00:00 GMT+02:00
            Location(12.365, 67.910, 1692669645000, false, false), // Dienstag, 22. August 2023 04:00:45 GMT+02:00
        )

        val testLocationsThird = listOf(
            Location(12.365, 67.910, 1692676800000, false, false), // Dienstag, 22. August 2023 06:00:00 GMT+02:00
            Location(12.365, 67.910, 1692676845000, false, false), //Dienstag, 22. August 2023 06:00:45 GMT+02:00
        )

        runBlocking {
            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocations)
            //from now on E and F should keep running
            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocationsSecond)
            //in between A gets started and F gets stopped
            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocationsThird)

            // Expected app usage objects for each timestamp
            val expectedAppUsages = listOf(
                AppUsage("E", 1692676800000, foreground = true, background = false),
                AppUsage("A", 1692676800000, foreground = true, background = false),
                AppUsage("E", 1692676845000, foreground = true, background = false),
                AppUsage("A", 1692676845000, foreground = true, background = false)
            )

            // Retrieve actual app usage objects from the fake repository
            val actualAppUsages = appUsageRepository.getAppUsageStats().filter { it.timestamp >=  1692676800000}

            // Create a mapping of expected app usages using package name and timestamp as the key
            val expectedAppUsageMap = expectedAppUsages.associateBy { it.packageName + it.timestamp }

            // Perform assertions
            assertEquals(expectedAppUsages.size, actualAppUsages.size)

            for (actual in actualAppUsages) {
                val expectedKey = actual.packageName + actual.timestamp
                val expected = expectedAppUsageMap[expectedKey]

                assertNotNull(expected)
                assertEquals(expected?.packageName, actual.packageName)
                assertEquals(expected?.timestamp, actual.timestamp)
                assertEquals(expected?.foreground, actual.foreground)
                assertEquals(expected?.background, actual.background)
            }
        }
    }

    @Test
    fun testLocationUsedFlag() {
        runBlocking {
            val testLocations = listOf(
                Location(12.345, 67.890, 1692662400000, false, false), // Dienstag, 22. August 2023 02:00:00 GMT+02:00
                Location(12.355, 67.900, 1692662445000, false, false), // Dienstag, 22. August 2023 02:00:45 GMT+02:00
                Location(12.365, 67.910, 1692662490000, false, false), // Dienstag, 22. August 2023 02:01:30 GMT+02:00
                Location(12.365, 67.910, 1692662535000, false, false), // Dienstag, 22. August 2023 02:02:15 GMT+02:00
                Location(12.365, 67.910, 1692662580000, false, false), // Dienstag, 22. August 2023 02:03:00 GMT+02:00
                Location(12.365, 67.910, 1692662680000, false, false), // Dienstag, 22. August 2023 02:03:00 GMT+02:00
            )

            ComputeUsage(appUsageRepository, locationRepository, appRepository, usageEventProvider).invoke(testLocations)

            val locations = locationRepository.getUsedLocations(1692662400000)
            //last location is not set to locationused = true
            assertEquals(5, locations.size)
        }
    }

}