package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveReviewResult(
    val id: Long,
    val code: String,
    val name_kr: String?,
    val name_en: String?,
    val price: Long?,
    val etc: List<String?>,
    val score: Double?,
    val review_cnt: Long
)
