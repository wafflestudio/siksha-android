package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.PostCommentRequestBody
import com.wafflestudio.siksha2.repositories.pagingsource.CommentPagingSource
import com.wafflestudio.siksha2.repositories.pagingsource.PostPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val api: SikshaApi
) {
    suspend fun getBoards(): List<Board> {
        return api.getBoards().map { it.toBoard() }
    }

    fun postPagingSource(boardId: Long) = PostPagingSource(boardId, api)

    suspend fun getPost(postId: Long): Post {
        return api.getPost(postId).toPost()
    }

    fun commentPagingSource(postId: Long) = CommentPagingSource(postId, api)

    suspend fun addCommentToPost(postId: Long, content: String) {
        api.postComment(PostCommentRequestBody(postId, content))
    }

    suspend fun likePost(postId: Long): Post {
        return api.postLikePost(postId).toPost()
    }

    suspend fun unlikePost(postId: Long): Post {
        return api.postUnlikePost(postId).toPost()
    }
}
