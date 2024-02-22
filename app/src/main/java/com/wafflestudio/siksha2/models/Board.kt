package com.wafflestudio.siksha2.models

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Board(
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val id: Long = 0L,
    val type: Long = 0L,
    val name: String = "",
    val description: String = ""
) {
    companion object {
        val Empty = Board()
    }
}
