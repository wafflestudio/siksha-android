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
import retrofit2.HttpException
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _postUiState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val postUiState: StateFlow<PostUiState> = _postUiState

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
            runCatching {
                val post = communityRepository.getPost(postId)
                if (!post.available) {
                    _postUiState.value = PostUiState.Failed("신고가 누적되어 숨겨진 게시글입니다.")
                    return@runCatching
                }
                _postUiState.value = PostUiState.Success(post)
            }.onFailure { throwable ->
                val errorMessage = (throwable as? HttpException)?.let {
                    when (it.code()) {
                        404 -> "존재하지 않는 글입니다."
                        else -> "게시글을 불러올 수 없습니다."
                    }
                } ?: "게시글을 불러올 수 없습니다."
                _postUiState.value = PostUiState.Failed(errorMessage)
            }
        }
    }

    fun addComment(content: String, isAnonymous: Boolean) {
        if (content.isEmpty()) return
        val post = (postUiState.value as? PostUiState.Success)?.post ?: return
        viewModelScope.launch {
            runCatching {
                communityRepository.addCommentToPost(post.id, content, isAnonymous)
            }.onSuccess {
                _postDetailEvent.emit(PostDetailEvent.AddCommentSuccess)
                refreshPost(post.id)
            }.onFailure {
                _postDetailEvent.emit(PostDetailEvent.AddCommentFailed)
            }
        }
    }

    fun togglePostLike() {
        val post = (postUiState.value as? PostUiState.Success)?.post ?: return
        viewModelScope.launch {
            runCatching {
                val updatedPost = when (post.isLiked) {
                    true -> communityRepository.unlikePost(post.id)
                    false -> communityRepository.likePost(post.id)
                }
                _postUiState.value = PostUiState.Success(updatedPost)
            }.onFailure {
                // TODO: 예외 처리
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

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            runCatching {
                val response = communityRepository.deletePost(postId)
                if (response.isSuccessful) {
                    _postDetailEvent.emit(PostDetailEvent.DeletePostSuccess)
                } else {
                    _postDetailEvent.emit(PostDetailEvent.DeletePostFailed)
                }
            }.onFailure {
                _postDetailEvent.emit(PostDetailEvent.DeletePostFailed)
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            runCatching {
                communityRepository.deleteComment(commentId)
            }.onSuccess {
                _postDetailEvent.emit(PostDetailEvent.DeleteCommentSuccess)

                val post = (postUiState.value as? PostUiState.Success)?.post ?: return@onSuccess
                refreshPost(post.id)
            }.onFailure {
                _postDetailEvent.emit(PostDetailEvent.DeleteCommentFailed)
            }
        }
    }

    fun setIsAnonymous(isAnonymous: Boolean) {
        communityRepository.setIsAnonymous(isAnonymous)
    }
}

sealed interface PostUiState {
    class Success(val post: Post) : PostUiState
    class Failed(val errorMessage: String) : PostUiState
    object Loading : PostUiState
}

sealed interface PostDetailEvent {
    object AddCommentSuccess : PostDetailEvent
    object AddCommentFailed : PostDetailEvent
    object ToggleCommentLikeSuccess : PostDetailEvent
    object ToggleCommentLikeFailed : PostDetailEvent
    object DeletePostSuccess : PostDetailEvent
    object DeletePostFailed : PostDetailEvent
    object DeleteCommentSuccess : PostDetailEvent
    object DeleteCommentFailed : PostDetailEvent
}
