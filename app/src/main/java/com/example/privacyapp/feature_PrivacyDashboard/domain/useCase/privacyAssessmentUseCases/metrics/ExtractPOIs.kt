package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ExtractPOIs {

    operator fun invoke(pyRoute: PyObject): List<POI>{

        //get python instance
        val py = Python.getInstance()
        val stopDetection = py.getModule("stopDetection")

        //perform stop detection
        val pois = stopDetection.callAttr("extract_pois", pyRoute).asList()

        //extract found pois from pyobject
        val regex = Regex("[+-]?\\d*\\.?\\d+")
        val finalPois = mutableListOf<POI>()
        for (i in 0 until pois.size) {
            val poi = pois[i].toString()
            val temp = regex.findAll(poi).map { it.value }.toList()

            //timestamp is not the actual timestamp of the POI, timestamps are sequential numbers to order the locations. The lower the number, the earlier the POI was found
            finalPois.add(POI(Math.toDegrees(temp[0].toDouble()),Math.toDegrees(temp[1].toDouble()), System.currentTimeMillis() - (i*60*60*1000), 1))

        }
        return finalPois
    }

    /**
     * calculates distance between two points using Haversine formula
     * @returns distance in km
     */
    private fun calculationByDistance(
        initialLat: Double, initialLong: Double,
        finalLat: Double, finalLong: Double
    ): Double {
        var initialLat = initialLat
        var finalLat = finalLat
        val R = 6371 // km (Earth radius)
        val dLat = toRadians(finalLat - initialLat)
        val dLon = toRadians(finalLong - initialLong)
        initialLat = toRadians(initialLat)
        finalLat = toRadians(finalLat)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(initialLat) * cos(finalLat)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun toRadians(deg: Double): Double {
        return deg * (Math.PI / 180)
    }
}