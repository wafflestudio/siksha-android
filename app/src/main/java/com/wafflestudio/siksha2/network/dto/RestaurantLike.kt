package com.wafflestudio.siksha2.network.dto

data class RestaurantLikeRequest(
    val like: Boolean
)

data class RestaurantLikeResponse(
    val id: Long,
    val liked: Boolean
)
