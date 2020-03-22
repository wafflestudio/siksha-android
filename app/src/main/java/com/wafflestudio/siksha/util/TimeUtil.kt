package com.wafflestudio.siksha.util

import android.util.Log
import com.wafflestudio.siksha.model.Menu
import java.text.SimpleDateFormat
import java.util.*

fun isVacation(today: Date): Boolean {
    val sdf = SimpleDateFormat("yyyy/MM/dd")
    val vacationInterval = arrayListOf(Pair(sdf.parse("2020/03/22"), sdf.parse("2020/03/23")))
    vacationInterval.forEach {
        if (it.first.before(today) && it.second.after(today)) {
            return@isVacation true
        }
    }
    return false
}


fun compareDate(date: String): Boolean {
    return SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time) == date
}

fun getCurrentType(): Menu.Type = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..10 -> Menu.Type.BREAKFAST
    in 11..13 -> Menu.Type.LUNCH
    else -> Menu.Type.DINNER
}

fun formatDate(date: String): String = SimpleDateFormat("MM.dd.E")
        .format(SimpleDateFormat("yyyy-MM-dd").parse(date))
        .format(Locale.KOREA)


class HMRange(
        val start: HMUnit,
        val end: HMUnit
) {
    data class HMUnit(
            val hour: Int,
            val minute: Int
    ) {
        constructor (hourStr: String, minuteStr: String) : this(hourStr.toInt(), minuteStr.toInt())
    }

    fun isContain(target: HMUnit): Boolean {
        return start.hour * 60 + start.minute < target.hour * 60 + target.minute
                && target.hour * 60 + target.minute < end.hour * 60 + end.minute
    }
}

fun isOpenUnit(inputUnit: String): Boolean {
    val regex = """([가-힣][가-힣]) ([0-9][0-9]):([0-9][0-9])-([0-9][0-9]):([0-9][0-9])""".toRegex()

    val calculationTimeFormat = SimpleDateFormat("kmm", Locale.KOREA)
    val currentTime = Calendar.getInstance()
    val currentUnit = HMRange.HMUnit(currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE))


    var weekday: HMRange? = null
    var weekend: HMRange? = null
    var vacation: HMRange? = null

    val regexResult = regex.findAll(inputUnit)

    regexResult.forEach {
        val (type, startH, startM, endH, endM) = it.destructured
        val range = HMRange(HMRange.HMUnit(startH, startM), HMRange.HMUnit(endH, endM))
        when (type) {
            "주말" -> weekend = range
            "주중" -> weekday = range
            "방학" -> vacation = range
        }
    }

    val range: HMRange? = if (isVacation(currentTime.time)) {
        vacation
    } else {
        when (currentTime.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> weekend
            Calendar.SUNDAY -> weekend
            else -> weekday
        }
    }

    range ?: return false
    return range.isContain(currentUnit)
}