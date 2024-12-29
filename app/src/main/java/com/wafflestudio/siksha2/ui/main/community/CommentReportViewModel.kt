package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.User
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.CommunityRepository
import com.wafflestudio.siksha2.repositories.UserStatusManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository,
    private val userStatusManager: UserStatusManager
) : ViewModel() {

    private val _commentReportEvent = MutableSharedFlow<CommentReportEvent>()
    val commentReportEvent = _commentReportEvent.asSharedFlow()

    private val commentId = CommentReportFragmentArgs.fromSavedStateHandle(savedStateHandle).commentId

    private val _user = MutableStateFlow(User.Empty)
    val user: StateFlow<User> = _user

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            when (val response = userStatusManager.getUserData()) {
                is NetworkResult.Success -> {
                    _user.value = response.body
                }
                is NetworkResult.Failure -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed(response.message))
                is NetworkResult.NetworkError -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("네트워크 연결이 불안정합니다."))
                else -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("알 수 없는 오류가 발생했습니다."))
            }
        }
    }

    fun reportComment(reportContent: String) {
        viewModelScope.launch {
            when (val response = communityRepository.reportComment(commentId, reportContent)) {
                is NetworkResult.Success -> _commentReportEvent.emit(CommentReportEvent.ReportCommentSuccess)
                is NetworkResult.Failure -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed(response.message))
                is NetworkResult.NetworkError -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("네트워크 연결이 불안정합니다."))
                else -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("알 수 없는 오류가 발생했습니다."))
            }
        }
    }
}

sealed interface CommentReportEvent {
    object ReportCommentSuccess : CommentReportEvent
    class ReportCommentFailed(val errorMessage: String) : CommentReportEvent
}
