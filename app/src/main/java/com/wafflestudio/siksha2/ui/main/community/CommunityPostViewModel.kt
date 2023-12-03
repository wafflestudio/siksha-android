package com.wafflestudio.siksha2.ui.main.community

import androidx.lifecycle.ViewModel
import com.wafflestudio.siksha2.models.Post
import com.wafflestudio.siksha2.repositories.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityPostViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _post = MutableStateFlow<Post>(Post())
    val post: StateFlow<Post> = _post

    suspend fun initialize(postId: Long) {
        _post.value = communityRepository.getPost(postId)
    }
}
