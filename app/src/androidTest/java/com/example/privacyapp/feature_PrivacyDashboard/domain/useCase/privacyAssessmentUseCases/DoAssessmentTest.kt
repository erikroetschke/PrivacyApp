package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStats
import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakeLocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakePOIRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repositories.FakePrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

/**Implements Tests for DoAssessment, but also for UpdatePOIs
 *
 */
class DoAssessmentTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var privacyAssessmentRepository: PrivacyAssessmentRepository
    private lateinit var poiRepository: POIRepository
    private lateinit var sharedPrefs:PreferencesManager

    val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()


    @Before
    fun setUp() {
        locationRepository = FakeLocationRepository()
        privacyAssessmentRepository = FakePrivacyAssessmentRepository()
        poiRepository = FakePOIRepository()

        ApplicationProvider.initialize(androidx.test.core.app.ApplicationProvider.getApplicationContext())
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        //start python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context));
        }
         sharedPrefs = PreferencesManagerImpl(ApplicationProvider.application)


    }

    @Test
    fun testPOIDetectionLast24H(){
        runBlocking {
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *3).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.7).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.4).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.1).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*2).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.9).toLong(), true, false))

            UpdatePOIs(poiRepository, locationRepository).invoke()

            val result = DoAssessment(privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.DAY, MetricType.ABSOLUT)
            assertEquals(2.toDouble(), result[20].second, 0.1)
            assertEquals(0.toDouble(), result[21].second, 0.1)
            assertEquals(timeDate.hour, result.last().first)
            val resultScore = DoAssessment(privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.DAY, MetricType.SCORE)
            assertEquals(2/(sharedPrefs.getSettingInt(PreferencesManager.POI_LIMIT)).toDouble(), resultScore[20].second, 0.1)
            assertEquals(2/(sharedPrefs.getSettingInt(PreferencesManager.POI_LIMIT)).toDouble(), resultScore[21].second, 0.1)
            assertEquals(timeDate.hour, resultScore.last().first)
        }
    }

    @Test
    fun testPOIDetectionLast7dAndMonth(){
        runBlocking {

            val hour = timeDate.hour

            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 12)), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 11.7)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 11.3)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.9)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.8)).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.4)).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.1)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10)).toLong(), true, false))


            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *3).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.7).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.4).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.1).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*2).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.9).toLong(), true, false))

            UpdatePOIs(poiRepository, locationRepository).invoke()

            //week absolut
            val result = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.WEEK, MetricType.ABSOLUT)
            assertEquals(2.toDouble(), result[6].second, 0.1)
            assertEquals(3.toDouble(), result[5].second, 0.1)
            assertEquals(timeDate.dayOfMonth, result[6].first)

            //month absolut
            val resultWeek = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.MONTH, MetricType.ABSOLUT)
            assertEquals(2.toDouble(), resultWeek.last().second, 0.1)
            assertEquals(3.toDouble(), resultWeek[resultWeek.size - 2].second, 0.1)
            assertEquals(timeDate.dayOfMonth, resultWeek.last().first)

            //Week + score + dynmic limit
            val resultWeekScore = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.WEEK, MetricType.SCORE)
            assertEquals(5/(6*resultWeekScore.size.toDouble()), resultWeekScore[6].second, 0.01)
            assertEquals(3/(6*resultWeekScore.size.toDouble()), resultWeekScore[5].second, 0.01)

            //Month Score + dynmic limit
            val resultMonthScore = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.MONTH, MetricType.SCORE)
            assertEquals(5/(6*resultMonthScore.size.toDouble()), resultMonthScore.last().second, 0.01)
            assertEquals(3/(6*resultMonthScore.size.toDouble()), resultMonthScore[resultWeek.size - 2].second, 0.01)

            sharedPrefs.setSettingBool(PreferencesManager.DYNAMIC_LIMIT, false)

            //Week + score + static limit
            val resultWeekScoreStatic = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.WEEK, MetricType.SCORE)
            assertEquals(5/(6.toDouble()), resultWeekScoreStatic[6].second, 0.01)
            assertEquals(3/(6.toDouble()), resultWeekScoreStatic[5].second, 0.01)

            //Month Score + static limit
            val resultMonthScoreStatic = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopDetection, MetricInterval.MONTH, MetricType.SCORE)
            assertEquals(5/(6.toDouble()), resultMonthScoreStatic.last().second, 0.01)
            assertEquals(3/(6.toDouble()), resultMonthScoreStatic[resultWeek.size - 2].second, 0.01)

        }
    }

    @Test
    fun testPoiFrequencyDayInterval(){
        runBlocking {
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *3).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.7).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.4).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.1).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*2).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.9).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.8).toLong(), true, false))
            locationRepository.insertLocation(Location(49.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.7).toLong(), true, false))

            UpdatePOIs(poiRepository, locationRepository).invoke()
            sharedPrefs.setSettingInt(PreferencesManager.MAX_POI_OCCURRENCE, 2)

            val result = DoAssessment(privacyAssessmentRepository, poiRepository).invoke(Metric.StopFrequency, MetricInterval.DAY, MetricType.ABSOLUT)
            assertEquals(1.toDouble(), result[21].second, 0.1)
            assertEquals(0.toDouble(), result[20].second, 0.1)
            assertEquals(timeDate.hour, result.last().first)
            val resultScore = DoAssessment(privacyAssessmentRepository, poiRepository).invoke(Metric.StopFrequency, MetricInterval.DAY, MetricType.SCORE)
            assertEquals(1/(3/2).toDouble(), resultScore[21].second, 0.01)
            assertEquals(0/(3/2).toDouble(), resultScore[20].second, 0.01)
            assertEquals(timeDate.hour, resultScore.last().first)
        }
    }

    @Test
    fun testPOIFrequencyWeekAndMonth(){
        runBlocking {

            val hour = timeDate.hour
            //1 day ago, POI(52.0/13.0)  occurs 2 times
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 12)), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 11.7)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 11.3)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.9)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.8)).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.4)).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10.1)).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 * (hour + 10)).toLong(), true, false))

            //current day POI(51.0/13.0) occurs the second time , POI(52.0/13.0) already triggered last day, other POI occurred for the first time
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *3).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.7).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.4).toLong(), true, false))
            locationRepository.insertLocation(Location(51.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60 *2.1).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*2).toLong(), true, false))
            locationRepository.insertLocation(Location(52.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.9).toLong(), true, false))
            locationRepository.insertLocation(Location(19.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.8).toLong(), true, false))
            locationRepository.insertLocation(Location(19.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.88).toLong(), true, false))
            locationRepository.insertLocation(Location(15.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.7).toLong(), true, false))
            locationRepository.insertLocation(Location(15.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.78).toLong(), true, false))
            locationRepository.insertLocation(Location(11.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.6).toLong(), true, false))
            locationRepository.insertLocation(Location(11.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.68).toLong(), true, false))
            locationRepository.insertLocation(Location(10.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.5).toLong(), true, false))
            locationRepository.insertLocation(Location(10.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.58).toLong(), true, false))
            locationRepository.insertLocation(Location(16.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.4).toLong(), true, false))
            locationRepository.insertLocation(Location(16.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.48).toLong(), true, false))
            locationRepository.insertLocation(Location(2.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.3).toLong(), true, false))
            locationRepository.insertLocation(Location(2.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.38).toLong(), true, false))
            locationRepository.insertLocation(Location(1.0, 13.0, System.currentTimeMillis() - (1000 * 60 * 60*1.2).toLong(), true, false))

            UpdatePOIs(poiRepository, locationRepository).invoke()
            sharedPrefs.setSettingInt(PreferencesManager.MAX_POI_OCCURRENCE, 2)

            //week absolut
            val result = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopFrequency, MetricInterval.WEEK, MetricType.ABSOLUT)
            assertEquals(1.toDouble(), result[6].second, 0.1)
            assertEquals(1.toDouble(), result[5].second, 0.1)
            assertEquals(timeDate.dayOfMonth, result[6].first)

            //month absolut
            val resultWeek = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopFrequency, MetricInterval.MONTH, MetricType.ABSOLUT)
            assertEquals(1.toDouble(), resultWeek.last().second, 0.1)
            assertEquals(1.toDouble(), resultWeek[resultWeek.size - 2].second, 0.1)
            assertEquals(timeDate.dayOfMonth, resultWeek.last().first)

            //Week + score
            val resultWeekScore = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopFrequency, MetricInterval.WEEK, MetricType.SCORE)
            assertEquals(2/(4.toDouble()), resultWeekScore[6].second, 0.01)
            assertEquals(1/(4.toDouble()), resultWeekScore[5].second, 0.01)

            //Month + Score
            val resultMonthScore = DoAssessment( privacyAssessmentRepository, poiRepository).invoke(Metric.StopFrequency, MetricInterval.MONTH, MetricType.SCORE)
            assertEquals(2/(4.toDouble()), resultMonthScore.last().second, 0.01)
            assertEquals(1/(4.toDouble()), resultMonthScore[resultWeek.size - 2].second, 0.01)

        }
    }
}