package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.repositories.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _postReportEvent = MutableSharedFlow<PostReportEvent>()
    val postReportEvent = _postReportEvent.asSharedFlow()

    val postId = PostDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).postId

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
