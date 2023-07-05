package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location

class GetNumberOfPOI {

    operator fun invoke(points: List<Location>): List<Location>{

        //get python instance
        val py = Python.getInstance()
        val modulePoint = py.getModule("point_t")

        //convert into python points
        val pyPoints: MutableList<PyObject> = mutableListOf()
        for (point in points) {
            pyPoints.add(modulePoint.callAttr("__init__", listOf(point.latitude, point.longitude), point.timestamp))
        }

        return points
    }
}