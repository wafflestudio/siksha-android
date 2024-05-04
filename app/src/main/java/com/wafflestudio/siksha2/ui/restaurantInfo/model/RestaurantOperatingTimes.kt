package com.wafflestudio.siksha2.ui.restaurantInfo.model

import com.wafflestudio.siksha2.models.RestaurantInfo

class RestaurantOperatingTimes(
    val weekdays: DailyOperatingTimes?,
    val saturday: DailyOperatingTimes?,
    val holiday: DailyOperatingTimes?
)

class DailyOperatingTimes(
    val breakfast: OperatingTime?,
    val lunch: OperatingTime?,
    val dinner: OperatingTime?
) {
    companion object {
        const val BREAKFAST_TIME_AS_MINUTE = 510
        const val LUNCH_TIME_AS_MINUTE = 750
        const val DINNER_TIME_AS_MINUTE = 1080
    }
}

data class OperatingTime(
    val startMinute: Int,
    val endMinute: Int
) {
    override fun toString(): String {
        return "${minuteToString(startMinute)}-${minuteToString(endMinute)}"
    }

    private fun minuteToString(minuteSinceMidnight: Int): String {
        val hours = minuteSinceMidnight / 60
        val minutes = minuteSinceMidnight % 60
        return String.format("%02d:%02d", hours, minutes)
    }
}

fun RestaurantInfo.OperatingHour.toRestaurantOperatingTimes(): RestaurantOperatingTimes =
    listOf(weekdays, saturday, holiday)
        .map(::parseDailyOperatingTimes)
        .let { (weekdays, saturday, holiday) ->
            RestaurantOperatingTimes(
                weekdays = weekdays,
                saturday = saturday,
                holiday = holiday
            )
        }

private fun parseDailyOperatingTimes(dailyOperatingTimesString: List<String>): DailyOperatingTimes? {
    val operatingTimes = dailyOperatingTimesString.map(::parseOperatingTime)
    return listOf(
        DailyOperatingTimes.BREAKFAST_TIME_AS_MINUTE,
        DailyOperatingTimes.LUNCH_TIME_AS_MINUTE,
        DailyOperatingTimes.DINNER_TIME_AS_MINUTE
    ).map { pivot ->
        operatingTimes.find { pivot in (it.startMinute..it.endMinute) }
    }.let { (breakfast, lunch, dinner) ->
        if (breakfast == null && lunch == null && dinner == null) {
            null
        } else {
            DailyOperatingTimes(
                breakfast = breakfast,
                lunch = lunch,
                dinner = dinner
            )
        }
    }
}

private fun parseOperatingTime(operatingTimeString: String): OperatingTime =
    operatingTimeString
        .split("-")
        .map(::parseMinute)
        .let { (startMinute, endMinute) ->
            OperatingTime(startMinute, endMinute)
        }

private fun parseMinute(timeString: String): Int {
    val timePattern = """^(\d{2}):(\d{2})$""".toRegex()
    val matchResult = timePattern.matchEntire(timeString)

    if (matchResult != null) {
        val (hours, minutes) = matchResult.destructured
        return hours.toInt() * 60 + minutes.toInt()
    } else {
        throw IllegalArgumentException("Invalid time format. Time format must be HH:mm.")
    }
}
