package com.wafflestudio.siksha2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Board(
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    val id: Long,
    val type: Long,
    val name: String,
    val description: String
)
