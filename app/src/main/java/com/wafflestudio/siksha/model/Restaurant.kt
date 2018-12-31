package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class Restaurant(
        @Json(name = "id") val id: Int,
        @Json(name = "en_name") val enName: String,
        @Json(name = "kr_name") val krName: String,
        @Json(name = "operating_hours") val operatingHours: String,
        @Json(name = "hours_breakfast") val hoursBreakfast: String,
        @Json(name = "hours_lunch") val hoursLunch: String,
        @Json(name = "hours_dinner") val hoursDinner: String,
        @Json(name = "location") val location: String,
        @Json(name = "latitude") val latitude: Double,
        @Json(name = "longitude") val longitude: Double
)