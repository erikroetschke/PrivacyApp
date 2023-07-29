package com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases

import androidx.compose.runtime.mutableStateListOf
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.Location
import com.example.privacyapp.feature_PrivacyDashboard.domain.model.PrivacyAssessment1h
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.LocationRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.repository.PrivacyAssessmentRepository
import com.example.privacyapp.feature_PrivacyDashboard.domain.useCase.privacyAssessmentUseCases.metrics.CallMetric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.Metric
import com.example.privacyapp.feature_PrivacyDashboard.domain.util.MetricInterval
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar

class DoAssessment (
    private val locationRepository: LocationRepository,
    private val privacyAssessmentRepository: PrivacyAssessmentRepository
){

     suspend operator fun invoke(metric: Metric, metricInterval: MetricInterval): List<Pair<Int, Double>> {
        var resultData = listOf<Pair<Int, Double>>()

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
                    resultData = metricResult.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }.map { entry -> Pair(entry.key.toInt(), entry.value) }

                    // check if there is a value for each hour, otherwise add 0
                    val startHour = currentHour + 1 % 24
                    //create list where every hour has 0 as value
                    val zeros = mutableListOf<Pair<Int, Double>>()
                    for (i in 0..23) {
                        zeros.add(Pair((startHour + i) % 24, 0.toDouble()))
                    }
                    var index = 0
                    //map value from assessment on list
                    if(resultData.isNotEmpty()){
                        for ((indexZeros, element) in zeros.withIndex()) {
                            if(element.first == resultData[index].first) {
                                zeros[indexZeros] = resultData[index]
                                if (resultData.size > index + 1) {
                                    index++
                                }
                            }
                        }
                    }


                    resultData = zeros

                    /*
                    //drop table for debugging
                    privacyAssessmentRepository.deleteAssessment1hOlderThanTimestamp(System.currentTimeMillis())

                    //look if there is existing data since this timestamp, also decrease timestamp for 1 minute, as timestamps may are not accurate to the full hour on the seconds
                    val assessmentData = privacyAssessmentRepository.getAssessment1hByMetricSinceTimestamp(metric, timestamp24HoursAgoRoundedToCompleteHour - 1000 * 60 ).sortedBy { it.timestampStart }.toMutableList()
                    //look how far data reaches to find hour where assessment needs to be computed
                    val timestampStart = if(assessmentData.isNotEmpty()) {
                        (1000 * 60 * 60) + assessmentData.last().timestampStart
                    }else {
                        timestamp24HoursAgoRoundedToCompleteHour
                    }
                    //get locations that have to be computed
                    val locations = locationRepository.getUsedLocations(timestampStart).sortedBy { it.timestamp }

                    //break them down into 1h blocks
                    val locationsIn1hBlocks = mutableStateListOf<List<Location>>()
                    val temp = mutableStateListOf<Location>()
                    var currentMaxTimeStamp = timestampStart + (1000 * 60 *60)
                    val locationIterator = locations.listIterator()


                    //put them in buckets
                    while (locationIterator.hasNext()) {
                        val next = locationIterator.next()
                        if (next.timestamp > currentMaxTimeStamp) {
                            locationsIn1hBlocks.add(temp.toMutableList())
                            temp.clear()
                            currentMaxTimeStamp += (1000 * 60 *60)
                            locationIterator.previous()

                        }else{
                            temp.add(next)
                        }
                    }
                    // add the last list, as it has not been added in the while loop
                    locationsIn1hBlocks.add(temp.toMutableList())
                    //add empty lists for the possible remaining hours
                    val remainingHours = 24 - locationsIn1hBlocks.size - assessmentData.size - 1
                    for (i in 0..remainingHours) {
                        locationsIn1hBlocks.add(emptyList())
                    }

                    for ((index, locationChunk) in locationsIn1hBlocks.withIndex()) {
                        //perform Metric on Chunk
                        val privacyLeakScore = CallMetric().invoke(locationChunk, metric) //TODO also pass existing Data to check for duplicates
                        val timestampHour = timestampStart + index * 1000 * 60 * 60
                        //add this to AssessmentData
                        assessmentData.add(PrivacyAssessment1h(timestampHour, metric.metricName, metric.metricDescription, privacyLeakScore, metric.weighting))
                        //safe them into the DB except for the last, because it may be incomplete as the hour isn't over yet
                        if (timestampStart + (index+1) * 1000 * 60 * 60 < currentTime){
                            privacyAssessmentRepository.insertAssessment1h(PrivacyAssessment1h(timestampHour, metric.metricName, metric.metricDescription, privacyLeakScore, metric.weighting))
                        }
                    }
                    //put data in resultList
                    for ((index, assessment) in assessmentData.withIndex()){
                        resultData.add(Pair(startHour + index, assessment.metricValue))
                    }
                    */

                }
                MetricInterval.WEEK -> {

                    //start Timestamp current time - 7days + hoursToCompleteDay - hoursOfDay - minutes - seconds
                    val timestampOneWeekAgoRoundedUpToFullDay = currentTime - (1000 * 60 * 60 * 24 * 7) + hoursToCompleteDay - minutesFromCompleteHour - secondsFromCompleteMinute
                    //get locations
                    val locations = locationRepository.getUsedLocations(timestampOneWeekAgoRoundedUpToFullDay).sortedBy { it.timestamp }
                    //compute metric
                    var metricResult = CallMetric().invoke(locations, metric)
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
                    resultData = metricResult.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }.map { entry -> Pair(entry.key.toInt(), entry.value) }

                    // check if there is a value for each Day, otherwise add 0
                    //create list where every day has 0 as value
                    val zeros = mutableListOf<Pair<Int, Double>>()
                    for (i in 6 downTo 0) {
                        zeros.add(Pair(timeDate.minusDays(i.toLong()).dayOfMonth, 0.toDouble()))
                    }
                    var index = 0
                    //map value from assessment on list
                    if (resultData.isNotEmpty()) {
                        for ((indexZeros, element) in zeros.withIndex()) {
                            if(element.first == resultData[index].first) {
                                zeros[indexZeros] = resultData[index]
                                if (resultData.size > index + 1) {
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
                    var metricResult = CallMetric().invoke(locations, metric)
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

        return resultData
    }
}