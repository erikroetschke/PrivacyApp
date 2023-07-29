package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics

import com.chaquo.python.PyObject
import com.chaquo.python.Python

class StopDetection {

    operator fun invoke(route: PyObject): Int{

        //get python instance
        val py = Python.getInstance()
        val stopDetection = py.getModule("stopDetection")

        //perform stop detection
        val pois = stopDetection.callAttr("extract_pois", route).asList()

        return pois.size
    }
}