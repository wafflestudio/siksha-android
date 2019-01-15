package com.wafflestudio.siksha.util

import com.wafflestudio.siksha.model.Menu
import com.wafflestudio.siksha.model.Restaurant
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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

fun isOpenUnit(inputUnit: String): Boolean {
    val openRegex = "오. [0-9]+시( [0-9]+분)? - 오. [0-9]+시( [0-9]+분)?"
    val calculationTimeFormat = SimpleDateFormat("kmm", Locale.KOREA)
    val currentTime = Date()
    val calculationCurrentTime = calculationTimeFormat.format(currentTime)
    val integerCurrentTime = Integer.parseInt(calculationCurrentTime)
    val openPattern = Pattern.compile(openRegex)
    val openMatch = openPattern.matcher(inputUnit)
    val numExtractPattern = Pattern.compile("[0-9]+")
    val timeInterval = intArrayOf(2400, 0)
    while (openMatch.find()) {
        val raw = openMatch.group()
        val parts = raw.split(" - ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        parts.forEachIndexed { index, string ->
            val numMatch = numExtractPattern.matcher(string)
            numMatch.find()
            var res = numMatch.group()
            if (string.contains("오후")) {
                val integerRes = Integer.parseInt(res) + 12
                res = integerRes.toString()
            }
            res += if (string.contains("분")) {
                numMatch.find()
                numMatch.group()
            } else "00"
            val resInt = Integer.parseInt(res)
            timeInterval[index] = when (index) {
                0 -> if (resInt < timeInterval[index]) resInt else timeInterval[index]
                1 -> if (timeInterval[index] < resInt) resInt else timeInterval[index]
                else -> timeInterval[index]
            }
        }
    }
    return timeInterval[0] <= integerCurrentTime && integerCurrentTime <= timeInterval[1]
}