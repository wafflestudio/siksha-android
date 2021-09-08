package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetVersionResult(
    @Json(name = "version") val version: String,
    @Json(name = "minimum_version") val minVersion: String
)
