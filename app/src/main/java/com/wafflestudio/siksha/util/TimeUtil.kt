package com.wafflestudio.siksha.util

import com.wafflestudio.siksha.model.Menu
import java.text.SimpleDateFormat
import java.util.*

fun compareDate(date: String): Boolean {
    return SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time) == date
}

fun getCurrentType(): Menu.Type = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..10 -> Menu.Type.BREAKFAST
    in 11..13 -> Menu.Type.LUNCH
    else -> Menu.Type.DINNER
}
