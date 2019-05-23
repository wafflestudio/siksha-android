package com.wafflestudio.siksha.model

import com.squareup.moshi.Json
import com.wafflestudio.siksha.util.isOpenUnit

data class Restaurant(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "code") val code: String,
    @field:Json(name = "en_name") val enName: String,
    @field:Json(name = "kr_name") val krName: String,
    @field:Json(name = "operating_hours") val operatingHours: String,
    @field:Json(name = "hours_breakfast") val hoursBreakfast: String,
    @field:Json(name = "hours_lunch") val hoursLunch: String,
    @field:Json(name = "hours_dinner") val hoursDinner: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "latitude") val latitude: Double,
    @field:Json(name = "longitude") val longitude: Double,

    var favorite: Boolean = false
) {
  val isOpen: Boolean
    get() = isOpenUnit(hoursBreakfast) || isOpenUnit(hoursLunch) || isOpenUnit(hoursDinner)
}