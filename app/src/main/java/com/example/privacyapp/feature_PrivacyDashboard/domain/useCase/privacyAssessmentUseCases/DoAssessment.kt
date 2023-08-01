package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases


import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.CallMetric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricType

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class DoAssessment (
    private val locationRepository: LocationRepository,
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
){

     suspend operator fun invoke(metric: Metric, metricInterval: MetricInterval, metricType: MetricType): List<Pair<Int, Double>> {
        var resultData = mutableListOf<Pair<Int, Double>>()

            //utils find start timestamp
            val currentTime = System.currentTimeMillis()
            val timeDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val minutesFromCompleteHour = timeDate.minute * 60 * 1000
            val minutesToCompleteHour = (60 - timeDate.minute) * 60 * 1000
            val secondsFromCompleteMinute = timeDate.second * 1000
            val hoursToCompleteDay = (24 - timeDate.hour) * 60 * 60 * 1000
            val currentHour = timeDate.hour

            when (metricInterval) {
                MetricInterval.DAY -> {

                    //start Timestamp for the Interval current time - 24h + minutesToCompleteHour - seconds
                    val timestamp24HoursAgoRoundedToCompleteHour = currentTime - (1000 * 60 * 60 * 24) + minutesToCompleteHour  - secondsFromCompleteMinute
                    //get locations for the last 24 Hours
                    val locations = locationRepository.getUsedLocations(timestamp24HoursAgoRoundedToCompleteHour).sortedBy { it.timestamp }
                    //compute metric
                    var metricResult = CallMetric().invoke(locations, metric).sortedBy { it.first }
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
                    metricResult = metricResult.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }.map { entry -> Pair(entry.key, entry.value) }

                    // check if there is a value for each hour, otherwise add 0
                    val startHour = currentHour + 1 % 24
                    //create list where every hour has 0 as value
                    val zeros = mutableListOf<Pair<Int, Double>>()
                    for (i in 0..23) {
                        zeros.add(Pair((startHour + i) % 24, 0.toDouble()))
                    }
                    var index = 0
                    //map value from assessment on list
                    if(metricResult.isNotEmpty()){
                        for ((indexZeros, element) in zeros.withIndex()) {
                            if(element.first == metricResult[index].first.toInt()) {
                                zeros[indexZeros] = metricResult[index].let { pair -> Pair(pair.first.toInt(), pair.second) }
                                if (metricResult.size > index + 1) {
                                    index++
                                }
                            }
                        }
                    }

                    resultData = zeros

                }
                MetricInterval.WEEK -> {

                    //start Timestamp current time - 7days + hoursToCompleteDay - hoursOfDay - minutes - seconds
                    val timestampOneWeekAgoRoundedUpToFullDay = currentTime - (1000 * 60 * 60 * 24 * 7) + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
                    //get locations
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

                    resultData = zeros

                }
                MetricInterval.MONTH -> {

                    //timestamp current time - 1month + hoursToCompleteDay - hoursOfDay - minutes - seconds
                    val timestampOneMonthAgoRoundedUpToCompleteDay = ChronoUnit.MILLIS.between(Instant.EPOCH, Instant.now().atZone(ZoneId.systemDefault()).minusMonths(1))
                    + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
                    //get locations
                    val locations = locationRepository.getUsedLocations(timestampOneMonthAgoRoundedUpToCompleteDay).sortedBy { it.timestamp }
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

                    resultData = zeros
                }
            }

         //modify Data according to metric Type
         var cumulativeScore = 0.toDouble()
         for ((index, result) in resultData.withIndex()) {
             if(metricType == MetricType.CUMULATIVE) {
                resultData[index] = Pair(result.first, result.second + cumulativeScore)
                 cumulativeScore += result.second
             } else if (metricType == MetricType.SCORE) {

                 resultData[index] = (if(result.second <= metric.maxValue) {
                     Pair(result.first, result.second/ metric.maxValue)
                 } else  {
                     Pair(result.first, 1.toDouble())
                 })

             }
         }

        return resultData
    }
}