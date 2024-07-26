package com.wafflestudio.siksha2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.Etc
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.utils.toLocalDateTime

@JsonClass(generateAdapter = true)
data class PostDto(
    @Json(name = "board_id") val boardId: Long = 0L,
    @Json(name = "title") val title: String = "",
    @Json(name = "content") val content: String = "",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "nickname") val nickname: String? = "",
    @Json(name = "available") val available: Boolean = true,
    @Json(name = "anonymous") val anonymous: Boolean = true,
    @Json(name = "is_mine") val isMine: Boolean = false,
    @Json(name = "etc") val etc: Etc? = null,
    @Json(name = "like_cnt") val likeCount: Long = 0L,
    @Json(name = "comment_cnt") val commentCount: Long = 0L,
    @Json(name = "is_liked") val isLiked: Boolean = false
) {
    fun toPost(): Post = Post(
        boardId = boardId,
        title = title,
        content = content,
        createdAt = createdAt.toLocalDateTime(),
        updatedAt = updatedAt.toLocalDateTime(),
        id = id,
        nickname = nickname ?: "익명",
        available = available,
        anonymous = anonymous,
        isMine = isMine,
        etc = etc,
        likeCount = likeCount,
        commentCount = commentCount,
        isLiked = isLiked
    )
}
