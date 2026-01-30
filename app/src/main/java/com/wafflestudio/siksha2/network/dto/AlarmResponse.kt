package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmResponse(
    val created_at: String,
    val updated_at: String,
    val id: Long,
    val restaurant_id: Long,
    val code: String?,
    val date: String?,
    val type: String?,
    val name_kr: String,
    val name_en: String?,
    val price: Int?,
    val etc: List<String>?,
    val is_liked: Boolean,
    val alarm: Boolean
)
