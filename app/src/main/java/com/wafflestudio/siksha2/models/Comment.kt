package com.wafflestudio.siksha2.models

import java.time.LocalDateTime

data class Comment(
    val postId: Long = 0L,
    val content: String = "",
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val id: Long = 0L,
    val nickname: String = "",
    val available: Boolean = true,
    val likeCount: Long = 0L,
    val isLiked: Boolean = true
)
