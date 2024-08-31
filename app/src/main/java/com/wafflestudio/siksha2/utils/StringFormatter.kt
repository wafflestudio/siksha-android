package com.wafflestudio.siksha2.utils

import timber.log.Timber
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object StringFormatter {
    fun formatScore(score: Double?): String {
        return (if (score != null) "%.1f".format(score) else "-")
    }
}

fun LocalDate.toKoreanDate(): String {
    return format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
}

fun LocalDate.toISODateString(): String {
    return format(DateTimeFormatter.ISO_DATE)
}

fun LocalDate.toPrettyString(): String {
    val year = year.toString().padStart(4, '0')
    val month = monthValue.toString().padStart(2, '0')
    val day = dayOfMonth.toString().padStart(2, '0')
    val dayOfWeek = when (dayOfWeek!!) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
    }
    return "$year-$month-$day ($dayOfWeek)"
}

fun Long?.toPrettyString(): String {
    return if (this == null) {
        "    -    "
    } else {
        String.format("%,d", this)
    }
}

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
}

fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
}

fun LocalDateTime.toParsedTimeString(): String {
    try {
        val diff = Duration.between(this, LocalDateTime.now())
        val hours = diff.toHours()
        val minutes = diff.toMinutes()
        val days = diff.toDays()
        return when {
            days > 0 -> {
                format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            }
            hours > 0 -> {
                "$hours 시간 전"
            }
            minutes > 0 -> {
                "$minutes 분 전"
            }
            else -> {
                "방금 전"
            }
        }
    } catch (e: DateTimeParseException) {
        Timber.e("LocalDateTime parse error!")
        return ""
    }
}
