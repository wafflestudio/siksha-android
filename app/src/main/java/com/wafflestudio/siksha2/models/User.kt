package com.wafflestudio.siksha2.models

import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.network.dto.core.UserDto

@JsonClass(generateAdapter = true)
data class User(
    var id: Long,
    var nickname: String,
    var profileUrl: String?
) {
    companion object {
        val Empty = User(0L, "", null)
    }
}

fun UserDto.toUser() = User(id = id, nickname = nickname, profileUrl = profileUrl)
