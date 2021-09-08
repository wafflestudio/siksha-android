package com.wafflestudio.siksha2.utils

fun String.hasFinalConsInKr(): Boolean {
    val last = this[length - 1].toInt()
    return if (last < 0xAC00 || last > 0xD7A3) false
    else (last - 0xAC00) % 28 > 0
}
