package com.wafflestudio.siksha2.models

import java.time.LocalDateTime

data class Post(
    val boardId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val id: Long = 0L,
    val nickname: String = "",
    val available: Boolean = true,
    val etc: Etc? = null,
    val likeCount: Long = 0L,
    val commentCount: Long = 0L,
    val isLiked: Boolean = false
)
