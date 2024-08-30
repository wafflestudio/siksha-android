package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.PostCommentRequestBody
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.repositories.pagingsource.CommentPagingSource
import com.wafflestudio.siksha2.repositories.pagingsource.PostPagingSource
import com.wafflestudio.siksha2.repositories.pagingsource.UserPostPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val api: SikshaApi,
    private val sikshaPrefObjects: SikshaPrefObjects
) {
    val isAnonymous = sikshaPrefObjects.communityIsAnonymous.asFlow()

    suspend fun getBoards(): List<Board> {
        return api.getBoards().map { it.toBoard() }
    }

    suspend fun getBoard(boardId: Long): Board {
        return api.getBoard(boardId).toBoard()
    }

    fun getUserPostPagingSource() = UserPostPagingSource(api)
    fun getPostPagingSource(boardId: Long) = PostPagingSource(boardId, api)

    suspend fun getPost(postId: Long): Post {
        return api.getPost(postId).toPost()
    }

    fun commentPagingSource(postId: Long) = CommentPagingSource(postId, api)

    suspend fun addCommentToPost(postId: Long, content: String, isAnonymous: Boolean) {
        api.postComment(PostCommentRequestBody(postId, content, isAnonymous))
    }

    suspend fun likePost(postId: Long): Post {
        return api.postLikePost(postId).toPost()
    }

    suspend fun unlikePost(postId: Long): Post {
        return api.postUnlikePost(postId).toPost()
    }

    suspend fun likeComment(commentId: Long) {
        api.postLikeComment(commentId)
    }

    suspend fun unlikeComment(commentId: Long) {
        api.postUnlikeComment(commentId)
    }

    suspend fun getTrendingPosts(): List<Post> {
        return withContext(Dispatchers.IO) {
            api.getTrendingPosts().result.map {
                it.toPost()
            }
        }
    }

    fun setIsAnonymous(isAnonymous: Boolean) {
        sikshaPrefObjects.communityIsAnonymous.setValue(isAnonymous)
    }
}
