package com.wafflestudio.siksha2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.KeywordDist

@JsonClass(generateAdapter = true)
data class FetchKeywordDistResult(
    @Json(name = "taste_keyword") val tasteKeyword: String,
    @Json(name = "taste_cnt") val tasteCnt: Long,
    @Json(name = "price_keyword") val priceKeyword: String,
    @Json(name = "price_cnt") val priceCnt: Long,
    @Json(name = "food_composition_keyword") val foodCompositionKeyword: String,
    @Json(name = "food_composition_cnt") val foodCompositionCnt: Long
) {
    fun toKeywordDist(): KeywordDist = KeywordDist(
        listOf(tasteKeyword, priceKeyword, foodCompositionKeyword),
        listOf(tasteCnt, priceCnt, foodCompositionCnt)
    )
}
