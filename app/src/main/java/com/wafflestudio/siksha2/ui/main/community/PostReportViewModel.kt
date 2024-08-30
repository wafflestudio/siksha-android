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
import javax.inject.Inject

@HiltViewModel
class PostReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository,
    private val userStatusManager: UserStatusManager,
) : ViewModel() {

    private val _postReportEvent = MutableSharedFlow<PostReportEvent>()
    val postReportEvent = _postReportEvent.asSharedFlow()

    val postId = PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId

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
            }.onFailure {
                _postReportEvent.emit(PostReportEvent.ReportPostFailed)
            }
        }
    }
}

sealed interface PostReportEvent {
    object ReportPostSuccess : PostReportEvent
    object ReportPostFailed : PostReportEvent
}
