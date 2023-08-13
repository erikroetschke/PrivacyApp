package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ExtractPOIs(
    private val poiRepository: POIRepository
) {

    private val sharedPrefs = PreferencesManagerImpl(ApplicationProvider.application)

    suspend operator fun invoke(pyRoute: PyObject): List<POI>{

        //get python instance
        val py = Python.getInstance()
        val stopDetection = py.getModule("stopDetection")

        //perform stop detection
        val pois = stopDetection.callAttr("extract_pois", pyRoute, sharedPrefs.getSettingInt(PreferencesManager.MIN_POI_TIME), sharedPrefs.getSettingInt(PreferencesManager.POI_RADIUS)).asList()

        //extract found pois from pyobject
        val regex = Regex("[+-]?\\d*\\.?\\d+")
        val finalPois = mutableListOf<POI>()
        for (i in 0 until pois.size) {
            val poi = pois[i].toString()
            val temp = regex.findAll(poi).map { it.value }.toList()

            val finalPOI = POI(Math.toDegrees(temp[1].toDouble()),Math.toDegrees(temp[2].toDouble()), temp[0].toFloat().toLong())

            finalPois.add(finalPOI)

            if (i != pois.size - 1){
                //add to db, except the last one as the timestamp might change in future computations
                poiRepository.insertPOI(finalPOI)
            }
        }
        return finalPois
    }
}