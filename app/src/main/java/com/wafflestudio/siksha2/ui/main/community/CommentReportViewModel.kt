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
            runCatching {
                _user.value = userStatusManager.getUserData()
            }.onFailure {
                // TODO: 예외 대응 필요
            }
        }
    }

    fun reportComment(reportContent: String) {
        viewModelScope.launch {
            runCatching {
                communityRepository.reportComment(commentId, reportContent)
            }.onSuccess {
                _commentReportEvent.emit(CommentReportEvent.ReportCommentSuccess)
            }.onFailure { throwable ->
                when (throwable) {
                    is HttpException -> {
                        when (throwable.code()) {
                            409 -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("이미 신고한 게시글입니다."))
                            else -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("알 수 없는 오류입니다."))
                        }
                    }
                    else -> _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed("알 수 없는 오류입니다."))
                }
            }
        }
    }
}

sealed interface CommentReportEvent {
    object ReportCommentSuccess : CommentReportEvent
    class ReportCommentFailed(val errorMessage: String) : CommentReportEvent
}
