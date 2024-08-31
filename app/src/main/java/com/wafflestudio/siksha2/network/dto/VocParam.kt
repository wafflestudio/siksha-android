package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VocParam(
    @Json(name = "voc") val voc: String,
    @Json(name = "platform") val platform: String
)
