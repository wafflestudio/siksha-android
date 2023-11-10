package com.wafflestudio.siksha2.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "board_id") val boardId: Long = 0L,
    val title: String = "",
    val content: String = "",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    val id: Long = 0L,
    @Json(name = "user_id") val userId: String = "",
    val available: Boolean = true,
    val etc: Etc? = null, // TODO: 맞는지 확인 필요
    @Json(name = "like_cnt") val likeCount: Long = 0L,
    @Json(name = "comment_cnt") val commentCount: Long = 0L,
    @Json(name = "is_liked") val isLiked: Boolean = false
)
