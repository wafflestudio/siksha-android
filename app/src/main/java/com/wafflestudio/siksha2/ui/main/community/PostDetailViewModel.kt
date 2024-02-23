package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.repositories.pagingsource.CommentPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _post = MutableStateFlow<Post>(Post())
    val post: StateFlow<Post> = _post

    private val _loadCommentSignal = MutableSharedFlow<Unit>()
    val commentPagingData = _loadCommentSignal.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = CommentPagingSource.ITEMS_PER_PAGE
            ),
            pagingSourceFactory = { communityRepository.commentPagingSource(_post.value.id) }
        ).flow.cachedIn(viewModelScope)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    init {
        viewModelScope.launch {
            _post.value = communityRepository.getPost(
                PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId
            )
            _loadCommentSignal.emit(Unit)
        }
    }

    suspend fun addComment(content: String) {
        communityRepository.addCommentToPost(_post.value.id, content)
    }

    suspend fun likePost() {
        _post.value = communityRepository.likePost(_post.value.id)
    }

    suspend fun unlikePost() {
        _post.value = communityRepository.unlikePost(_post.value.id)
    }

    suspend fun likeComment(commentId: Long) {
        communityRepository.likeComment(commentId)
    }

    suspend fun unlikeComment(commentId: Long) {
        communityRepository.unlikeComment(commentId)
    }
}
