package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostCommentRequestBody(
    @Json(name = "post_id") val postId: Long,
    val content: String
)
