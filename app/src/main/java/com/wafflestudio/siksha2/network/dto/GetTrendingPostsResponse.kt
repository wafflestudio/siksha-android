package com.wafflestudio.siksha2.network.dto

import com.wafflestudio.siksha2.network.dto.core.PostDto

data class GetTrendingPostsResponse(
    val result: List<PostDto>
)
