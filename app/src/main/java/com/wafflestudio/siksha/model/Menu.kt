package com.wafflestudio.siksha.model

import com.squareup.moshi.Json

data class Menu(
        @Json(name = "id") val id: Int,
        @Json(name = "restaurant") val restaurant: Restaurant,
        @Json(name = "meals") val meals: List<Meal>
)
