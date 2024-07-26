package com.wafflestudio.siksha2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.utils.toLocalDateTime

@JsonClass(generateAdapter = true)
data class BoardDto(
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    val id: Long = 0L,
    val type: Long = 0L,
    val name: String = "",
    val description: String = ""
) {
    fun toBoard(): Board = Board(
        createdAt = createdAt.toLocalDateTime(),
        updatedAt = updatedAt.toLocalDateTime(),
        id = id,
        type = type,
        name = name,
        description = description
    )
}
