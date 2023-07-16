package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location

class StopDetection {

    operator fun invoke(points: List<Location>): List<Location>{

        //get python instance
        val py = Python.getInstance()
        val routeCreation = py.getModule("routeCreation")
        val stopDetection = py.getModule("stopDetection")

        //convert list of locations into python points
        val pyPoints: MutableList<PyObject> = mutableListOf()
        for (point in points) {
            pyPoints.add(routeCreation.callAttr("create_Point", point.latitude, point.longitude, point.timestamp))
        }

        //extract timestamps from locations
        val timestamps = points.map { point -> point.timestamp }

        //create route from points
        val pyRoute = routeCreation.callAttr("create_Route", pyPoints.toTypedArray(), timestamps.toTypedArray())

        //perform stop detection
        val pois = stopDetection.callAttr("extract_pois", pyRoute).asList()

        //extract found pois from pyobject
        val regex = Regex("[+-]?\\d*\\.?\\d+")
        val finalPois = mutableListOf<Location>()
        for (i in 0 until pois.size) {
            val poi = pois.get(i).toString()
            val temp = regex.findAll(poi).map { it.value }.toList()
            //timestamp is not the actual timestamp of the POI, timestamps are sequential numbers to order the locations. The lower the number, the earlier the POI was found
            finalPois.add(Location(Math.toDegrees(temp[0].toDouble()),Math.toDegrees(temp[1].toDouble()), i.toLong(), true))
        }

        return finalPois
    }
}