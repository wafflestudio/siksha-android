package com.wafflestudio.siksha2.network.dto.core

import com.squareup.moshi.JsonClass
import com.wafflestudio.siksha2.models.Comment
import com.wafflestudio.siksha2.utils.toLocalDateTime

@JsonClass(generateAdapter = true)
data class CommentDto(
    val post_id: Long = 0L,
    val content: String = "",
    val created_at: String = "",
    val updated_at: String = "",
    val id: Long = 0L,
    val nickname: String = "",
    val available: Boolean = true,
    val like_cnt: Long = 0L,
    val is_liked: Boolean = true
) {
    fun toComment(): Comment = Comment(
        postId = post_id,
        content = content,
        createdAt = created_at.toLocalDateTime(),
        updatedAt = updated_at.toLocalDateTime(),
        id = id,
        nickname = nickname,
        available = available,
        likeCount = like_cnt,
        isLiked = is_liked
    )
}
