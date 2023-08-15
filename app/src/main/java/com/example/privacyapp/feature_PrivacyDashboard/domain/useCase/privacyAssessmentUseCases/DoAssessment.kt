package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases


import android.icu.util.Calendar
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.data.repository.PreferencesManagerImpl
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.POI
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.POIRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PreferencesManager
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.ExtractPOIs
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.ApplicationProvider
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DoAssessment(
    private val locationRepository: LocationRepository,
    private val privacyAssessmentRepository: PrivacyAssessmentRepository,
    private val poiRepository: POIRepository
) {

    private val sharedPrefs = PreferencesManagerImpl(ApplicationProvider.application)

    private val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()

    //will only be used for the POI frequency metric
    private var numberOfClusters = 0


    /**
     * Invokes the privacy metric computation for a given metric, metric interval, and metric type.
     *
     * @param metric The privacy metric to be computed (Metric.StopDetection or Metric.StopFrequency).
     * @param metricInterval The time interval (MetricInterval) for which the computation should be performed.
     * @param metricType The type of the metric (MetricType).
     * @return A list of pairs representing the computed privacy metric values over the specified time intervals.
     */
    suspend operator fun invoke(
        metric: Metric,
        metricInterval: MetricInterval,
        metricType: MetricType
    ): List<Pair<Int, Double>> {
        var resultData = listOf<Pair<Int, Double>>()

        when (metricInterval) {
            MetricInterval.DAY -> {
                val res = createEmptyResultList(MetricInterval.DAY)

                val timestamp24HoursAgoRoundedToCompleteHour = getStartTimestamp(metricInterval)
                //get locations for the last 24 Hours,
                val locations =
                    locationRepository.getUsedLocations(timestamp24HoursAgoRoundedToCompleteHour)
                        .sortedBy { it.timestamp }
                if (locations.isEmpty()) {
                    return res
                }

                //compute metric
                when (metric) {
                    Metric.StopDetection -> {
                        //compute python route from locations
                        val pyRoute = createPythonRoute(locations)

                        var metricResult = ExtractPOIs(poiRepository).invoke(pyRoute, false)
                            .map { poi -> Pair<Long, Double>(poi.timestamp, 1.toDouble()) }
                            .sortedBy { it.first }

                        //convert timestamp into hour
                        metricResult = metricResult.map { pair ->
                            Pair(
                                Instant.ofEpochMilli(pair.first)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalTime().hour.toLong(),
                                pair.second
                            )
                        }.toMutableList()


                        val startHour = res[0].first
                        for (item in metricResult) {
                            val index = ((item.first + 24 - startHour) % 24).toInt()
                            res[index] = Pair(item.first.toInt(), res[index].second + item.second)
                        }
                        resultData = res
                    }

                    Metric.StopFrequency -> {

                        val pyRoute = createPythonRoute(locations.filter { !it.processed })
                        //locations are saved to db in this function
                        ExtractPOIs(poiRepository).invoke(pyRoute, true)
                        //get all pois in th last 24h
                        val pOIsLast24h = poiRepository.getPOIsSinceTimestamp(
                            timestamp24HoursAgoRoundedToCompleteHour
                        )
                        // compute result data by finding duplicates
                        resultData = clusterPOIs(pOIsLast24h, metricInterval)

                        //set all locations.processed to true
                        for (location in locations) {
                            if (!location.processed) {
                                //update them
                                locationRepository.insertLocation(
                                    Location(
                                        location.longitude,
                                        location.latitude,
                                        location.timestamp,
                                        location.locationUsed,
                                        true
                                    )
                                )
                            }
                        }
                    }
                }
            }

            MetricInterval.WEEK -> {

                var timestamp = getStartTimestamp(metricInterval)

                var res = createEmptyResultList(MetricInterval.WEEK)

                when (metric) {
                    //separated from the rest, as this metric has dependencies to the days before, therefore need special treatment
                    Metric.StopFrequency -> {
                        val locations = locationRepository.getUsedLocationsByInterval(
                            timestamp,
                            System.currentTimeMillis()
                        ).filter { !it.processed }
                        val pyRoute = createPythonRoute(locations)
                        //locations are saved to db in this function
                        ExtractPOIs(poiRepository).invoke(pyRoute, true)
                        //get all pois
                        val pOIs = poiRepository.getPOIsSinceTimestamp(timestamp)
                        // compute result data by finding duplicates
                        res = clusterPOIs(pOIs, metricInterval).toMutableList()
                        //set all locations.processed to true
                        for (location in locations) {
                            if (!location.processed) {
                                //update them
                                locationRepository.insertLocation(
                                    Location(
                                        location.longitude,
                                        location.latitude,
                                        location.timestamp,
                                        location.locationUsed,
                                        true
                                    )
                                )
                            }
                        }
                    }

                    else -> {
                        //every metric which can be computed independly from the days before
                        //get already existing assessments, subtract 1 min to be sure get all corresponding assessments, since timestamps are not accurate on ms
                        val existingAssessments =
                            privacyAssessmentRepository.getAssessment1dByMetricSinceTimestamp(
                                metric,
                                timestamp - (1000 * 60)
                            ).sortedBy { it.timestampStart }.toMutableList()
                        //for loop over the days and compute for each day individually so completed days can be chached for future computations
                        for (i in 0..6) {

                            //check if assessment for that day and that metric is already in db
                            if (existingAssessments.isNotEmpty() && abs(existingAssessments[0].timestampStart - timestamp) < 1000 * 60 * 5) {
                                //current timestamp is close to the object in the db
                                res[i] = Pair(res[i].first, existingAssessments[0].metricValue)
                                existingAssessments.removeAt(0)
                            } else {

                                //get location
                                val locations = locationRepository.getUsedLocationsByInterval(
                                    timestamp,
                                    timestamp + (1000 * 60 * 60 * 24)
                                ).sortedBy { it.timestamp }
                                if (locations.isEmpty()) {
                                    res[i] = Pair(res[i].first, 0.toDouble())
                                } else {
                                    //compute python route from locations
                                    val pyRoute = createPythonRoute(locations)

                                    when (metric) {
                                        Metric.StopDetection -> {
                                            //compute metric
                                            val metricResult =
                                                ExtractPOIs(poiRepository).invoke(pyRoute, false).size
                                            res[i] = Pair(res[i].first, metricResult.toDouble())
                                            //if its not the current day(because the result might change over the day), add assessment to db
                                            if (i != 6) {
                                                privacyAssessmentRepository.insertAssessment1d(
                                                    PrivacyAssessment1d(
                                                        timestamp,
                                                        metric.metricName,
                                                        metricResult.toDouble()
                                                    )
                                                )
                                            }
                                        }

                                        else -> {/*these have been considered even before this when block*/
                                        }
                                    }
                                }
                            }
                            //plus 1 day
                            timestamp += (1000 * 60 * 60 * 24)
                        }
                    }
                }
                resultData = res

            }

            MetricInterval.MONTH -> {

                var timestamp = getStartTimestamp(metricInterval)

                var res = createEmptyResultList(MetricInterval.MONTH)

                when (metric) {
                    Metric.StopFrequency -> {
                        val locations = locationRepository.getUsedLocationsByInterval(
                            timestamp,
                            System.currentTimeMillis()
                        ).filter { !it.processed }
                        val pyRoute = createPythonRoute(locations)
                        //locations are saved to db in this function
                        ExtractPOIs(poiRepository).invoke(pyRoute, true)
                        //get all pois
                        val pOIs = poiRepository.getPOIsSinceTimestamp(timestamp)
                        // compute result data by finding duplicates
                        res = clusterPOIs(pOIs, metricInterval).toMutableList()
                        //set all locations.processed to true
                        for (location in locations) {
                            if (!location.processed) {
                                //update them
                                locationRepository.insertLocation(
                                    Location(
                                        location.longitude,
                                        location.latitude,
                                        location.timestamp,
                                        location.locationUsed,
                                        true
                                    )
                                )
                            }
                        }
                    }

                    else -> {
                        //every metric which can be computed independly from the days before
                        //get already existing assessments, subtract 1 min to be sure get all corresponding assessments, since timestamps are not accurate on ms
                        val existingAssessments =
                            privacyAssessmentRepository.getAssessment1dByMetricSinceTimestamp(
                                metric,
                                timestamp - (1000 * 60)
                            ).sortedBy { it.timestampStart }.toMutableList()

                        for (i in 0 until res.size) {
                            //check if assessment for that day and that metric is already in db
                            if (existingAssessments.isNotEmpty() && abs(existingAssessments[0].timestampStart - timestamp) < 1000 * 60 * 5) {
                                //current timestamp is close to the object in the db
                                res[i] = Pair(res[i].first, existingAssessments[0].metricValue)
                                existingAssessments.removeAt(0)
                            } else {

                                //get location
                                val locations = locationRepository.getUsedLocationsByInterval(
                                    timestamp,
                                    timestamp + (1000 * 60 * 60 * 24)
                                ).sortedBy { it.timestamp }
                                if (locations.isEmpty()) {
                                    res[i] = Pair(res[i].first, 0.toDouble())
                                } else {
                                    //compute python route from locations
                                    val pyRoute = createPythonRoute(locations)

                                    when (metric) {
                                        Metric.StopDetection -> {
                                            //compute metric
                                            val metricResult =
                                                ExtractPOIs(poiRepository).invoke(pyRoute, false).size
                                            res[i] = Pair(res[i].first, metricResult.toDouble())
                                            //if its not the current day(because the result might change over the day), add assessment to db
                                            if (i != res.size - 1) {
                                                privacyAssessmentRepository.insertAssessment1d(
                                                    PrivacyAssessment1d(
                                                        timestamp,
                                                        metric.metricName,
                                                        metricResult.toDouble()
                                                    )
                                                )
                                            }
                                        }

                                        else -> {/*these have been considered even before this when block*/
                                        }
                                    }
                                }
                            }
                            //plus 1 day
                            timestamp += (1000 * 60 * 60 * 24)
                        }
                    }
                }
                resultData = res
            }
        }
        return reformatData(resultData, metricType, metric, metricInterval)
    }

    /**
     * Clusters the given list of Points of Interest (POIs) that occurred within the last 24 hours
     * based on their geographical proximity and calculates the number of POIs which occurred more
     * often than the specified threshold by the user within the specified time intervals.
     * if(poiCluster.size > threshold) -> add 1 in the specified timeframe(day or hour)
     * In Order for a point to be in a cluster it has to have a max distance to every other point in the cluster,
     * which is half the POI Radius. The POI Radius is specified by the user in the settings
     *
     * @param pOIs A list of Points of Interest (POIs) that occurred within the last 24 hours.
     * @param metricInterval The time interval (MetricInterval) for which the occurrences should be calculated.
     * @return a list of pairs representing
     * the number of POIs which occurred more often than the specified threshold by the user within the specified time intervals.
     */
    private fun clusterPOIs(
        pOIs: List<POI>,
        metricInterval: MetricInterval
    ): List<Pair<Int, Double>> {

        val pOIs = pOIs.toMutableList()
        /*var string = ""
        for (poi in pOIs) {
            string += poi.latitude.toString() + "," + poi.longitude.toString() + "\n"
        }
        println(string)*/
        val result = createEmptyResultList(metricInterval)

        val distanceThreshold =
            (sharedPrefs.getSettingInt(PreferencesManager.POI_RADIUS) / 2f).toInt()

        //find cluster
        val clusters = mutableListOf<MutableList<POI>>()

        for (poi in pOIs) {
            var addedToCluster = false
            for (cluster in clusters) {
                // you could go through all pois and check the condition and add the POI when at least one fulfills it.
                // But i think in this special usecase, this works better
                if (calculationByDistance(
                        cluster[0].latitude,
                        cluster[0].longitude,
                        poi.latitude,
                        poi.longitude
                    ) * 1000 < distanceThreshold
                ) {
                    cluster.add(poi)
                    addedToCluster = true
                    break
                }
            }
            if (!addedToCluster) {
                clusters.add(mutableListOf(poi))
            }
        }

        //reformat data according to metricInterval
        val maxOccurrence = sharedPrefs.getSettingInt(PreferencesManager.MAX_POI_OCCURRENCE)

        for (cluster in clusters) {
            if (cluster.size >= maxOccurrence) {
                when (metricInterval) {
                    MetricInterval.DAY -> {
                        val hour = Instant.ofEpochMilli(cluster[maxOccurrence - 1].timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime().hour
                        val startHour = result[0].first
                        val index = ((hour + 24 - startHour) % 24)

                        result[index] = Pair(hour, result[index].second + 1)

                    }

                    else -> {
                        //DAY AND WEEK
                        val day = Instant.ofEpochMilli(cluster[maxOccurrence - 1].timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime().dayOfMonth
                        for ((index, dayOfMonth) in result.withIndex()) {
                            if (dayOfMonth.first == day) {
                                result[index] = Pair(day, result[index].second + 1)
                            }
                        }
                    }
                }
            }
        }
        numberOfClusters = clusters.size
        return result
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

    /**
     * Converts an angle in degrees to its equivalent in radians.
     *
     * @param deg The angle in degrees to be converted to radians.
     * @return The equivalent angle in radians.
     */
    private fun toRadians(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    /**
     * creates, depending on the Interval, a list with the same format as the result, but pair.second (the actual assessment) is 0
     * @param metricInterval MetricInterval
     * @return List with pairs (int, double)
     */
    private fun createEmptyResultList(metricInterval: MetricInterval): MutableList<Pair<Int, Double>> {
        val zeros = mutableListOf<Pair<Int, Double>>()
        when (metricInterval) {
            MetricInterval.DAY -> {
                //val startHour = (timeDate.hour + 1) % 24
                val rightNow = Calendar.getInstance()
                val startHour = (rightNow.get(Calendar.HOUR_OF_DAY) + 1) % 24

                for (i in 0..23) {
                    zeros.add(Pair((startHour + i) % 24, 0.toDouble()))
                }
            }

            MetricInterval.WEEK -> {
                for (i in 6 downTo 0) {
                    zeros.add(Pair(timeDate.minusDays(i.toLong()).dayOfMonth, 0.toDouble()))
                }
            }

            MetricInterval.MONTH -> {
                var date = timeDate.minusMonths(1).plusDays(1)
                val nextDay = timeDate.plusDays(1)
                while (date.month != nextDay.month || date.dayOfMonth != nextDay.dayOfMonth) {
                    zeros.add(Pair(date.dayOfMonth, 0.toDouble()))
                    date = date.plusDays(1)
                }
            }
        }
        return zeros
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

    /**
     * returns the timestamp where the assessment should start, depending on the metricInterval
     * @param metricInterval metricInterval
     * @return unix epoche timestamp
     */
    private fun getStartTimestamp(metricInterval: MetricInterval): Long {
        //utils find start timestamp
        val currentTime = System.currentTimeMillis()
        val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val minutesFromCompleteHour = timeDate.minute * 60 * 1000
        val minutesToCompleteHour = (60 - timeDate.minute) * 60 * 1000
        val secondsFromCompleteMinute = timeDate.second * 1000
        val hoursToCompleteDay = (24 - timeDate.hour) * 60 * 60 * 1000

        return when (metricInterval) {
            MetricInterval.DAY -> {
                //start Timestamp for the Interval current time - 24h + minutesToCompleteHour - seconds
                currentTime - (1000 * 60 * 60 * 24) + minutesToCompleteHour - secondsFromCompleteMinute
            }

            MetricInterval.WEEK -> {
                //start Timestamp current time - 7days + hoursToCompleteDay - hoursOfDay - minutes - seconds
                currentTime - (1000 * 60 * 60 * 24 * 7) + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
            }

            MetricInterval.MONTH -> {
                //timestamp current time - 1month + hoursToCompleteDay - hoursOfDay - minutes - seconds
                ChronoUnit.MILLIS.between(
                    Instant.EPOCH,
                    Instant.now().atZone(ZoneId.systemDefault()).minusMonths(1)
                ) + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
            }

        }
    }

    /**
     * reformat data according to metric type. When metricType is cumulative, values get added up,
     * otherwise, when metricType is score, value get scaled with an max value in order to maintain a value range from [0,1]
     * @param data raw Data
     * @param metricType metric type
     * @param metric Metric
     * @return reformatted data
     */
    private fun reformatData(
        data: List<Pair<Int, Double>>,
        metricType: MetricType,
        metric: Metric,
        metricInterval: MetricInterval
    ): List<Pair<Int, Double>> {
        if (metricType == MetricType.ABSOLUT) {
            //no reformatting needed
            return data
        }
        //modify Data according to metric Type
        val data = data.toMutableList()
        var cumulativeScore = 0.toDouble()
        //get value to scale results
        val maxValue = when (metric) {
            Metric.StopDetection -> {
                when (metricInterval) {
                    MetricInterval.DAY -> sharedPrefs.getSettingInt(PreferencesManager.POI_LIMIT)
                    else -> {
                        if (sharedPrefs.getSettingBool(PreferencesManager.DYNAMIC_LIMIT)) {
                            sharedPrefs.getSettingInt(PreferencesManager.POI_LIMIT) * data.size
                        } else {
                            sharedPrefs.getSettingInt(PreferencesManager.POI_LIMIT)
                        }
                    }
                }
            }

            Metric.StopFrequency -> {
                numberOfClusters
            }
        }
        for ((index, result) in data.withIndex()) {
            if (metricType == MetricType.SCORE) {
                data[index] = Pair(result.first, result.second + cumulativeScore)
                cumulativeScore += result.second

                data[index] = (if (data[index].second <= maxValue) {
                    Pair(data[index].first, data[index].second / maxValue)
                } else {
                    Pair(data[index].first, 1.toDouble())
                })
            }
        }
        return data
    }
}