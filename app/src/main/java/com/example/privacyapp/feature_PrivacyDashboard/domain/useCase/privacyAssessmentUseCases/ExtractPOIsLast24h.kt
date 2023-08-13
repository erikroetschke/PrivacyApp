package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.ExtractPOIs
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import java.time.Instant
import java.time.ZoneId

class ExtractPOIsLast24h(private val locationRepository: LocationRepository,
private val poiRepository: POIRepository) {

    suspend operator fun invoke(): List<POI> {

        val currentTime = System.currentTimeMillis()
        val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val minutesToCompleteHour = (60 - timeDate.minute) * 60 * 1000
        val secondsFromCompleteMinute = timeDate.second * 1000

        //start Timestamp for the Interval current time - 24h + minutesToCompleteHour - seconds
        val timestampStart = currentTime - (1000 * 60 * 60 * 24) + minutesToCompleteHour - secondsFromCompleteMinute

        val locations = locationRepository.getUsedLocations(timestampStart)

        //prepare route
        //get python instance
        val py = Python.getInstance()
        val routeCreation = py.getModule("routeCreation")

        //convert list of locations into python points
        val pyPoints: MutableList<PyObject> = mutableListOf()
        for (point in locations) {
            pyPoints.add(
                routeCreation.callAttr(
                    "create_Point",
                    point.latitude,
                    point.longitude,
                    point.timestamp
                )
            )
        }

        //extract timestamps from locations
        val timestamps = locations.map { location -> location.timestamp }

        //create route from points
         val pyRoute = routeCreation.callAttr(
            "create_Route",
            pyPoints.toTypedArray(),
            timestamps.toTypedArray()
        )

        return ExtractPOIs(poiRepository).invoke(pyRoute)
    }
}