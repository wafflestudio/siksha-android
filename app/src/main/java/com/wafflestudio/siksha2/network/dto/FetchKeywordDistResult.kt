package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FetchKeywordDistResult(
    @Json(name = "taste_keyword") val tasteKeyword: String,
    @Json(name = "taste_cnt") val tasteCnt: String,
    @Json(name = "price_keyword") val priceKeyword: String,
    @Json(name = "price_cnt") val priceCnt: String,
    @Json(name = "food_composition_keyword") val foodCompositionKeyword: String,
    @Json(name = "food_composition_cnt") val foodCompositionCnt: String
)
