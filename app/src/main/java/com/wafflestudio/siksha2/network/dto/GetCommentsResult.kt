package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.network.dto.core.CommentDto

@JsonClass(generateAdapter = true)
data class GetCommentsResult(
    val result: List<CommentDto>,
    @Json(name = "total_count") val totalCount: Long,
    @Json(name = "has_next") val hasNext: Boolean
)
