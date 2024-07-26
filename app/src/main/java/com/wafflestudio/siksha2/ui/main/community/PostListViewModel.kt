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
import com.wafflestudio.siksha2.repositories.pagingsource.PostPagingSource.Companion.ITEMS_PER_PAGE
import com.wafflestudio.siksha2.utils.Selectable
import com.wafflestudio.siksha2.utils.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostListViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _boards = MutableStateFlow<List<Selectable<Board>>>(emptyList())
    val boards: StateFlow<List<Selectable<Board>>> get() = _boards

    val selectedBoard = _boards.map { list ->
        list.find { it.state }?.data
    }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Board.Empty)

    private val _postPagingData = selectedBoard.flatMapLatest { board ->
        Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { communityRepository.postPagingSource(board.id) }
        ).flow.cachedIn(viewModelScope)
    }
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

    suspend fun getBoards() {
        try {
            _boards.value = communityRepository.getBoards().map { board ->
                board.toDataWithState(false)
            }
            selectBoard(0)
        } catch (e: IOException) {
            // TODO: error handler
        }
    }

    fun selectBoard(boardIndex: Int) {
        _boards.value = _boards.value.mapIndexed { idx, board ->
            board.data.toDataWithState(idx == boardIndex)
        }
    }

    /**
     * 데이터셋 전체를 invalidate하는 대신, 변화가 생긴 게시글을 map으로 캐시한다. UI에서 게시글 목록 조회 시 반영된다.
     */
    fun updateListWithLikedPost(post: Post) {
        val modifiedPost = post.copy(
            isLiked = post.isLiked.not(),
            likeCount = if (post.isLiked) post.likeCount - 1 else post.likeCount + 1
        )
        modifiedPostsCache.value = modifiedPostsCache.value.toMutableMap().apply {
            put(post.id, modifiedPost)
        }
    }

    fun updateListWithCommentAddedPost(post: Post) {
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
