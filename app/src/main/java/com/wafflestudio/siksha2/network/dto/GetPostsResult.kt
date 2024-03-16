package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.network.dto.core.PostDto

@JsonClass(generateAdapter = true)
data class GetPostsResult(
    val result: List<PostDto>,
    @Json(name = "total_count") val totalCount: Long,
    @Json(name = "has_next") val hasNext: Boolean
)
