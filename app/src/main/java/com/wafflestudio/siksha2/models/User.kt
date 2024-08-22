package com.wafflestudio.siksha2.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    var id: Long,
    var nickname: String,
    var profileUrl: String?
)
