package com.wafflestudio.siksha.util

import com.wafflestudio.siksha.model.Menu
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun compareDate(date: String): Boolean {
    val calendar = Calendar.getInstance()
    val df = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    Timber.d("COMPARE DATE $df $date")
    return date == df
}

fun getCurrentType(): Menu.Type {
    val calendar = Calendar.getInstance()
    Timber.d("CALENDAR ${calendar.get(Calendar.HOUR_OF_DAY)}")
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..10 -> Menu.Type.BREAKFAST
        in 11..13 -> Menu.Type.LUNCH
        else -> Menu.Type.DINNER
    }
}
