package com.wafflestudio.siksha2.network.dto

import com.google.gson.annotations.JsonAdapter
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportCommentRequestBody(
    val reason: String
)

