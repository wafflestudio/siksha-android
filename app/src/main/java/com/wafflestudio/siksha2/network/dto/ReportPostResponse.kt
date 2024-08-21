package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportPostResponse(
    val id: Long,
    val reason: String,
    val postId: Long
)
