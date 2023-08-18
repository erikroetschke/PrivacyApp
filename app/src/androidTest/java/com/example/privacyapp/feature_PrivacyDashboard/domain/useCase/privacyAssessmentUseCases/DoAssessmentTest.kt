package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakeLocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakePOIRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakePrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class DoAssessmentTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var privacyAssessmentRepository: PrivacyAssessmentRepository
    private lateinit var poiRepository: POIRepository

    @Before
    fun setUp() {
        locationRepository = FakeLocationRepository()
        privacyAssessmentRepository = FakePrivacyAssessmentRepository()
        poiRepository = FakePOIRepository()
    }

    /*@Test
    fun testPOIDetectionLast24H(){
        runBlocking {
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *3), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.5).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *1), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 30), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis(), true, false))

            val result = DoAssessment(locationRepository, privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.DAY, MetricType.ABSOLUT)
            assertEquals(1.toDouble(), result[22].second, 0.1)
            assertEquals(1.toDouble(), result[21].second, 0.1)
        }
    }*/
}