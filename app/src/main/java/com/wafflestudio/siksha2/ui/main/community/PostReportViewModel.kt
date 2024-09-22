package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.User
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.repositories.UserStatusManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PostReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository,
    private val userStatusManager: UserStatusManager
) : ViewModel() {

    private val _postReportEvent = MutableSharedFlow<PostReportEvent>()
    val postReportEvent = _postReportEvent.asSharedFlow()

    private val postId = PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId

    private val _user = MutableStateFlow(User.Empty)
    val user: StateFlow<User> = _user

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            runCatching {
                _user.value = userStatusManager.getUserData()
            }.onFailure {
                // TODO: 예외 대응 필요
            }
        }
    }

    fun reportPost(reportContent: String) {
        viewModelScope.launch {
            runCatching {
                communityRepository.reportPost(postId, reportContent)
            }.onSuccess {
                _postReportEvent.emit(PostReportEvent.ReportPostSuccess)
            }.onFailure { throwable ->
                when (throwable) {
                    is HttpException -> {
                        when (throwable.code()) {
                            409 -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("이미 신고한 게시글입니다."))
                            else -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("알 수 없는 오류입니다."))
                        }
                    }
                    else -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("알 수 없는 오류입니다."))
                }
            }
        }
    }
}

sealed interface PostReportEvent {
    object ReportPostSuccess : PostReportEvent
    class ReportPostFailed(val errorMessage: String) : PostReportEvent
}
