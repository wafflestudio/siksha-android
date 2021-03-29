package com.wafflestudio.siksha2.network.dto

import com.wafflestudio.siksha2.models.RestaurantInfo

data class FetchRestaurantsResult(
    val count: Long,
    val result: List<RestaurantInfo>
)
