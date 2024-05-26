package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wafflestudio.siksha2.models.Comment
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

    fun addComment(content: String, isAnonymous: Boolean) {
        if (content.isEmpty()) return
        viewModelScope.launch {
            runCatching {
                communityRepository.addCommentToPost(_post.value.id, content, isAnonymous)
                _loadCommentSignal.emit(Unit)
            } // TODO: error handling
        }
    }

    fun togglePostLike() {
        viewModelScope.launch {
            when (post.value.isLiked) {
                true -> _post.value = communityRepository.unlikePost(post.value.id)
                false -> _post.value = communityRepository.likePost(post.value.id)
            }
        }
    }

    fun toggleCommentLike(comment: Comment) {
        viewModelScope.launch {
            when (comment.isLiked) {
                true -> communityRepository.unlikeComment(comment.id)
                false -> communityRepository.likeComment(comment.id)
            }
            _loadCommentSignal.emit(Unit)
        }
    }
}
