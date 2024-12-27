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
            when (val response = userStatusManager.getUserData()) {
                is NetworkResult.Success -> _user.value = response.body
                is NetworkResult.Failure -> _postReportEvent.emit(PostReportEvent.ReportPostFailed(response.message))
                is NetworkResult.NetworkError -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("네트워크 연결이 불안정합니다."))
                else -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("알 수 없는 오류가 발생했습니다."))
            }
        }
    }

    fun reportPost(reportContent: String) {
        viewModelScope.launch {
            when (val response = communityRepository.reportPost(postId, reportContent)) {
                is NetworkResult.Success -> _postReportEvent.emit(PostReportEvent.ReportPostSuccess)
                is NetworkResult.Failure -> _postReportEvent.emit(PostReportEvent.ReportPostFailed(response.message))
                is NetworkResult.NetworkError -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("네트워크 연결이 불안정합니다."))
                else -> _postReportEvent.emit(PostReportEvent.ReportPostFailed("알 수 없는 오류가 발생했습니다."))
            }
        }
    }
}

sealed interface PostReportEvent {
    object ReportPostSuccess : PostReportEvent
    class ReportPostFailed(val errorMessage: String) : PostReportEvent
}
