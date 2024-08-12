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
import kotlinx.coroutines.flow.asSharedFlow
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

    val commentPagingData = Pager(
        config = PagingConfig(
            pageSize = CommentPagingSource.ITEMS_PER_PAGE
        ),
        pagingSourceFactory = {
            communityRepository.commentPagingSource(
                PostDetailFragmentArgs.fromSavedStateHandle(
                    savedStateHandle
                ).postId
            )
        }
    ).flow
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    private val _postDetailEvent = MutableSharedFlow<PostDetailEvent>()
    val postDetailEvent = _postDetailEvent.asSharedFlow()

    val isAnonymous = communityRepository.isAnonymous.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        refreshPost(PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId)
    }

    private fun refreshPost(postId: Long) {
        viewModelScope.launch {
            _post.value = communityRepository.getPost(postId)
        }
    }

    fun addComment(content: String, isAnonymous: Boolean) {
        if (content.isEmpty()) return
        viewModelScope.launch {
            runCatching {
                communityRepository.addCommentToPost(_post.value.id, content, isAnonymous)
            }.onSuccess {
                _postDetailEvent.emit(PostDetailEvent.AddCommentSuccess)
                refreshPost(post.value.id)
            }.onFailure {
                _postDetailEvent.emit(PostDetailEvent.AddCommentFailed)
            }
        }
    }

    fun togglePostLike() {
        viewModelScope.launch {
            runCatching {
                when (post.value.isLiked) {
                    true -> _post.value = communityRepository.unlikePost(post.value.id)
                    false -> _post.value = communityRepository.likePost(post.value.id)
                }
            }
        }
    }

    fun toggleCommentLike(comment: Comment) {
        viewModelScope.launch {
            runCatching {
                when (comment.isLiked) {
                    true -> communityRepository.unlikeComment(comment.id)
                    false -> communityRepository.likeComment(comment.id)
                }
            }.onSuccess {
                _postDetailEvent.emit(PostDetailEvent.ToggleCommentLikeSuccess)
            }.onFailure {
                _postDetailEvent.emit(PostDetailEvent.ToggleCommentLikeFailed)
            }
        }
    }

    fun setIsAnonymous(isAnonymous: Boolean) {
        communityRepository.setIsAnonymous(isAnonymous)
    }
}

sealed interface PostDetailEvent {
    object AddCommentSuccess : PostDetailEvent
    object AddCommentFailed : PostDetailEvent
    object ToggleCommentLikeSuccess : PostDetailEvent
    object ToggleCommentLikeFailed : PostDetailEvent
}
