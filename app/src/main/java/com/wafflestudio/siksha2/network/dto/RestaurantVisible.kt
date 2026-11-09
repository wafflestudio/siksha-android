package com.wafflestudio.siksha2.network.dto

data class RestaurantVisibleRequest(
    val visible: Boolean
)

data class RestaurantVisibleResponse(
    val id: Long,
    val visible: Boolean
)
