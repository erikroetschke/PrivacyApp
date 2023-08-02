package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases


import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1d
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.CallMetric
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.ExtractPOIs
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class DoAssessment(
    private val locationRepository: LocationRepository,
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
) {

    private val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()

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
                //compute python route from locations
                val pyRoute = createPythonRoute(locations)
                //compute metric
                when (metric) {
                    Metric.StopDetection -> {
                        var metricResult = ExtractPOIs().invoke(pyRoute)
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

                        //group by hour and sum up value(second number of the pair) and map it back into a List of Pairs
                        metricResult = metricResult.groupBy { it.first }
                            .mapValues { entry -> entry.value.sumOf { it.second } }
                            .map { entry -> Pair(entry.key, entry.value) }
                        // check if there is a value for each hour, otherwise add 0

                        resultData = mapListonList(res, metricResult)
                    }

                    Metric.StopFrequency -> {
                        //TODO
                    }
                }

            }

            MetricInterval.WEEK -> {

                var timestamp = getStartTimestamp(metricInterval)

                //for debugging
                /*val toDelete =
                    privacyAssessmentRepository.getAssessment1dByMetricSinceTimestamp(
                        metric,
                        getStartTimestamp(MetricInterval.MONTH)
                    ).sortedBy { it.timestampStart }.toMutableList()
                for (item in toDelete) {
                    privacyAssessmentRepository.deleteAssessment1d(item)
                }*/

                val res = createEmptyResultList(MetricInterval.WEEK)

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
                                    val metricResult = ExtractPOIs().invoke(pyRoute).size
                                    res[i] = Pair(res[i].first, metricResult.toDouble())
                                    //if its not the current day(because the result might change over the day), add assessment to db
                                    if (i != 6) {
                                        privacyAssessmentRepository.insertAssessment1d(
                                            PrivacyAssessment1d(
                                                timestamp,
                                                metric.metricName,
                                                metric.metricDescription,
                                                metricResult.toDouble(),
                                                metric.weighting
                                            )
                                        )
                                    }
                                }

                                Metric.StopFrequency -> {
                                    //TODO
                                }
                            }
                        }
                    }
                    //plus 1 day
                    timestamp += (1000 * 60 * 60 * 24)
                }
                resultData = res

                /*//get locations
                val locations = locationRepository.getUsedLocations(timestampOneWeekAgoRoundedUpToFullDay).sortedBy { it.timestamp }
                //compute metric
                var metricResult = CallMetric().invoke(locations, metric).sortedBy { it.first }
                //convert timestamp into day of Month
                metricResult = metricResult.map { pair ->
                    Pair(
                        Instant.ofEpochMilli(pair.first)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime().dayOfMonth.toLong(),
                        pair.second
                    )
                }
                //group by day and sum up value(second number of the pair) and map it back into a List of Pairs
                metricResult = metricResult.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }.map { entry -> Pair(entry.key, entry.value) }

                // check if there is a value for each Day, otherwise add 0
                //create list where every day has 0 as value
                val zeros = mutableListOf<Pair<Int, Double>>()
                for (i in 6 downTo 0) {
                    zeros.add(Pair(timeDate.minusDays(i.toLong()).dayOfMonth, 0.toDouble()))
                }
                var index = 0
                //map value from assessment on list
                if (metricResult.isNotEmpty()) {
                    for ((indexZeros, element) in zeros.withIndex()) {
                        if(element.first == metricResult[index].first.toInt()) {
                            zeros[indexZeros] = metricResult[index].let { pair -> Pair(pair.first.toInt(), pair.second) }
                            if (metricResult.size > index + 1) {
                                index++
                            }
                        }
                    }
                }

                resultData = zeros*/

            }

            MetricInterval.MONTH -> {

                var timestamp = getStartTimestamp(metricInterval)

                val res = createEmptyResultList(MetricInterval.MONTH)

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
                                    val metricResult = ExtractPOIs().invoke(pyRoute).size
                                    res[i] = Pair(res[i].first, metricResult.toDouble())
                                    //if its not the current day(because the result might change over the day), add assessment to db
                                    if (i != res.size - 1) {
                                        privacyAssessmentRepository.insertAssessment1d(
                                            PrivacyAssessment1d(
                                                timestamp,
                                                metric.metricName,
                                                metric.metricDescription,
                                                metricResult.toDouble(),
                                                metric.weighting
                                            )
                                        )
                                    }
                                }

                                Metric.StopFrequency -> {
                                    //TODO
                                }
                            }
                        }
                    }
                    //plus 1 day
                    timestamp += (1000 * 60 * 60 * 24)

                }

                /*//get locations
                val locations = locationRepository.getUsedLocations(timestamp).sortedBy { it.timestamp }
                //compute metric
                var metricResult = CallMetric().invoke(locations, metric).sortedBy { it.first }
                //convert timestamp into day of Month
                val temp = metricResult.map { pair ->
                    Pair(
                        //add also month, because day of month is not unique
                        (Instant.ofEpochMilli(pair.first)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime().dayOfMonth.toString() + "X" +
                                Instant.ofEpochMilli(pair.first)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime().month.toString()
                                ),
                        pair.second
                    )
                }

                //group by and sum up
                val tempMap = temp.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }
                //convert back into resultData and remove month
                val result = mutableListOf<Pair<Int, Double>>()
                val regex = Regex("[0-9]+")
                for ((key, value) in tempMap) {
                    result.add(Pair(regex.find(key)?.value.toString().toInt(), value))
                }

                // check if there is a value for each Day, otherwise add 0
                //create list where every day has 0 as value
                var date = timeDate.minusMonths(1).plusDays(1)
                val nextDay = timeDate.plusDays(1)
                val zeros = mutableListOf<Pair<Int, Double>>()
                while (date.month != nextDay.month || date.dayOfMonth != nextDay.dayOfMonth) {
                    zeros.add(Pair(date.dayOfMonth, 0.toDouble()))
                    date = date.plusDays(1)
                }
                var index = 0
                //map value from assessment on list
                if(result.isNotEmpty()) {
                    for ((indexZeros, element) in zeros.withIndex()) {
                        if(element.first == result[index].first) {
                            zeros[indexZeros] = result[index]
                            if (result.size > index + 1) {
                                index++
                            }
                        }
                    }
                }
                   */
                resultData = res
            }
        }

        return reformatData(resultData, metricType, metric)
    }

    /**
     * maps the first list on second list. When firstList[i].first == secondList[i].first -> firstList[i].second = secondList[i].second
     * then returns firstlist
     * requires both lists to be sorted by date (e.g. when Interval is Week a valid sorted list looks like: (28,x), (29,x), (30,x), (1,x), (2,x), (3,x), (4,x))
     */
    private fun mapListonList(
        firstList: MutableList<Pair<Int, Double>>,
        secondList: List<Pair<Long, Double>>
    ): List<Pair<Int, Double>> {
        var index = 0
        if (secondList.isNotEmpty()) {
            for ((indexFirst, element) in firstList.withIndex()) {
                if (element.first == secondList[index].first.toInt()) {
                    firstList[indexFirst] =
                        secondList[index].let { pair -> Pair(pair.first.toInt(), pair.second) }
                    if (secondList.size > index + 1) {
                        index++
                    }
                }
            }
        }
        return firstList
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
                val startHour = timeDate.hour + 1 % 24
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
        metric: Metric
    ): List<Pair<Int, Double>> {
        //modify Data according to metric Type
        val data = data.toMutableList()
        var cumulativeScore = 0.toDouble()
        for ((index, result) in data.withIndex()) {
            if (metricType == MetricType.CUMULATIVE) {
                data[index] = Pair(result.first, result.second + cumulativeScore)
                cumulativeScore += result.second
            } else if (metricType == MetricType.SCORE) {

                data[index] = (if (result.second <= metric.maxValue) {
                    Pair(result.first, result.second / metric.maxValue)
                } else {
                    Pair(result.first, 1.toDouble())
                })

            }
        }
        return data
    }
}