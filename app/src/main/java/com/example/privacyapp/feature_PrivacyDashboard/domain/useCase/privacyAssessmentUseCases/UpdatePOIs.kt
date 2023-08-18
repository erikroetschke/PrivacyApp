package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.ExtractPOIs
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider

class UpdatePOIs(
    private val poiRepository: POIRepository,
    private val locationRepository: LocationRepository
) {
    private val sharedPrefs = PreferencesManagerImpl(ApplicationProvider.application)

    /**
     * This method processes location data to extract Points of Interest (POIs).
     * It retrieves location data from the repository, combines it with previous locations within a specified interval,
     * and then creates a Python route for POI extraction. Extracted POIs are saved to the database, and the processed
     * locations are updated accordingly.
     */
    suspend operator fun invoke(){
        val locations = locationRepository.getUsedAndNonProcessedLocations().toMutableList()
        if (locations.isEmpty()){
            return
        }
        val firstLocationTimestamp = locations.first().timestamp
        val locationsBefore = locationRepository.getUsedLocationsByInterval(firstLocationTimestamp - sharedPrefs.getSettingInt(PreferencesManager.MIN_POI_TIME)*60*1000, firstLocationTimestamp - 1)
        locations.addAll(locationsBefore)
        locations.sortedBy { it.timestamp }
        val sublists = splitListByHourDuration(locations)
        for (list in sublists) {
            val pyRoute = createPythonRoute(list)
            ExtractPOIs(poiRepository).invoke(pyRoute = pyRoute, saveToDB = true)
        }
        for (location in locations) {
            //update location to true
            locationRepository.insertLocation(Location(location.longitude, location.latitude, location.timestamp, locationUsed = true, processed = true))
        }
    }

    /**
     * Splits a sorted list of [Location] objects into sublists, where each sublist has a maximum
     * duration of one hour.
     *
     * @param locations The sorted list of [Location] objects to be split.
     * @return A list of sublists containing [Location] objects, where each sublist has a maximum duration of one hour.
     */
    private fun splitListByHourDuration(locations: List<Location>): List<List<Location>> {
        val sublists = mutableListOf<MutableList<Location>>()
        var currentSublist = mutableListOf<Location>()

        if (locations.isEmpty()) {
            return emptyList()
        }

        currentSublist.add(locations[0])
        var previousTimestamp = locations[0].timestamp

        for (i in 1 until locations.size) {
            val currentLocation = locations[i]
            val currentTimestamp = currentLocation.timestamp

            if ((currentTimestamp - previousTimestamp) <= (20 * sharedPrefs.getSettingInt(PreferencesManager.MIN_POI_TIME)* 60 * 1000)) { // 20 *  POI_Min_Threshold in min
                currentSublist.add(currentLocation)
            } else {
                sublists.add(currentSublist)
                currentSublist = mutableListOf(currentLocation)
                previousTimestamp = currentTimestamp
            }
        }

        if (currentSublist.isNotEmpty()) {
            sublists.add(currentSublist)
        }

        return sublists
    }

    /**
     * creates a Route which can be interpreted by python as a de4l_geodata/geodata/route.py.
     * For more information see https://git.informatik.uni-leipzig.de/scads/de4l/privacy/de4l-geodata/-/blob/main/de4l_geodata/geodata/route.py
     * @param locations list of location which will be appended to the route
     * @return PyObject
     */
    private fun createPythonRoute(locations: List<Location>): PyObject {

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
        return routeCreation.callAttr(
            "create_Route",
            pyPoints.toTypedArray(),
            timestamps.toTypedArray()
        )
    }
}