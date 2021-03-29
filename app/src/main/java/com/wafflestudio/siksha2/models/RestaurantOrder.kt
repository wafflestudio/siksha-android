package com.wafflestudio.siksha2.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantOrder(
    val order: List<Long>
)
