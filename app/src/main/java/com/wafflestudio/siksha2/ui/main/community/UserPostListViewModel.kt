package com.wafflestudio.siksha2.ui.main.community

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.siksha2.models.Board
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.repositories.pagingsource.PostPagingSource
import com.wafflestudio.siksha2.utils.Selectable
import com.wafflestudio.siksha2.utils.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserPostListViewModel@Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {
    private val _boards = MutableStateFlow<List<Selectable<Board>>>(emptyList())

    private val _postPagingData = Pager(
            config = PagingConfig(
                pageSize = PostPagingSource.ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { communityRepository.getUserPostPagingSource() }
        ).flow.cachedIn(viewModelScope)

    private val modifiedPostsCache = MutableStateFlow(mapOf<Long, Post>())

    val postPagingData =
        combine(_postPagingData, modifiedPostsCache) { pagingData, modifiedPosts ->
            pagingData.map { post ->
                modifiedPosts[post.id] ?: post
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    val postListState = LazyListState(
        firstVisibleItemIndex = 0,
        firstVisibleItemScrollOffset = 0
    )

    init {
        viewModelScope.launch {
            getBoards()
        }
    }

    private suspend fun getBoards() {
        try {
            // Fetch the boards and map them with the state
            _boards.value = communityRepository.getBoards().mapIndexed { index, board ->
                board.toDataWithState(index == 0) // Always select the first board
            }
        } catch (e: IOException) {
            // TODO: error handler
        }
    }

    fun updateUserListWithLikedPost(post: Post) {
        val modifiedPost = post.copy(
            isLiked = post.isLiked.not(),
            likeCount = if (post.isLiked) post.likeCount - 1 else post.likeCount + 1
        )
        modifiedPostsCache.value = modifiedPostsCache.value.toMutableMap().apply {
            put(post.id, modifiedPost)
        }
    }

    fun updateUserListWithCommentAddedPost(post: Post) {
        val modifiedPost = post.copy(
            commentCount = post.commentCount + 1
        )
        modifiedPostsCache.value = modifiedPostsCache.value.toMutableMap().apply {
            put(post.id, modifiedPost)
        }
    }

    fun invalidateCache() {
        modifiedPostsCache.value = emptyMap()
    }

}
