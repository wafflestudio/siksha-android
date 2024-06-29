package com.wafflestudio.siksha2.network.dto.core

import com.squareup.moshi.JsonClass

// TODO: error dto 형식 서버와 확정
/*@JsonClass(generateAdapter = true)
data class ErrorDto(
    @Json(name = "detail") val details: List<ErrorDetail>,
    val body: String?,
    val message: String,
)

data class ErrorDetail(
    val type: String,
    val location: List<String>,
    val msg: String,
    val input: String?,
    val url: String,
)*/

@JsonClass(generateAdapter = true)
data class ErrorDto(
    val detail: String?
)
