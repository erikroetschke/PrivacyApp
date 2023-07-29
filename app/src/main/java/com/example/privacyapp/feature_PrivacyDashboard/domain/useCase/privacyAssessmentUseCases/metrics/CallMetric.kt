package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric

/**
 * Wrapper class to call all metrics
 */
class CallMetric() {

    operator fun invoke(locations: List<Location>, metric: Metric):List<Pair<Long, Double>> {

        if (locations.isEmpty()) {
            return emptyList()
        }

        //prepare route
        //get python instance
        val py = Python.getInstance()
        val routeCreation = py.getModule("routeCreation")

        //convert list of locations into python points
        val pyPoints: MutableList<PyObject> = mutableListOf()
        for (point in locations) {
            pyPoints.add(routeCreation.callAttr("create_Point", point.latitude, point.longitude, point.timestamp))
        }

        //extract timestamps from locations
        val timestamps = locations.map { location -> location.timestamp }

        //create route from points
        val pyRoute = routeCreation.callAttr("create_Route", pyPoints.toTypedArray(), timestamps.toTypedArray())

        when (metric) {
            Metric.StopDetection -> {
                return ExtractPOIs().invoke(pyRoute).map { poi -> Pair<Long, Double>(poi.timestamp, 1.toDouble()) }
            }
        }
    }
}