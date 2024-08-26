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
class CommentReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _commentReportEvent = MutableSharedFlow<CommentReportEvent>()
    val commentReportEvent = _commentReportEvent.asSharedFlow()

    val commentId = CommentReportFragmentArgs.fromSavedStateHandle(savedStateHandle).commentId

    fun reportComment(reportContent: String) {
        viewModelScope.launch {
            runCatching {
                communityRepository.reportComment(commentId, reportContent)
            }.onSuccess {
                _commentReportEvent.emit(CommentReportEvent.ReportCommentSuccess)
            }.onFailure {
                _commentReportEvent.emit(CommentReportEvent.ReportCommentFailed)
            }
        }
    }
}

sealed interface CommentReportEvent {
    object ReportCommentSuccess : CommentReportEvent
    object ReportCommentFailed : CommentReportEvent
}
