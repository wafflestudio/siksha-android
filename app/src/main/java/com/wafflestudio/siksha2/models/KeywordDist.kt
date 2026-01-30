package com.wafflestudio.siksha2.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KeywordDist(
    val keywords: List<String>,
    val keywordCounts: List<Long>,
    val keywordTotals: List<Long>
) {
    companion object {
        val Empty = KeywordDist(listOf(), listOf(), listOf())
    }
}
